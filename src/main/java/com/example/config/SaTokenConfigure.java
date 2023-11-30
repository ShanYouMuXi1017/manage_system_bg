package com.example.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // Sa-Token 参数配置，参考文档：https://sa-token.cc
    // 此配置会覆盖 application.yml 中的配置
    @Bean
    @Primary
    public SaTokenConfig getSaTokenConfigPrimary() {
        SaTokenConfig config = new SaTokenConfig();
        config.setTokenName("satoken");             // token名称 (同时也是cookie名称)
        config.setTimeout(24 * 60 * 60);            // token有效期，单位s 默认 1 天
        config.setActivityTimeout(-1);              // token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒
        config.setIsConcurrent(true);               // 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
        config.setIsShare(true);                    // 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token)
        config.setTokenStyle("simple-uuid");        // token风格
        config.setIsLog(false);                     // 是否输出操作日志
        //config.cookie.setHttpOnly(true);          // 是否禁止 js 操作 Cookie
        return config;
    }


    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，定义详细认证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 指定一条 match 规则
            SaRouter.match("/**")                   // 拦截的 path 列表，可以写多个 */
                    .notMatch("/user/doLogin")               // 排除掉的 path 列表，可以写多个
                    .notMatch("/swagger-ui/**")              //取消拦截swagger文档
                    .notMatch("/courseExamPaper/Table/*")    //取消拦截文件导出
                    .notMatch("/manager/**")    //取消拦截文件导出
                    .notMatch("/report/**")    //取消拦截文件导出
                    .check(r -> StpUtil.checkLogin());       // 要执行的校验动作，可以写完整的 lambda 表达式
            // 根据路由划分模块，不同模块不同鉴权
            SaRouter.match("/user/choiceRole", r -> StpUtil.checkRoleOr("3","4","5","6"));
        })).addPathPatterns("/**");
    }
}
