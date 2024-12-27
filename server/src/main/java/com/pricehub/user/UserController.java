package com.pricehub;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/create")
    public ResponseEntity<Response> addUser(@RequestBody User user) {
        if (userRepository.findById(user.getId()).isPresent()) {
            Response response = new Response("user already exists", HttpStatus.BAD_REQUEST.value(), "用户已存在");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            Response response = new Response("email already exists", HttpStatus.BAD_REQUEST.value(), "邮箱已存在");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
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

    @PostMapping("/reset")
    public ResponseEntity<Response> resetPassword(@RequestBody SetPasswordRequest request) {
        // 验证邮箱是否存在
        User user = userService.getUserByEmail(request.getEmail());

        if (user == null) {
            Response response = new Response("email not found", HttpStatus.NOT_FOUND.value(), "邮箱不存在");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // 验证验证码
        boolean isVerified = userService.verifyVerificationCode(user.getEmail(), request.getVerificationCode());
        if (!isVerified) {
            Response response = new Response("invalid verification code", HttpStatus.BAD_REQUEST.value(), "验证码无效");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // 更新密码
        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        Response response = new Response("password reset successful", HttpStatus.OK.value(), "密码重置成功");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/sendver")
    public ResponseEntity<Response> sendVerificationCode(@RequestParam String email) {
        // // 验证邮箱是否存在 
        // User user = userService.getUserByEmail(email);

        // if (user == null) {
        //     Response response = new Response("email not found", HttpStatus.NOT_FOUND.value(), "邮箱不存在");
        //     return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        // }

        // 生成验证码
        String verificationCode = generateVerificationCode();

        // 发送验证码邮件
        String subject = "PriceHub 验证码";
        String content = "您的验证码是：" + verificationCode + "，请在5分钟内使用。";
        emailService.sendPriceDropEmail(email, subject, content);

        // 保存验证码到数据库
        userService.saveVerificationCode(email, verificationCode);

        Response response = new Response("verification code sent", HttpStatus.OK.value(), "验证码已发送");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000到999999之间的随机数
        return String.valueOf(code);
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

    static class SetPasswordRequest {
        private String email; // 用户邮箱
        private String verificationCode; // 验证码
        private String newPassword; // 新密码

        // Getters and Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getVerificationCode() {
            return verificationCode;
        }

        public void setVerificationCode(String verificationCode) {
            this.verificationCode = verificationCode;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}

