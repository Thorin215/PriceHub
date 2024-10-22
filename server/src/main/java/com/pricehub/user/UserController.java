package com.pricehub;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<Response> addUser(@RequestBody User user) {
        userRepository.save(user);
        Response response = new Response("success", HttpStatus.OK.value(), user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/all")
    public ResponseEntity<ListResponse> queryAllUsers() {
        List<User> users = userRepository.findAll();
        ListResponse response = new ListResponse("success", HttpStatus.OK.value(), users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userRepository.findById(loginRequest.getId());
        if (user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())) {
            Response response = new Response("login successful", HttpStatus.OK.value(), "登录成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            Response response = new Response("invalid credentials", HttpStatus.UNAUTHORIZED.value(), "凭证无效");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<Response> queryUserById(@RequestBody QueryRequest queryRequest) {
        Optional<User> user = userRepository.findById(queryRequest.getId());
        if (user.isPresent()) {
            Response response = new Response("success", HttpStatus.OK.value(), user.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            Response response = new Response("user not found", HttpStatus.NOT_FOUND.value(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    static class QueryRequest {
        private String id;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    static class ListResponse {
        private String msg;
        private int code;
        private List<User> data;

        public ListResponse(String msg, int code, List<User> data) {
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

        public List<User> getData() {
            return data;
        }

        public void setData(List<User> data) {
            this.data = data;
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

    static class LoginRequest {
        private String id;
        private String password;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

