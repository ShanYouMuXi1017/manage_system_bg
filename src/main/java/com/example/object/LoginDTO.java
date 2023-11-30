package com.example.object;

import lombok.Data;

@Data
public class LoginDTO {
    private String name;
    private String password;
    /**
     * 0  admin表
     * 1  student_information表
     */
    private String identity;
}
