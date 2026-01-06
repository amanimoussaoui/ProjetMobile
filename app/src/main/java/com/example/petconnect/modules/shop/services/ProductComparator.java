package com.example.petconnect.modules.shop.services;

import com.example.petconnect.modules.shop.models.Product;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductComparator {
    public static List<Product> compareProducts(List<Product> products) {
        // Comparer par: prix, rating, caractéristiques
        return products.stream()
                .sorted(Comparator
                        .comparing(Product::getRating).reversed()
                        .thenComparing(Product::getPrice))
                .collect(Collectors.toList());
    }
}
