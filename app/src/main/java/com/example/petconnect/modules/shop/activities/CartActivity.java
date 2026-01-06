package com.example.petconnect.modules.shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.petconnect.R;
import com.example.petconnect.modules.shop.adapters.CartAdapter;
import com.example.petconnect.modules.shop.models.Order;
import com.example.petconnect.modules.shop.services.CartService;
import com.example.petconnect.modules.shop.services.OrderService;
import com.example.petconnect.modules.shop.services.PaymentService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private TextView tvTotal;
    private CartAdapter cartAdapter;
    private CartService cartService;
    private PaymentService paymentService;
    private OrderService orderService;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialiser Firebase
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            userId = "local_user";
        }

        // Initialiser les services
        cartService = CartService.getInstance();
        paymentService = new PaymentService();
        orderService = new OrderService(userId);

        // Initialiser les vues
        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        tvTotal = findViewById(R.id.tv_total);

        Button btnPay = findViewById(R.id.btn_pay);
        Button btnInvoice = findViewById(R.id.btn_invoice);

        // Configurer RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(new ArrayList<>(), new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(CartService.CartItem item, int newQuantity) {
                cartService.updateQuantity(item.product.getId(), newQuantity, new CartService.CartCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            loadCartItems();
                            Log.d("CartActivity", "Quantité mise à jour: " + newQuantity);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Log.e("CartActivity", "Erreur mise à jour quantité: " + error);
                            Toast.makeText(CartActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }

            @Override
            public void onItemRemoved(CartService.CartItem item) {
                cartService.removeFromCart(item.product.getId(), new CartService.CartCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            loadCartItems();
                            Toast.makeText(CartActivity.this, "Produit supprimé du panier", Toast.LENGTH_SHORT).show();
                            Log.d("CartActivity", "Produit supprimé du panier");
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Log.e("CartActivity", "Erreur suppression: " + error);
                            Toast.makeText(CartActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }

            @Override
            public void onCartUpdated() {
                updateTotal();
            }
        });
        cartRecyclerView.setAdapter(cartAdapter);

        loadCartItems();
        btnPay.setOnClickListener(v -> processPayment());
        btnInvoice.setOnClickListener(v -> showInvoice());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    private void loadCartItems() {
        cartService.loadFromFirebase(new CartService.CartCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    List<CartService.CartItem> items = cartService.getItems();
                    Log.d("CartActivity", "Chargement réussi: " + items.size() + " items");
                    cartAdapter.updateItems(new ArrayList<>(items));
                    updateTotal();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("CartActivity", "Erreur chargement panier: " + error);
                    Toast.makeText(CartActivity.this, "Erreur chargement panier: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateTotal() {
        double total = cartService.getTotal();
        tvTotal.setText(String.format("%.2f €", total));
    }

    private void processPayment() {
        cartService.loadFromFirebase(new CartService.CartCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    List<CartService.CartItem> items = cartService.getItems();
                    if (items.isEmpty()) {
                        Toast.makeText(CartActivity.this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double total = cartService.getTotal();
                    String orderNumber = "ORD_" + System.currentTimeMillis();
                    Order order = new Order(orderNumber, userId, items);
                    order.setTotal(total);
                    order.setStatus("pending");
                    order.setPaid(false);

                    saveOrderToFirebase(order);
                    processPaymentWithOrder(order, total);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("CartActivity", "Erreur chargement panier: " + error);
                    Toast.makeText(CartActivity.this, "Erreur chargement panier: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void processPaymentWithOrder(Order order, double total) {
        paymentService.processPayment(this, total, "Commande PetConnect", new PaymentService.PaymentCallback() {
            @Override
            public void onPaymentSuccess(String paymentId) {
                order.setPaymentId(paymentId);
                order.setPaid(true);
                order.setStatus("paid");

                updateOrderStatus(order);
                Toast.makeText(CartActivity.this, "Paiement réussi! Commande #" + order.getOrderNumber(), Toast.LENGTH_LONG).show();

                cartService.clear();
                cartAdapter.updateItems(new ArrayList<>());
                updateTotal();
                finish();
            }

            @Override
            public void onPaymentCancelled() {
                Toast.makeText(CartActivity.this, "Paiement annulé", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPaymentError(String error) {
                Toast.makeText(CartActivity.this, "Erreur de paiement: " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CartActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveOrderToFirebase(Order order) {
        if (orderService != null) {
            Log.d("CartActivity", "Sauvegarde commande dans Firebase: " + order.getOrderNumber());
            Log.d("CartActivity", "   Items: " + (order.getItems() != null ? order.getItems().size() : 0));
            
            orderService.placeOrder(order, new OrderService.OrderListener() {
                @Override
                public void onSuccess(String orderId) {
                    Log.d("CartActivity", "✅ Commande sauvegardée dans Firebase: " + orderId);
                }

                @Override
                public void onError(String error) {
                    Log.e("CartActivity", "❌ Erreur sauvegarde commande Firebase: " + error);
                    Toast.makeText(CartActivity.this, "Erreur sauvegarde commande: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("CartActivity", "OrderService est null");
        }
    }
    
    private void updateOrderStatus(Order order) {
        if (orderService != null && order.getOrderNumber() != null) {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("status", order.getStatus());
            updateData.put("isPaid", order.isPaid());
            updateData.put("paymentId", order.getPaymentId() != null ? order.getPaymentId() : "");
            updateData.put("updatedAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
            
            db.collection("PetConnect")
                    .document("orders")
                    .collection("all_orders")
                    .document(order.getOrderNumber())
                    .update(updateData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("CartActivity", "✅ Statut commande mis à jour: " + order.getOrderNumber());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CartActivity", "❌ Erreur mise à jour statut: " + e.getMessage());
                    });
        }
    }

    private void showInvoice() {
        Log.d("CartActivity", "Bouton Facture cliqué");

        cartService.loadFromFirebase(new CartService.CartCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    List<CartService.CartItem> items = cartService.getItems();
                    if (items.isEmpty()) {
                        Toast.makeText(CartActivity.this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String orderNumber = "INV_" + System.currentTimeMillis();
                    Order order = new Order(orderNumber, userId, items);
                    order.setTotal(cartService.getTotal());
                    order.setStatus("draft");
                    order.setPaid(false);
                    order.setCreatedAt(new java.util.Date());

                    saveOrderToFirebase(order);
                    openInvoiceActivity(order, items);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("CartActivity", "Erreur chargement panier: " + error);
                    Toast.makeText(CartActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void openInvoiceActivity(Order order, List<CartService.CartItem> items) {
        try {
            Intent intent = new Intent(CartActivity.this, InvoiceActivity.class);

            intent.putExtra("orderId", order.getId());
            intent.putExtra("userId", userId);
            intent.putExtra("orderNumber", order.getOrderNumber());
            intent.putExtra("orderTotal", order.getTotal());
            intent.putExtra("orderStatus", order.getStatus());
            intent.putExtra("isPaid", order.isPaid());
            intent.putExtra("createdAt", order.getCreatedAt() != null ? order.getCreatedAt().getTime() : System.currentTimeMillis());

            intent.putExtra("itemsCount", order.getItems().size());
            for (int i = 0; i < order.getItems().size(); i++) {
                CartService.CartItem item = order.getItems().get(i);
                intent.putExtra("item_" + i + "_productId", item.product.getId());
                intent.putExtra("item_" + i + "_productName", item.product.getName());
                intent.putExtra("item_" + i + "_quantity", item.quantity);
                intent.putExtra("item_" + i + "_price", item.product.getPrice());
            }

            Log.d("CartActivity", "Ouverture de InvoiceActivity avec " + items.size() + " items");
            startActivity(intent);

        } catch (Exception e) {
            Log.e("CartActivity", "Erreur lors de l'ouverture de la facture: " + e.getMessage());
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
