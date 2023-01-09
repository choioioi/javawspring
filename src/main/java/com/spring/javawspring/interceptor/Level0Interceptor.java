package com.spring.javawspring.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class Level0Interceptor extends HandlerInterceptorAdapter { // 컨트롤 누르고 HandlerInterceptorAdapter 들어가봄. 이 밑에 프리핸들을 저 부모한테서 가져온 것
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession(); // 얘는 이 위에 괄호안에 못넣고 이렇게 선언해야함
		int level = session.getAttribute("sLevel")==null? 99 : (int) session.getAttribute("sLevel");
		if(level != 0) { // 관리자가 아닌 경우엔 메세지를 통해서 무조건 초기화면창으로 보낸다.
			RequestDispatcher dispatcher = request.getRequestDispatcher("/msg/adminNo");
			dispatcher.forward(request, response);
			return false;
		}
		return true;
	}
}
