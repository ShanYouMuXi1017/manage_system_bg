package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CourseBasicInformationMAPPER;
import com.example.mapper.CourseExamineChildMethodsMAPPER;
import com.example.mapper.CourseExamineMethodsMAPPER;
import com.example.mapper.CourseTargetMAPPER;
import com.example.mapper.examinePaper.CourseFinalExamPaperDetailMAPPER;
import com.example.mapper.examinePaper.CourseFinalExamPaperMAPPER;
import com.example.object.*;
import com.example.object.finalExamine.CourseFinalExamPaper;
import com.example.object.finalExamine.CourseFinalExamPaperDetail;
import com.example.service.CourseBasicInformationSERVICE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseBasicInformationServiceIMPL extends ServiceImpl<CourseBasicInformationMAPPER, CourseBasicInformation> implements CourseBasicInformationSERVICE {

    @Autowired
    private CourseBasicInformationMAPPER courseMapper;

    @Autowired
    private CourseTargetMAPPER courseTarget;

    @Autowired
    private CourseBasicInformationMAPPER courseBasicInformationMAPPER;

    @Autowired
    private CourseExamineMethodsMAPPER courseExamineMethodsMAPPER;

    @Autowired
    private CourseExamineChildMethodsMAPPER courseExamineChildMethodsMAPPER;

    @Autowired
    private CourseFinalExamPaperMAPPER courseFinalExamPaperMAPPER;

    @Autowired
    private CourseFinalExamPaperDetailMAPPER courseFinalExamPaperDetailMAPPER;

    @Override
    public List<Map<String, Object>> getCourseTree() {
        List<CourseSyllabusInformation> courses = courseMapper.getCourses();
        Map<String, List<String>> courseMap = new LinkedHashMap<>();

        for (CourseSyllabusInformation course : courses) {
            courseMap
                    .computeIfAbsent(course.getMajor(), k -> new ArrayList<>())
                    .add(course.getCourseName());
        }

        List<Map<String, Object>> treeData = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : courseMap.entrySet()) {
            Map<String, Object> majorNode = new HashMap<>();
            majorNode.put("label", entry.getKey());
            List<Map<String, String>> children = new ArrayList<>();
            for (String courseName : entry.getValue()) {
                Map<String, String> courseNode = new HashMap<>();
                courseNode.put("label", courseName);
                children.add(courseNode);
            }
            majorNode.put("children", children);
            treeData.add(majorNode);
        }

        return treeData;
    }

    @Override
    public List<CourseBasicInformation> couresNameList(String coursename) {
        return courseMapper.courseMapper(coursename);
    }

    @Override
    public int getNewCourseId() {
        return courseMapper.getNewCourseId();
    }


    @Override
    public boolean saveInheritCourseTarget(Integer afterSaveCourseId, Integer toJiChengCourseId) {

        QueryWrapper<CourseTarget> courseTargetQueryWrapper = new QueryWrapper<>();
        courseTargetQueryWrapper.eq("course_id",toJiChengCourseId);
        List<CourseTarget> toJiChengCourseTargets = courseTarget.selectList(courseTargetQueryWrapper);
        int size = toJiChengCourseTargets.size();

        int [] flag = new int[size];
        boolean saveFlag = false;

        //查询通过继承创建的课程
        CourseBasicInformation afterCourse = courseBasicInformationMAPPER.selectById(afterSaveCourseId);
        String afterCourseCourseName = afterCourse.getCourseName();

        //填充课程目标
        for(int i = 0;i < size; i++){
            CourseTarget target = new CourseTarget();
            target.setCourseId(afterSaveCourseId);
            target.setCourseName(afterCourseCourseName);
            target.setTargetName(toJiChengCourseTargets.get(i).getTargetName());
            target.setWeight(toJiChengCourseTargets.get(i).getWeight());
            target.setCourseTarget(toJiChengCourseTargets.get(i).getCourseTarget());
            target.setPathWays(toJiChengCourseTargets.get(i).getPathWays());
            target.setIndicatorPoints(toJiChengCourseTargets.get(i).getIndicatorPoints());
            target.setEvaluationMethod(toJiChengCourseTargets.get(i).getEvaluationMethod());
            flag[i] = courseTarget.insert(target);
        }
        saveFlag = !toJiChengCourseTargets.isEmpty() && Arrays.stream(flag).allMatch(value -> value == 1);
        return saveFlag;
    }

    @Override
    public boolean saveInheritCourseMethod(Integer afterSaveCourseId, Integer toJiChengCourseId) {
        QueryWrapper<CourseExamineMethods> courseExamineMethodsQueryWrapper = new QueryWrapper<>();
        courseExamineMethodsQueryWrapper.eq("course_id",toJiChengCourseId);
        List<CourseExamineMethods> toJiChengCourseMethods = courseExamineMethodsMAPPER.selectList(courseExamineMethodsQueryWrapper);
        int size = toJiChengCourseMethods.size();

        //保存插入课程考核评价方式后返回的数据
        int [] flag = new int[size];

        //保存课程考核评价方式在表中的id
        int [] courseMethCount = new int[size];

        //保存是否插入数据成功
        boolean insertFlag = false;
        boolean insertFlag1 = false;
        boolean insertFlag2 = false;

        //绑定父课程与子课程的考核评价方式id的关系
        HashMap<Integer, Integer> connectionById = new HashMap<>();

        //查询通过继承创建的课程
        CourseBasicInformation afterCourse = courseBasicInformationMAPPER.selectById(afterSaveCourseId);
        String afterCourseCourseName = afterCourse.getCourseName();

        //填充课程考核评价方式
        if(!toJiChengCourseMethods.isEmpty()){
            for(int i = 0;i < toJiChengCourseMethods.size(); i++){
                CourseExamineMethods courseExamineMethods = new CourseExamineMethods();
                courseExamineMethods.setCourseId(afterSaveCourseId);
                courseExamineMethods.setCourseName(afterCourseCourseName);
                courseExamineMethods.setExamineItem(toJiChengCourseMethods.get(i).getExamineItem());
                courseExamineMethods.setPercentage(toJiChengCourseMethods.get(i).getPercentage());
                courseExamineMethods.setItemScore(toJiChengCourseMethods.get(i).getItemScore());

                //保存课程考核评价方式在表中的id
                courseMethCount[i] = toJiChengCourseMethods.get(i).getId();

                flag[i] = courseExamineMethodsMAPPER.insert(courseExamineMethods);
                //绑定父表与子表的关系
                connectionById.put(toJiChengCourseMethods.get(i).getId(),courseExamineMethods.getId());
            }
        }

        //如果课程考核评价方式父表插入成功，则处理课程考核评价方式的子表
        if(Arrays.stream(flag).allMatch(value -> value == 1) && !toJiChengCourseMethods.isEmpty()){
            QueryWrapper<CourseExamineChildMethods> queryWrapper1 = new QueryWrapper<>();
            QueryWrapper<CourseExamineChildMethods> queryWrapper2 = new QueryWrapper<>();
            queryWrapper1.eq("course_examine_methods_id",courseMethCount[0]);
            queryWrapper2.eq("course_examine_methods_id",courseMethCount[1]);
            List<CourseExamineChildMethods> courseExamineChildMethods1 = courseExamineChildMethodsMAPPER.selectList(queryWrapper1);
            List<CourseExamineChildMethods> courseExamineChildMethods2 = courseExamineChildMethodsMAPPER.selectList(queryWrapper2);

            int [] flag1 = new int[courseExamineChildMethods1.size()];
            int [] flag2 = new int[courseExamineChildMethods2.size()];


            if(!courseExamineChildMethods1.isEmpty()){
                for(int i = 0;i < courseExamineChildMethods1.size(); i++){
                    CourseExamineChildMethods examineChildMethods = new CourseExamineChildMethods();
                    examineChildMethods.setCourseExamineMethodsId(connectionById.get(courseExamineChildMethods1.get(i).getCourseExamineMethodsId()));
                    examineChildMethods.setExamineChildItem(courseExamineChildMethods1.get(i).getExamineChildItem());
                    examineChildMethods.setChildPercentage(courseExamineChildMethods1.get(i).getChildPercentage());
                    examineChildMethods.setChildScore(courseExamineChildMethods1.get(i).getChildScore());
                    examineChildMethods.setCourseTarget(courseExamineChildMethods1.get(i).getCourseTarget());
                    examineChildMethods.setIndicatorPointsDetail(courseExamineChildMethods1.get(i).getIndicatorPointsDetail());
                    flag1[i] = courseExamineChildMethodsMAPPER.insert(examineChildMethods);
                }
            }
            if(!courseExamineChildMethods2.isEmpty()){
                for(int i = 0;i < courseExamineChildMethods2.size(); i++){
                    CourseExamineChildMethods examineChildMethods = new CourseExamineChildMethods();
                    examineChildMethods.setCourseExamineMethodsId(connectionById.get(courseExamineChildMethods2.get(i).getCourseExamineMethodsId()));
                    examineChildMethods.setExamineChildItem(courseExamineChildMethods2.get(i).getExamineChildItem());
                    examineChildMethods.setChildPercentage(courseExamineChildMethods2.get(i).getChildPercentage());
                    examineChildMethods.setChildScore(courseExamineChildMethods2.get(i).getChildScore());
                    examineChildMethods.setCourseTarget(courseExamineChildMethods2.get(i).getCourseTarget());
                    examineChildMethods.setIndicatorPointsDetail(courseExamineChildMethods2.get(i).getIndicatorPointsDetail());
                    flag2[i] = courseExamineChildMethodsMAPPER.insert(examineChildMethods);
                }
            }
            insertFlag1 = !courseExamineChildMethods1.isEmpty() && Arrays.stream(flag1).allMatch(value -> value == 1);
            insertFlag2 = !courseExamineChildMethods2.isEmpty() && Arrays.stream(flag2).allMatch(value -> value == 1);
        }
        insertFlag = !toJiChengCourseMethods.isEmpty() && Arrays.stream(flag).allMatch(value -> value == 1);
        return insertFlag || insertFlag1 || insertFlag2;
    }

    @Override
    public boolean saveInheritCoursePaper(Integer afterSaveCourseId, Integer toJiChengCourseId) {
        //对课程考核评价方式中父表的查找
        QueryWrapper<CourseExamineMethods> courseExamineMethodsQueryWrapper = new QueryWrapper<>();
        courseExamineMethodsQueryWrapper.eq("course_id",toJiChengCourseId);
        courseExamineMethodsQueryWrapper.eq("examine_item","期末考核成绩");
        CourseExamineMethods QiMoExamineMethod = courseExamineMethodsMAPPER.selectOne(courseExamineMethodsQueryWrapper);

        QueryWrapper<CourseExamineMethods> courseExamineMethodsQueryWrapper1 = new QueryWrapper<>();
        courseExamineMethodsQueryWrapper1.eq("course_id",afterSaveCourseId);
        courseExamineMethodsQueryWrapper1.eq("examine_item","期末考核成绩");
        CourseExamineMethods QiMoExamineMethod1 = courseExamineMethodsMAPPER.selectOne(courseExamineMethodsQueryWrapper1);



        //对课程考核方式中子表的查找
        QueryWrapper<CourseExamineChildMethods> childMethodsQueryWrapper = new QueryWrapper<>();
        childMethodsQueryWrapper.eq("course_examine_methods_id",QiMoExamineMethod.getId());
        childMethodsQueryWrapper.eq("examine_child_item","试卷");
        CourseExamineChildMethods courseExamineChildMethods = courseExamineChildMethodsMAPPER.selectOne(childMethodsQueryWrapper);

        QueryWrapper<CourseExamineChildMethods> childMethodsQueryWrapper1 = new QueryWrapper<>();
        childMethodsQueryWrapper1.eq("course_examine_methods_id",QiMoExamineMethod1.getId());
        childMethodsQueryWrapper1.eq("examine_child_item","试卷");
        CourseExamineChildMethods courseExamineChildMethods1 = courseExamineChildMethodsMAPPER.selectOne(childMethodsQueryWrapper1);

        //对试卷设置中父表的查找
        QueryWrapper<CourseFinalExamPaper> finalExamPaperQueryWrapper = new QueryWrapper<>();
        finalExamPaperQueryWrapper.eq("exam_child_method_id",courseExamineChildMethods.getId());
        List<CourseFinalExamPaper> finalExamPaperList = courseFinalExamPaperMAPPER.selectList(finalExamPaperQueryWrapper);
        int [] saveFlag = new int[finalExamPaperList.size()];
        //设置保存成功的标志
        boolean flag = false;
        //保存继承的表与被继承的表id的关系
        HashMap<Integer, Integer> idConnectionMap = new HashMap<>();
        //填充父表
        for(int i = 0;i < finalExamPaperList.size();i++){
            CourseFinalExamPaper courseFinalExamPaper = new CourseFinalExamPaper();
            courseFinalExamPaper.setExamChildMethodId(courseExamineChildMethods1.getId());
            courseFinalExamPaper.setItemName(finalExamPaperList.get(i).getItemName());
            courseFinalExamPaper.setItemScore(finalExamPaperList.get(i).getItemScore());
            //返回 1 则插入成功
            saveFlag[i] = courseFinalExamPaperMAPPER.insert(courseFinalExamPaper);
            idConnectionMap.put(finalExamPaperList.get(i).getId(),courseFinalExamPaper.getId());

        }
        //填充子表
        for(int i = 0; i < finalExamPaperList.size();i++){
            //通过父表查询子表
            QueryWrapper<CourseFinalExamPaperDetail> paperDetailQueryWrapper = new QueryWrapper<>();
            paperDetailQueryWrapper.eq("primary_id",finalExamPaperList.get(i).getId());
            List<CourseFinalExamPaperDetail> detailList = courseFinalExamPaperDetailMAPPER.selectList(paperDetailQueryWrapper);
            //开始填充子表
            for(int j = 0; j < detailList.size(); j++){
                CourseFinalExamPaperDetail examPaperDetail = new CourseFinalExamPaperDetail();
                examPaperDetail.setPrimaryId(idConnectionMap.get(finalExamPaperList.get(i).getId()));
                examPaperDetail.setTitleNumber(detailList.get(j).getTitleNumber());
                examPaperDetail.setScore(detailList.get(j).getScore());
                examPaperDetail.setIndicatorPoints(detailList.get(j).getIndicatorPoints());
                examPaperDetail.setCourseTarget(detailList.get(j).getCourseTarget());
                courseFinalExamPaperDetailMAPPER.insert(examPaperDetail);
            }
        }
        //当表不为空，并且全部都插入成功返回true
        flag = !finalExamPaperList.isEmpty() && Arrays.stream(saveFlag).allMatch(value -> value == 1);
        return flag;
    }

}
