package com.petconnect.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.petconnect.R;
import com.petconnect.adapters.OrderAdapter;
import com.petconnect.models.Order;
import com.petconnect.services.OrderService;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private TextView tvEmpty;
    private OrderAdapter orderAdapter;
    private OrderService orderService;
    private List<Order> ordersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerOrders = findViewById(R.id.recycler_orders);
        tvEmpty = findViewById(R.id.tv_empty);

        // Initialiser le service de commandes (userId local)
        orderService = new OrderService("local_user");
        ordersList = new ArrayList<>();

        // Configurer RecyclerView
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, ordersList, "local_user");
        recyclerOrders.setAdapter(orderAdapter);

        // Charger les commandes (localement)
        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        if (orderService == null) {
            return;
        }

        orderService.getUserOrders(new OrderService.OrderListListener() {
            @Override
            public void onOrdersLoaded(List<Order> orders) {
                ordersList.clear();
                ordersList.addAll(orders);
                orderAdapter.updateOrders(new ArrayList<>(ordersList));

                // Afficher/masquer le message vide
                if (ordersList.isEmpty()) {
                    tvEmpty.setVisibility(TextView.VISIBLE);
                    recyclerOrders.setVisibility(RecyclerView.GONE);
                } else {
                    tvEmpty.setVisibility(TextView.GONE);
                    recyclerOrders.setVisibility(RecyclerView.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                tvEmpty.setText("Erreur lors du chargement des commandes: " + error);
                tvEmpty.setVisibility(TextView.VISIBLE);
                recyclerOrders.setVisibility(RecyclerView.GONE);
            }
        });
    }
}

