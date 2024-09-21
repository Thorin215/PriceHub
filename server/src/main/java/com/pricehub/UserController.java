package com.pricehub;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Response> addUser(@RequestBody User user) {
        userRepository.save(user);
        Response response = new Response("success", HttpStatus.OK.value(), user); // 使用状态码
        return new ResponseEntity<>(response, HttpStatus.OK); // 返回 200 状态码
    }

    static class Response {
        private String msg;
        private int code; // 改为 int 类型
        private User data;

        public Response(String msg, int code, User data) {
            this.msg = msg;
            this.code = code; // 使用状态码
            this.data = data;
        }

        // Getters and Setters
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

        public User getData() {
            return data;
        }

        public void setData(User data) {
            this.data = data;
        }
    }
}

