package com.example.object.finalExamine;

import lombok.Data;

@Data
public class CourseFinalExamPaper {
    private Integer id;
    //外键考察评价方式表
    private int examChildMethodId;
    //项目名称
    private String itemName;
    //项目分数
    private int itemScore;
}

