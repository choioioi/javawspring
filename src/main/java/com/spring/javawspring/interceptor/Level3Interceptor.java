package com.spring.javawspring.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class Level3Interceptor extends HandlerInterceptorAdapter { // 컨트롤 누르고 HandlerInterceptorAdapter 들어가봄. 이 밑에 프리핸들을 저 부모한테서 가져온 것
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession(); // 얘는 이 위에 괄호안에 못넣고 이렇게 선언해야함
		int level = session.getAttribute("sLevel")==null? 99 : (int) session.getAttribute("sLevel");
		if(level > 3) {
			RequestDispatcher dispatcher;
			if(level == 99) { // 레벨이 99라면 비회원
				dispatcher = request.getRequestDispatcher("/msg/memberNo");
			}
			else { // 준회원(level:4)인 경우 pds에 들어갔을 때 
				dispatcher = request.getRequestDispatcher("/msg/levelCheckNo");
			}
			dispatcher.forward(request, response);
			return false;
		}	
		return true;
	}
}
