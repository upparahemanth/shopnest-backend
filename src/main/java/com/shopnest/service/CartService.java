package com.shopnest.service;

import com.shopnest.entity.Cart;

public interface CartService {
    Cart getCartByUser(String email);
    Cart addToCart(String email, Long productId, Integer quantity);
    Cart updateCartItem(String email, Long cartItemId, Integer quantity);
    void removeFromCart(String email, Long cartItemId);
    void clearCart(String email);
}