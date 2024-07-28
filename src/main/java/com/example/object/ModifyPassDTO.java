package com.example.object;

import lombok.Data;

/**
 * @ Description： 密码修改的模型
 * @ Author： 程序员好冰
 * @ Date： 2024/03/07/09:27
 */
@Data
public class ModifyPassDTO {
    private Integer id;
    private String oldPassword;
    private String password;
}
