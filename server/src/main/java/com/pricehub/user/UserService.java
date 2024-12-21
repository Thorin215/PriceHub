package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeRecordRepository codeRecordRepository;

    @Autowired
    public UserService(UserRepository userRepository, CodeRecordRepository codeRecordRepository) {
        this.userRepository = userRepository;
        this.codeRecordRepository = codeRecordRepository;
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

    public void saveVerificationCode(String userId, String code) {
        // 尝试查找该用户的验证码记录
        CodeRecord existingCodeRecord = codeRecordRepository.findByUserId(userId);

        if (existingCodeRecord != null) {
            // 如果存在，更新验证码和创建时间
            existingCodeRecord.setCode(code);
            existingCodeRecord.setCreatedAt(new Date());
            codeRecordRepository.save(existingCodeRecord); // 更新记录
        } else {
            // 如果不存在，创建新的验证码记录
            CodeRecord newCodeRecord = new CodeRecord();
            newCodeRecord.setUserId(userId);
            newCodeRecord.setCode(code);
            newCodeRecord.setCreatedAt(new Date());
            codeRecordRepository.save(newCodeRecord); // 保存新记录
        }
    }

    public boolean verifyVerificationCode(String userId, String userInputCode) {
        CodeRecord codeRecord = codeRecordRepository.findByUserId(userId);

        if (codeRecord == null) {
            System.out.println("验证码不存在");
            return false;
        }

        if (codeRecord.isExpired()) {
            System.out.println("验证码已过期");
            codeRecordRepository.delete(codeRecord);
            return false;
        }

        if (!codeRecord.getCode().equals(userInputCode)) {
            System.out.println("验证码不匹配");
            return false;
        }

        // 验证成功后删除验证码
        codeRecordRepository.delete(codeRecord);
        System.out.println("验证码验证成功");
        return true;
    }
}
