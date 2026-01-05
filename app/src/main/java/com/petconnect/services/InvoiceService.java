package com.petconnect.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.petconnect.activities.InvoiceActivity;
import com.petconnect.models.Order;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InvoiceService {
    private Context context;

    public InvoiceService(Context context) {
        this.context = context;
    }

    // Générer le HTML de la facture
    public String generateInvoiceHtml(Order order) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head>")
                .append("<meta charset='UTF-8'>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 40px; }")
                .append(".header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 20px; }")
                .append(".company { font-size: 24px; font-weight: bold; color: #4CAF50; }")
                .append(".invoice-title { font-size: 18px; margin-top: 10px; }")
                .append(".info-section { margin-bottom: 30px; }")
                .append(".info-row { display: flex; margin-bottom: 10px; }")
                .append(".info-label { font-weight: bold; width: 150px; }")
                .append(".items-table { width: 100%; border-collapse: collapse; margin: 20px 0; }")
                .append(".items-table th { background-color: #f2f2f2; padding: 12px; text-align: left; border: 1px solid #ddd; }")
                .append(".items-table td { padding: 10px; border: 1px solid #ddd; }")
                .append(".total-section { text-align: right; margin-top: 30px; font-size: 18px; }")
                .append(".status-paid { color: green; font-weight: bold; }")
                .append(".status-pending { color: orange; font-weight: bold; }")
                .append(".footer { margin-top: 50px; text-align: center; font-size: 12px; color: #666; }")
                .append("</style>")
                .append("</head><body>")
                .append("<div class='header'>")
                .append("<div class='company'>PETCONNECT SHOP</div>")
                .append("<div class='invoice-title'>FACTURE</div>")
                .append("</div>")
                .append("<div class='info-section'>")
                .append("<div class='info-row'><span class='info-label'>N° Commande:</span> ").append(order.getOrderNumber()).append("</div>")
                .append("<div class='info-row'><span class='info-label'>Date:</span> ").append(formatDate(new Date())).append("</div>")
                .append("<div class='info-row'><span class='info-label'>Client:</span> ").append(order.getCustomerName()).append("</div>")
                .append("<div class='info-row'><span class='info-label'>Email:</span> ").append(order.getCustomerEmail()).append("</div>")
                .append("<div class='info-row'><span class='info-label'>Statut:</span> ")
                .append("<span class='").append(order.isPaid() ? "status-paid" : "status-pending").append("'>")
                .append(order.isPaid() ? "PAYÉ" : "EN ATTENTE")
                .append("</span></div>")
                .append("</div>")
                .append("<table class='items-table'>")
                .append("<thead><tr><th>Produit</th><th>Quantité</th><th>Prix unitaire</th><th>Total</th></tr></thead>")
                .append("<tbody>");

        // Ajouter les items
        for (CartService.CartItem item : order.getItems()) {
            html.append("<tr>")
                    .append("<td>").append(item.product.getName()).append("</td>")
                    .append("<td>").append(item.quantity).append("</td>")
                    .append("<td>").append(String.format(Locale.FRANCE, "%.2f €", item.product.getFinalPrice())).append("</td>")
                    .append("<td>").append(String.format(Locale.FRANCE, "%.2f €", item.getItemTotal())).append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table>")
                .append("<div class='total-section'>")
                .append("<p><strong>TOTAL TTC: ").append(String.format(Locale.FRANCE, "%.2f €", order.getTotal())).append("</strong></p>")
                .append("</div>")
                .append("<div class='footer'>")
                .append("<p>PetConnect Shop - SIRET: 123 456 789 00010 - TVA: FR12 345678901</p>")
                .append("<p>Contact: contact@petconnect.fr - Tél: 01 23 45 67 89</p>")
                .append("</div>")
                .append("</body></html>");

        return html.toString();
    }

    // Afficher la facture dans une activité
    public void displayInvoice(Order order) {
        Intent intent = new Intent(context, InvoiceActivity.class);
        intent.putExtra("order", (CharSequence) order);
        context.startActivity(intent);
    }

    // Imprimer la facture
    public void printInvoice(WebView webView, String orderNumber) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("Facture_" + orderNumber);
        String jobName = "Facture " + orderNumber;

        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }

    // Partager la facture
    public void shareInvoice(String htmlContent) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/html");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Facture PetConnect");
        shareIntent.putExtra(Intent.EXTRA_TEXT, htmlContent);

        context.startActivity(Intent.createChooser(shareIntent, "Partager la facture"));
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
        return sdf.format(date);
    }
}