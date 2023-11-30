package com.example.object;

import lombok.Data;

@Data
public class EducationProgram {
    private Integer id;//主键Id
    private String major;//专业名称
    private String majorVersion;//版本号
    private int isLoad;//是否上传
    /*private String createTime;//创建时间*/

}
