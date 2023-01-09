package com.spring.javawspring.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
// 회원 전부 허용
public class Level4Interceptor extends HandlerInterceptorAdapter { // 컨트롤 누르고 HandlerInterceptorAdapter 들어가봄. 이 밑에 프리핸들을 저 부모한테서 가져온 것
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession(); // 얘는 이 위에 괄호안에 못넣고 이렇게 선언해야함
		int level = session.getAttribute("sLevel")==null? 99 : (int) session.getAttribute("sLevel");
		
		if(level > 4) { // 비회원일 때
			RequestDispatcher dispatcher = request.getRequestDispatcher("/msg/memberNo"); // '로그인 후 이용' 메세지 뜸
			dispatcher.forward(request, response);
			return false;
		}	
		return true;
	}
}
