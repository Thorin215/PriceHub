package com.pricehub;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCart(@RequestBody CartRequest request) {
        // 打印请求参数
        System.out.println("User ID: " + request.getUserId());
        System.out.println("Good ID: " + request.getGoodId());
        System.out.println("Version ID: " + request.getVersionId());

        // 调用服务层的方法添加商品到购物车
        boolean check = cartService.addCart(request.getUserId(), request.getGoodId(), request.getVersionId());
        if (!check) {
            Response response = new Response("error", HttpStatus.BAD_REQUEST.value(), "商品已经在购物车中");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }else{
            Response response = new Response("success", HttpStatus.OK.value(), "商品添加成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        // Response response = new Response("success", HttpStatus.OK.value(), "商品添加成功");
        // return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListResponse> getCart(@PathVariable String id) {
        List<Cart> carts = cartService.getCartByUserId(id);
        ListResponse response = new ListResponse("success", HttpStatus.OK.value(), carts);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCart(@RequestParam String userId, @RequestParam Long goodId,
                                         @RequestParam Long versionId) {
        cartService.updateCart(userId, goodId, versionId);
        return ResponseEntity.ok("购物车商品更新成功");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeCart(@RequestParam String userId, @RequestParam Long goodId,
                                         @RequestParam Long versionId) {
        cartService.removeCart(userId, goodId, versionId);
        return ResponseEntity.ok("商品删除成功");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestParam String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("购物车已清空");
    }
    
    static class CartRequest {
        private String userId;
        private Long versionId;  // 修改为 Long 类型
        private Long goodId;     // 修改为 Long 类型

        // Getter 和 Setter 方法
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Long getVersionId() {
            return versionId;
        }

        public void setVersionId(Long versionId) {
            this.versionId = versionId;
        }

        public Long getGoodId() {
            return goodId;
        }

        public void setGoodId(Long goodId) {
            this.goodId = goodId;
        }
    }

    static class Response {
        private String msg;
        private int code;
        private Object data; // 改为 Object 类型

        public Response(String msg, int code, Object data) {
            this.msg = msg;
            this.code = code;
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    static class ListResponse {
        private String msg;
        private int code;
        private List<Cart> data;

        public ListResponse(String msg, int code, List<Cart> data) {
            this.msg = msg;
            this.code = code;
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<Cart> getData() {
            return data;
        }

        public void setData(List<Cart> data) {
            this.data = data;
        }
    }
}
