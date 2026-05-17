package com.shopnest.controller;

import com.shopnest.entity.Cart;
import com.shopnest.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Principal principal) {
        return ResponseEntity.ok(cartService.getCartByUser(principal.getName()));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(Principal principal,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(
                cartService.addToCart(principal.getName(), productId, quantity));
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<Cart> updateCartItem(Principal principal,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(
                cartService.updateCartItem(principal.getName(), cartItemId, quantity));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeFromCart(Principal principal,
            @PathVariable Long cartItemId) {
        cartService.removeFromCart(principal.getName(), cartItemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ResponseEntity.ok("Cart cleared");
    }
}