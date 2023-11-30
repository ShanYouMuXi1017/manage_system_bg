package com.example.object.courseSurvey;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SurveyVO {
    private Integer courseTargetId; //课程目标id
    private String courseTarget; //课程目标详情
    private int gradeA; //选项 A 人数
    private int gradeB; //选项 B 人数
    private int gradeC; //选项 B 人数
    private int gradeD; //选项 B 人数
    private int total; //当前课程指定课程目标总人数

    //获取选项 A 的占比
    public double getGradeAPercent(){
        if (this.total==0){
            return 0d;
        }else {
            BigDecimal c = new BigDecimal(this.gradeA).divide(new BigDecimal(this.total), 4, BigDecimal.ROUND_HALF_UP);
            return c.doubleValue();
        }
    }

    //获取选项 B 的占比
    public double getGradeBPercent(){
        if (this.total==0){
            return 0d;
        }else {
            BigDecimal c = new BigDecimal(this.gradeB).divide(new BigDecimal(this.total), 4, BigDecimal.ROUND_HALF_UP);
            return c.doubleValue();
        }
    }

    //获取选项 C 的占比
    public double getGradeCPercent(){
        if (this.total==0){
            return 0d;
        }else {
            BigDecimal c = new BigDecimal(this.gradeC).divide(new BigDecimal(this.total), 4, BigDecimal.ROUND_HALF_UP);
            return c.doubleValue();
        }
    }

    //获取选项 D 的占比
    public double getGradeDPercent(){
        if (this.total==0){
            return 0d;
        }else {
            BigDecimal c = new BigDecimal(this.gradeD).divide(new BigDecimal(this.total), 4, BigDecimal.ROUND_HALF_UP);
            return c.doubleValue();
        }
    }


}
