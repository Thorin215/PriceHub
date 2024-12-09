package com.pricehub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class CartService {

    private final CartRepository cartRepository;
    
    // private CartService cartService;
    private GoodService goodService;
    private VersionService versionService;
    
    @Autowired
    public CartService(CartRepository cartRepository, GoodService goodService, VersionService versionService) {
        this.cartRepository = cartRepository;
        this.goodService = goodService;
        this.versionService = versionService;
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

    public List<CartItem> getCartItemsForUser(String userId) {
        List<CartItem> cartItems = new ArrayList<>();
        // 获取指定用户的所有购物车项
        List<Cart> carts = cartRepository.findByUserId(userId);

        // 遍历所有购物车项，将其转换为 CartItem
        for (Cart cart : carts) {
            CartItem item = new CartItem();
            
            // 假设 Cart 中包含 goodId 和 versionId
            Good good = goodService.getGoodById(cart.getGoodId()); // 获取商品信息
            Version version = versionService.getLatestVersionByGoodId(cart.getGoodId()); // 获取商品最新版本信息

            // 将 Cart 和商品信息转换为 CartItem
            item.setGoodId(good.getId());
            item.setGoodName(good.getName());
            item.setGoodImage(good.getImage()); // 假设 Good 类有 image 字段
            item.setPrice(version != null ? version.getPrice().doubleValue() : 0.0); // 获取版本的价格
            item.setVersionId(version != null ? version.getId() : null); // 获取版本 ID
            item.setNewprice(versionService.getLatestPriceByGoodId(good.getId())); // 获取最新价格

            // 将 CartItem 添加到列表
            cartItems.add(item);
        }

        return cartItems;
    }

}
