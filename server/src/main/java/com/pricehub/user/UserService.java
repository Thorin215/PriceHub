package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 根据用户ID查询邮件
    public String getUserEmailById(String userId) {
        System.out.println("user:" + userId);
        User user = userRepository.findById(userId).orElse(null);
        return (user != null) ? user.getEmail() : null;

    }

    public String getUserName(String userId) {
        System.out.println("user:" + userId);
        User user = userRepository.findById(userId).orElse(null);
        return (user != null) ? user.getName():null;

    }

    // 根据用户邮箱查询用户信息
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null); // 如果找不到该邮箱的用户，返回null
    }
}
