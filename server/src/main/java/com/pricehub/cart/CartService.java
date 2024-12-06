package com.pricehub;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public boolean addCart(String userId, Long goodId, Long versionId) {
        // 检查购物车中是否已存在相同的商品和版本
        Optional<Cart> existingCart = cartRepository.findByUserIdAndGoodIdAndVersionId(userId, goodId, versionId);
        
        // 如果不存在重复项，则添加购物车
        if (!existingCart.isPresent()) {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setGoodId(goodId);
            cart.setVersionId(versionId);
            cartRepository.save(cart);
        } else {
            // 如果已经存在，可以选择记录日志或返回某种提示
            // 例如：throw new RuntimeException("该商品已经在购物车中");
            System.out.println("该商品已经在购物车中！");
            return false;
        }

        return true;
    }

    public List<Cart> getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public void updateCart(String userId, Long goodId, Long versionId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setGoodId(goodId);
        cart.setVersionId(versionId);
        cartRepository.save(cart);
    }

    public void removeCart(String userId, Long goodId, Long versionId) {
        cartRepository.deleteByUserId(userId); // 删除指定用户的所有购物车项，或可以根据需求调整
    }

    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }
}
