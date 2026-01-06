package com.example.petconnect.modules.shop.adapters;

import android.content.Context;
import android.content.Intent;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petconnect.R;
import com.example.petconnect.modules.shop.activities.InvoiceActivity;
import com.example.petconnect.modules.shop.models.Order;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<Order> orders;
    private String currentUserId;

    public OrderAdapter(Context context, List<Order> orders, String userId) {
        this.context = context;
        this.orders = orders;
        this.currentUserId = userId;
    }

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    public void setUserId(String userId) {
        this.currentUserId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderNumber.setText("Commande #" + order.getOrderNumber());
        holder.tvTotal.setText(String.format("€%.2f", order.getTotal()));
        holder.tvStatus.setText(getStatusLabel(order.getStatus()));
        
        if (order.isPaid()) {
            holder.tvPaymentStatus.setText("Payé");
            holder.tvPaymentStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.status_completed_bg));
        } else {
            holder.tvPaymentStatus.setText("Non payé");
            holder.tvPaymentStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.status_pending_bg));
        }

        if (order.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            holder.tvDate.setText(sdf.format(order.getCreatedAt()));
        }

        if (holder.btnPrint != null) {
            holder.btnPrint.setOnClickListener(v -> printOrder(order));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InvoiceActivity.class);
            intent.putExtra("orderId", order.getId());

            String userIdToSend = (currentUserId != null && !currentUserId.isEmpty())
                    ? currentUserId
                    : "user_id_here";

            intent.putExtra("userId", userIdToSend);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    private String getStatusLabel(String status) {
        if (status == null) return "En attente";
        switch (status.toLowerCase()) {
            case "pending": return "En attente";
            case "paid": return "Payé";
            case "shipped": return "Expédié";
            case "delivered": return "Livré";
            case "cancelled": return "Annulé";
            default: return status;
        }
    }

    private void printOrder(Order order) {
        String htmlContent = generateOrderHtml(order);
        
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(view, order.getOrderNumber());
            }
        });
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null);
    }

    private void createWebPrintJob(WebView webView, String orderNumber) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("Order_" + orderNumber);
        String jobName = "Commande " + orderNumber;
        PrintJob printJob = printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        
        if (printJob.isCompleted()) {
            Toast.makeText(context, "Impression terminée", Toast.LENGTH_SHORT).show();
        } else if (printJob.isFailed()) {
            Toast.makeText(context, "Erreur lors de l'impression", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateOrderHtml(Order order) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; padding: 20px; }");
        html.append("h1 { color: #329DA3; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #329DA3; color: white; }");
        html.append("</style></head><body>");
        html.append("<h1>Commande #").append(order.getOrderNumber()).append("</h1>");
        html.append("<p><strong>Date:</strong> ").append(formatDate(order.getCreatedAt())).append("</p>");
        html.append("<p><strong>Statut:</strong> ").append(getStatusLabel(order.getStatus())).append("</p>");
        html.append("<p><strong>Paiement:</strong> ").append(order.isPaid() ? "Payé" : "Non payé").append("</p>");
        
        html.append("<table>");
        html.append("<tr><th>Produit</th><th>Quantité</th><th>Prix unitaire</th><th>Total</th></tr>");
        
        if (order.getItems() != null) {
            for (com.example.petconnect.modules.shop.services.CartService.CartItem item : order.getItems()) {
                html.append("<tr>");
                html.append("<td>").append(item.product.getName()).append("</td>");
                html.append("<td>").append(item.quantity).append("</td>");
                html.append("<td>").append(String.format("%.2f €", item.product.getPrice())).append("</td>");
                html.append("<td>").append(String.format("%.2f €", item.getItemTotal())).append("</td>");
                html.append("</tr>");
            }
        }
        
        html.append("<tr><td colspan='3'><strong>Total</strong></td><td><strong>").append(String.format("%.2f €", order.getTotal())).append("</strong></td></tr>");
        html.append("</table>");
        html.append("</body></html>");
        return html.toString();
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
        return sdf.format(date);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvTotal, tvStatus, tvDate, tvPaymentStatus;
        Button btnPrint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tv_order_number);
            tvTotal = itemView.findViewById(R.id.tv_order_total);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvDate = itemView.findViewById(R.id.tv_order_date);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);
            btnPrint = itemView.findViewById(R.id.btn_print);
        }
    }
}
