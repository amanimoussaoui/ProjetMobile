package com.example.petconnect.modules.shop.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petconnect.R;
import com.example.petconnect.modules.shop.services.CartService;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Holder> {

    private List<CartService.CartItem> items;
    private final CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(CartService.CartItem item, int newQuantity);
        void onItemRemoved(CartService.CartItem item);
        void onCartUpdated();
    }

    public CartAdapter(List<CartService.CartItem> items, CartItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public CartAdapter(List<CartService.CartItem> items, Runnable onCartUpdated) {
        this.items = items;
        this.listener = new CartItemListener() {
            @Override
            public void onQuantityChanged(CartService.CartItem item, int newQuantity) {
                if (onCartUpdated != null) {
                    onCartUpdated.run();
                }
            }

            @Override
            public void onItemRemoved(CartService.CartItem item) {
                if (onCartUpdated != null) {
                    onCartUpdated.run();
                }
            }

            @Override
            public void onCartUpdated() {
                if (onCartUpdated != null) {
                    onCartUpdated.run();
                }
            }
        };
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder h, int position) {
        if (position >= items.size()) {
            return;
        }
        
        CartService.CartItem item = items.get(position);
        
        h.name.setText(item.product.getName());
        h.price.setText(String.format("Prix unitaire: %.2f €", item.product.getPrice()));
        h.qty.setText(String.valueOf(item.quantity));
        h.subtotal.setText(String.format("Sous-total: %.2f €", item.getItemTotal()));

        h.plus.setOnClickListener(v -> {
            if (position < items.size()) {
                CartService.CartItem currentItem = items.get(position);
                int newQuantity = currentItem.quantity + 1;
                if (listener != null) {
                    listener.onQuantityChanged(currentItem, newQuantity);
                }
            }
        });

        h.minus.setOnClickListener(v -> {
            if (position < items.size()) {
                CartService.CartItem currentItem = items.get(position);
                if (currentItem.quantity > 1) {
                    int newQuantity = currentItem.quantity - 1;
                    if (listener != null) {
                        listener.onQuantityChanged(currentItem, newQuantity);
                    }
                }
            }
        });

        h.edit.setOnClickListener(v -> {
            if (position < items.size()) {
                showEditQuantityDialog(h.itemView.getContext(), items.get(position), position);
            }
        });

        h.delete.setOnClickListener(v -> {
            if (position < items.size()) {
                if (listener != null) {
                    listener.onItemRemoved(items.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<CartService.CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    private void showEditQuantityDialog(Context context, CartService.CartItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Modifier la quantité");

        final EditText input = new EditText(context);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(item.quantity));
        input.setSelection(input.getText().length());
        builder.setView(input);

        builder.setPositiveButton("Modifier", (dialog, which) -> {
            String quantityStr = input.getText().toString();
            try {
                int newQuantity = Integer.parseInt(quantityStr);
                if (newQuantity > 0) {
                    if (listener != null) {
                        listener.onQuantityChanged(item, newQuantity);
                    }
                } else {
                    Toast.makeText(context, "La quantité doit être supérieure à 0", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView name, qty, price, subtotal;
        Button plus, minus;
        ImageButton delete, edit;

        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.tv_product_name);
            price = v.findViewById(R.id.tv_product_price);
            qty = v.findViewById(R.id.tv_quantity);
            subtotal = v.findViewById(R.id.tv_subtotal);
            plus = v.findViewById(R.id.btn_increase);
            minus = v.findViewById(R.id.btn_decrease);
            delete = v.findViewById(R.id.btn_delete);
            edit = v.findViewById(R.id.btn_edit);
        }
    }
}
