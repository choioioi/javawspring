package com.spring.javawspring.service;

import java.util.List;

import com.spring.javawspring.vo.GuestVO;

public interface GuestService {

	public List<GuestVO> getGuestList(int startIndexNo, int pageSize);

	public void setGuestInput(GuestVO vo); // 방명록 글쓰기

	public int totRecCnt();

	public void setGuestDelete(int idx); // 방명록 삭제

}
