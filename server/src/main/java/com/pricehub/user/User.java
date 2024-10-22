package com.pricehub; 

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    private String id;
    private String name;
    private String password;
    private String email; // 新增邮箱属性

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() { // 新增邮箱的 Getter
        return email;
    }

    public void setEmail(String email) { // 新增邮箱的 Setter
        this.email = email;
    }
}

