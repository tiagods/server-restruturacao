package com.tiagods.prolink.config;

import com.tiagods.prolink.utils.ContextHeaders;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

@WebFilter("/api/*")
public class WebConfiguration implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        /*
        String cid = httpRequest.getHeader("cid");

        if(cid == null) {
            cid = UUID.randomUUID().toString();
        }

        httpResponse.addHeader("cid", cid);
        */

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {}
}
