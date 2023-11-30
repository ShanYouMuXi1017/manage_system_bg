package com.example.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.example.object.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 本list仅做模拟，实际要根据具体业务逻辑来查询角色
        List<String> list = new ArrayList<>();
        String id = (String)loginId;
        if (id.startsWith("admin_")) {
            User user = (User) StpUtil.getSession().get(StpUtil.getLoginIdAsString());
            list.add(String.valueOf(user.getIsAdmin()));
        }else {
            list.add("student");
        }
        return list;
    }
}
