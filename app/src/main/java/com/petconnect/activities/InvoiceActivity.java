package com.petconnect.activities;

import android.content.Intent;
import android.net.Uri;
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
import androidx.core.content.FileProvider;

import com.petconnect.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {

    // UI Elements
    private TextView tvOrderNumber, tvOrderDate, tvCustomerName, tvCustomerEmail;
    private TextView tvSubtotal, tvShipping, tvTax, tvTotal, tvPaymentStatus;
    private LinearLayout llItemsContainer;
    private Button btnPrint, btnShare, btnClose;
    private ImageView ivPaymentStatus;
    private String invoiceHtmlContent;

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

        // Générer le HTML de la facture
        invoiceHtmlContent = generateInvoiceHtml();
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

        // Boutons
        btnPrint = findViewById(R.id.btn_print);
        btnShare = findViewById(R.id.btn_share);
        btnClose = findViewById(R.id.btn_close);

        // Configurer les boutons
        setupButtons();
    }

    private void setupButtons() {
        // Bouton Imprimer - Version améliorée
        btnPrint.setOnClickListener(v -> {
            Log.d("InvoiceActivity", "Impression lancée");
            printInvoiceWithPreview();
        });

        // Bouton Partager
        btnShare.setOnClickListener(v -> {
            Log.d("InvoiceActivity", "Partage lancé");
            shareInvoice();
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

    // ==================== FONCTION D'IMPRESSION AMÉLIORÉE ====================

    private void printInvoiceWithPreview() {
        try {
            // Créer un WebView pour l'impression
            WebView webView = new WebView(this);
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    // Lorsque la page est chargée, lancer l'impression avec prévisualisation
                    createWebPrintJob(view);
                }
            });

            // Utiliser le contenu HTML généré
            String htmlContent = invoiceHtmlContent != null ? invoiceHtmlContent : generateInvoiceHtml();
            webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null);

        } catch (Exception e) {
            Toast.makeText(this, "Erreur d'impression: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("InvoiceActivity", "Erreur impression: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createWebPrintJob(WebView webView) {
        try {
            PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

            if (printManager != null) {
                String jobName = "Facture PetConnect - " + tvOrderNumber.getText();
                PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

                // Créer des attributs d'impression optimisés
                PrintAttributes attributes = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(new PrintAttributes.Resolution("pdf", "print", 300, 300))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build();

                printManager.print(jobName, printAdapter, attributes);
                Toast.makeText(this, "Impression lancée avec succès", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Service d'impression non disponible", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'impression: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("InvoiceActivity", "Erreur createWebPrintJob: " + e.getMessage());
        }
    }

    // ==================== FONCTION DE PARTAGE ====================

    private void shareInvoice() {
        try {
            // Générer le contenu texte pour le partage
            String shareText = generateShareText();

            // Créer un fichier HTML temporaire (optionnel)
            File tempFile = createTempHtmlFile();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/html");

            // Sujet du message
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Facture PetConnect - " + tvOrderNumber.getText());

            // Texte du message
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            // Si un fichier a été créé, l'attacher
            if (tempFile != null && tempFile.exists()) {
                Uri fileUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider",
                        tempFile);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("text/html");
            }

            // Démarrer l'activité de partage
            startActivity(Intent.createChooser(shareIntent, "Partager la facture via"));

        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors du partage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("InvoiceActivity", "Erreur partage: " + e.getMessage());
        }
    }

    private String generateShareText() {
        StringBuilder text = new StringBuilder();
        text.append("🎫 FACTURE PETCONNECT\n\n");
        text.append("N° Commande: ").append(tvOrderNumber.getText()).append("\n");
        text.append("Date: ").append(tvOrderDate.getText()).append("\n");
        text.append("Client: ").append(tvCustomerName.getText()).append("\n");
        text.append("Email: ").append(tvCustomerEmail.getText()).append("\n");
        text.append("Statut paiement: ").append(tvPaymentStatus.getText()).append("\n\n");

        text.append("📋 DÉTAIL DES PRODUITS:\n");
        int childCount = llItemsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = llItemsContainer.getChildAt(i);
            if (itemView != null) {
                TextView tvProductName = itemView.findViewById(R.id.tv_product_name);
                TextView tvProductQty = itemView.findViewById(R.id.tv_product_qty);
                TextView tvProductPrice = itemView.findViewById(R.id.tv_product_price);
                TextView tvProductTotal = itemView.findViewById(R.id.tv_product_total);

                text.append("• ").append(tvProductName.getText())
                        .append(" (x").append(tvProductQty.getText()).append(") - ")
                        .append(tvProductPrice.getText()).append(" = ")
                        .append(tvProductTotal.getText()).append("\n");
            }
        }

        text.append("\n💰 TOTAL:\n");
        text.append("Sous-total: ").append(tvSubtotal.getText()).append("\n");
        text.append("Livraison: ").append(tvShipping.getText()).append("\n");
        text.append("TVA: ").append(tvTax.getText()).append("\n");
        text.append("TOTAL FINAL: ").append(tvTotal.getText()).append("\n\n");

        text.append("Merci pour votre confiance !\n");
        text.append("PetConnect Boutique");

        return text.toString();
    }

    private File createTempHtmlFile() {
        try {
            // Créer un nom de fichier unique
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(new Date());
            String fileName = "Facture_PetConnect_" + timeStamp + ".html";

            // Créer le fichier dans le cache
            File tempFile = new File(getCacheDir(), fileName);

            // Écrire le contenu HTML dans le fichier
            String htmlContent = invoiceHtmlContent != null ? invoiceHtmlContent : generateInvoiceHtml();
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(htmlContent.getBytes());
            fos.close();

            return tempFile;
        } catch (IOException e) {
            Log.e("InvoiceActivity", "Erreur création fichier: " + e.getMessage());
            return null;
        }
    }

    // ==================== GÉNÉRATION HTML ====================

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Nettoyer les fichiers temporaires si nécessaire
        cleanupTempFiles();
    }

    private void cleanupTempFiles() {
        try {
            File cacheDir = getCacheDir();
            if (cacheDir.exists() && cacheDir.isDirectory()) {
                File[] files = cacheDir.listFiles((dir, name) -> name.startsWith("Facture_PetConnect_"));
                if (files != null) {
                    for (File file : files) {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("InvoiceActivity", "Erreur nettoyage fichiers: " + e.getMessage());
        }
    }
}