package com.tensquare.manager.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;

@Component
public class ManagerFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";// 前置过滤器
    }

    @Override
    public int filterOrder() {
        return 0; // 优先级为0，数字越大，优先级越低
    }

    @Override
    public boolean shouldFilter() {
        return true; // 是否执行该过滤器，此处为true，说明需要过滤
    }

    @Autowired
    private JwtUtil jwtUtil;
    
    
    @Override
    public Object run() throws ZuulException {
        System.out.println("Zuul过滤器");
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();

        //跨域访问
        if(request.getMethod().equals("OPTIONS")){
            return null;
        }

        //登录
        String url = request.getRequestURL().toString();
        if(url.contains("/admin/login")){
            System.out.println("登陆页面"+url);
            return null;
        }


        String requestHeader = request.getHeader("Authorization");
       if(requestHeader!=null&&requestHeader.startsWith("Bearer ")){
            String token = requestHeader.substring(7);
            Claims claims = jwtUtil.parseJWT(token);
            if(claims!=null){
                if("admin".equals(claims.get("roles"))){
                    currentContext.addZuulRequestHeader("Authorization",requestHeader);
                    return null;
                }

            }
        }
        currentContext.setSendZuulResponse(false); //阻止向下执行
        currentContext.setResponseStatusCode(401); //http状态码
        currentContext.setResponseBody("无权访问");
        currentContext.getResponse().setContentType("text/html;charset=utf-8");

        return null; //放行
    }
}
