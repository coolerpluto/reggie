package com.fan.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.fan.reggie.common.BaseContext;
import com.fan.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
       HttpServletRequest request = (HttpServletRequest) servletRequest;
       HttpServletResponse response = (HttpServletResponse) servletResponse;
       String requestURI = request.getRequestURI();
       log.info("拦截到请求：{}",requestURI);
       String[] urls = new String[]{
                "/employee/logout",
                "/employee/login",
                "/backend/**",
                "/front/**"
       };

       Boolean needLoginUrl = check(urls,requestURI);

       if (!needLoginUrl){
           filterChain.doFilter(request,response);
           return;
       }

       if (request.getSession().getAttribute("employee")!=null){
           log.info("session:{}",request.getSession().getAttribute("employee"));
           log.info("session创建时间是:{}", request.getSession().getAttribute("sessionTime"));
           log.info("当前线程id：{}",Thread.currentThread().getId());
           Long empId = (Long)request.getSession().getAttribute("employee");
           BaseContext.setCurrentUserId(empId);
           filterChain.doFilter(request,response);
           return;
       }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
       return;


    }


    public Boolean check(String[] urls, String requestUrl){
        Boolean needLoginUrl = true;
        for (String url : urls){
            if (PATH_MATCHER.match(url, requestUrl)){
                needLoginUrl = false;
            }
        }
        return needLoginUrl;
    }
}
