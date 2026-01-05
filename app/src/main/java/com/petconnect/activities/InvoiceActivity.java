package com.petconnect.activities;

import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.petconnect.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {

    // UI Elements
    private TextView tvOrderNumber, tvOrderDate, tvCustomerName, tvCustomerEmail;
    private TextView tvSubtotal, tvShipping, tvTax, tvTotal, tvPaymentStatus;
    private LinearLayout llItemsContainer;
    private Button btnPrint, btnClose;
    private ImageView ivPaymentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Initialiser les vues
        initializeViews();

        // Récupérer les données de l'Intent
        String orderNumber = getIntent().getStringExtra("orderNumber");
        double orderTotal = getIntent().getDoubleExtra("orderTotal", 0.0);
        String orderStatus = getIntent().getStringExtra("orderStatus");
        boolean isPaid = getIntent().getBooleanExtra("isPaid", false);
        int itemsCount = getIntent().getIntExtra("itemsCount", 0);

        // Afficher les détails
        displayInvoiceDetails(orderNumber, orderTotal, orderStatus, isPaid, itemsCount);
    }

    private void initializeViews() {
        tvOrderNumber = findViewById(R.id.tv_order_number);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvCustomerEmail = findViewById(R.id.tv_customer_email);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShipping = findViewById(R.id.tv_shipping);
        tvTax = findViewById(R.id.tv_tax);
        tvTotal = findViewById(R.id.tv_total);
        tvPaymentStatus = findViewById(R.id.tv_payment_status);
        llItemsContainer = findViewById(R.id.ll_items_container);
        ivPaymentStatus = findViewById(R.id.iv_payment_status);

        // SEULEMENT ces 2 boutons maintenant
        btnPrint = findViewById(R.id.btn_print);
        btnClose = findViewById(R.id.btn_close);

        // Configurer les boutons
        setupButtons();
    }

    private void setupButtons() {
        // Bouton Imprimer
        btnPrint.setOnClickListener(v -> {
            Log.d("InvoiceActivity", "Impression lancée");
            printInvoice();
        });

        // Bouton Fermer
        btnClose.setOnClickListener(v -> finish());
    }

    private void displayInvoiceDetails(String orderNumber, double orderTotal,
                                       String orderStatus, boolean isPaid, int itemsCount) {

        // Numéro de commande
        if (orderNumber != null) {
            tvOrderNumber.setText("Commande #" + orderNumber);
        }

        // Date actuelle
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
        tvOrderDate.setText(sdf.format(new Date()));

        // Client par défaut
        tvCustomerName.setText("Client PetConnect");
        tvCustomerEmail.setText("client@petconnect.com");

        // Calculs
        double subtotal = orderTotal;
        double shipping = 4.99;
        double tax = subtotal * 0.20;
        double total = subtotal + shipping + tax;

        tvSubtotal.setText(String.format("€%.2f", subtotal));
        tvShipping.setText(String.format("€%.2f", shipping));
        tvTax.setText(String.format("€%.2f", tax));
        tvTotal.setText(String.format("€%.2f", total));

        // Statut de paiement
        String paymentStatus = isPaid ? "Payé ✓" : "Non payé";
        tvPaymentStatus.setText(paymentStatus);

        if (isPaid) {
            ivPaymentStatus.setImageResource(R.drawable.ic_payment_success);
            tvPaymentStatus.setTextColor(getResources().getColor(R.color.success));
        } else {
            ivPaymentStatus.setImageResource(R.drawable.ic_payment_pending);
            tvPaymentStatus.setTextColor(getResources().getColor(R.color.warning));
        }

        // Charger les items depuis l'Intent
        loadItemsFromIntent(itemsCount);
    }

    private void loadItemsFromIntent(int itemsCount) {
        llItemsContainer.removeAllViews();

        if (itemsCount == 0) {
            // Produits fictifs pour démo
            loadSampleProducts();
            return;
        }

        double total = 0;

        for (int i = 0; i < itemsCount; i++) {
            String productName = getIntent().getStringExtra("item_" + i + "_productName");
            int quantity = getIntent().getIntExtra("item_" + i + "_quantity", 0);
            double price = getIntent().getDoubleExtra("item_" + i + "_price", 0.0);

            if (productName != null) {
                double itemTotal = price * quantity;
                total += itemTotal;

                // Créer une vue d'item simple
                View itemView = createInvoiceItemView(productName, quantity, price, itemTotal);
                llItemsContainer.addView(itemView);
            }
        }

        // Mettre à jour le total
        tvSubtotal.setText(String.format("€%.2f", total));
        double shipping = 4.99;
        double tax = total * 0.20;
        double grandTotal = total + shipping + tax;
        tvTotal.setText(String.format("€%.2f", grandTotal));
    }

    private View createInvoiceItemView(String productName, int quantity, double price, double itemTotal) {
        View itemView = getLayoutInflater().inflate(R.layout.item_invoice_product, null);

        TextView tvProductName = itemView.findViewById(R.id.tv_product_name);
        TextView tvProductQty = itemView.findViewById(R.id.tv_product_qty);
        TextView tvProductPrice = itemView.findViewById(R.id.tv_product_price);
        TextView tvProductTotal = itemView.findViewById(R.id.tv_product_total);

        tvProductName.setText(productName);
        tvProductQty.setText(String.valueOf(quantity));
        tvProductPrice.setText(String.format("€%.2f", price));
        tvProductTotal.setText(String.format("€%.2f", itemTotal));

        return itemView;
    }

    private void loadSampleProducts() {
        // Produits fictifs pour la démonstration
        String[] productNames = {
                "Nourriture pour Chien Premium",
                "Jouet pour Chat Interactive",
                "Litière Aglomérante 5kg"
        };

        double[] productPrices = {29.99, 12.50, 19.99};
        int[] productQuantities = {2, 1, 1};

        double total = 0;

        for (int i = 0; i < productNames.length; i++) {
            double itemTotal = productPrices[i] * productQuantities[i];
            total += itemTotal;

            View itemView = createInvoiceItemView(
                    productNames[i],
                    productQuantities[i],
                    productPrices[i],
                    itemTotal
            );
            llItemsContainer.addView(itemView);
        }

        // Mettre à jour le total
        tvSubtotal.setText(String.format("€%.2f", total));
        double shipping = 4.99;
        double tax = total * 0.20;
        double grandTotal = total + shipping + tax;
        tvTotal.setText(String.format("€%.2f", grandTotal));
    }

    // ==================== FONCTION D'IMPRESSION ====================

    private void printInvoice() {
        try {
            // Créer un WebView pour l'impression
            WebView webView = new WebView(this);
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    // Lorsque la page est chargée, lancer l'impression
                    createWebPrintJob(view);
                }
            });

            // Générer le HTML de la facture
            String htmlContent = generateInvoiceHtml();
            webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null);

        } catch (Exception e) {
            Toast.makeText(this, "Erreur d'impression: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("InvoiceActivity", "Erreur impression: " + e.getMessage());
        }
    }

    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

        if (printManager != null) {
            String jobName = "Facture PetConnect - " + tvOrderNumber.getText();
            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
            Toast.makeText(this, "Impression lancée", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Service d'impression non disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateInvoiceHtml() {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head>")
                .append("<meta charset='UTF-8'>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }")
                .append(".header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 20px; }")
                .append(".company-name { color: #FF6B35; font-size: 24px; font-weight: bold; margin-bottom: 5px; }")
                .append(".invoice-title { font-size: 20px; font-weight: bold; margin-bottom: 10px; }")
                .append(".details-section { margin-bottom: 30px; }")
                .append(".details-row { margin-bottom: 10px; }")
                .append(".details-label { font-weight: bold; display: inline-block; width: 150px; }")
                .append(".customer-section { background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin-bottom: 20px; }")
                .append(".customer-title { font-weight: bold; margin-bottom: 10px; color: #666; }")
                .append(".payment-status { padding: 10px; border: 1px solid #4CAF50; border-radius: 5px; margin-bottom: 20px; }")
                .append(".items-table { width: 100%; border-collapse: collapse; margin: 20px 0; }")
                .append(".items-table th { background-color: #f2f2f2; padding: 12px; text-align: left; border: 1px solid #ddd; }")
                .append(".items-table td { padding: 10px; border: 1px solid #ddd; }")
                .append(".totals-section { text-align: right; margin-top: 30px; }")
                .append(".total-row { margin-bottom: 10px; }")
                .append(".total-label { display: inline-block; width: 150px; }")
                .append(".total-value { display: inline-block; width: 100px; }")
                .append(".grand-total { font-size: 18px; font-weight: bold; margin-top: 20px; padding-top: 10px; border-top: 2px solid #333; }")
                .append(".footer { margin-top: 50px; text-align: center; font-size: 12px; color: #666; }")
                .append("</style>")
                .append("</head><body>")
                .append("<div class='header'>")
                .append("<div class='company-name'>PETCONNECT BOUTIQUE</div>")
                .append("<div class='invoice-title'>FACTURE</div>")
                .append("</div>")
                .append("<div class='details-section'>")
                .append("<div class='details-row'><span class='details-label'>N° Commande:</span> ").append(tvOrderNumber.getText()).append("</div>")
                .append("<div class='details-row'><span class='details-label'>Date:</span> ").append(tvOrderDate.getText()).append("</div>")
                .append("</div>")
                .append("<div class='customer-section'>")
                .append("<div class='customer-title'>INFORMATIONS CLIENT</div>")
                .append("<div class='details-row'><span class='details-label'>Nom:</span> ").append(tvCustomerName.getText()).append("</div>")
                .append("<div class='details-row'><span class='details-label'>Email:</span> ").append(tvCustomerEmail.getText()).append("</div>")
                .append("</div>")
                .append("<div class='payment-status'>")
                .append("<span class='details-label'>Statut paiement:</span> ").append(tvPaymentStatus.getText())
                .append("</div>")
                .append("<table class='items-table'>")
                .append("<thead><tr><th>Produit</th><th>Quantité</th><th>Prix unitaire</th><th>Total</th></tr></thead>")
                .append("<tbody>");

        // Ajouter les produits
        int childCount = llItemsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = llItemsContainer.getChildAt(i);
            if (itemView != null) {
                TextView tvProductName = itemView.findViewById(R.id.tv_product_name);
                TextView tvProductQty = itemView.findViewById(R.id.tv_product_qty);
                TextView tvProductPrice = itemView.findViewById(R.id.tv_product_price);
                TextView tvProductTotal = itemView.findViewById(R.id.tv_product_total);

                html.append("<tr>")
                        .append("<td>").append(tvProductName.getText()).append("</td>")
                        .append("<td>").append(tvProductQty.getText()).append("</td>")
                        .append("<td>").append(tvProductPrice.getText()).append("</td>")
                        .append("<td>").append(tvProductTotal.getText()).append("</td>")
                        .append("</tr>");
            }
        }

        html.append("</tbody></table>")
                .append("<div class='totals-section'>")
                .append("<div class='total-row'><span class='total-label'>Sous-total:</span><span class='total-value'>").append(tvSubtotal.getText()).append("</span></div>")
                .append("<div class='total-row'><span class='total-label'>Livraison:</span><span class='total-value'>").append(tvShipping.getText()).append("</span></div>")
                .append("<div class='total-row'><span class='total-label'>TVA (20%):</span><span class='total-value'>").append(tvTax.getText()).append("</span></div>")
                .append("<div class='total-row grand-total'><span class='total-label'>TOTAL:</span><span class='total-value'>").append(tvTotal.getText()).append("</span></div>")
                .append("</div>")
                .append("<div class='footer'>")
                .append("<p>PetConnect Boutique</p>")
                .append("<p>Merci pour votre achat !</p>")
                .append("</div>")
                .append("</body></html>");

        return html.toString();
    }
}