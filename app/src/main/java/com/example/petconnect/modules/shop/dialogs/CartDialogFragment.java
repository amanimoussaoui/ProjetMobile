package com.example.petconnect.modules.shop.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petconnect.R;
import com.example.petconnect.modules.shop.adapters.CartAdapter;
import com.example.petconnect.modules.shop.services.CartService;
import java.text.NumberFormat;
import java.util.Locale;

public class CartDialogFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private TextView tvTotalPrice, tvEmptyCart;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_cart, null);

        recyclerView = view.findViewById(R.id.cart_recycler_view);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        tvEmptyCart = view.findViewById(R.id.tv_empty_cart);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CartAdapter adapter = new CartAdapter(
                CartService.getInstance().getItems(),
                this::updateTotal
        );
        recyclerView.setAdapter(adapter);

        updateTotal();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("🛒 Votre Panier")
                .setPositiveButton("Fermer", (d, i) -> dismiss());

        return builder.create();
    }

    private void updateTotal() {
        double total = CartService.getInstance().getTotal();

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        tvTotalPrice.setText("Total : " + format.format(total));

        if (CartService.getInstance().getItems().isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyCart.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
