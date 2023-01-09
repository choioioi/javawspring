package com.spring.javawspring.service;

import java.util.ArrayList;

import com.spring.javawspring.vo.MemberVO;

public interface MemberService {

	public MemberVO getMemberIdCheck(String mid);

	public MemberVO getMemberNickNameCheck(String nickName);

	public int setMemberJoinOk(MemberVO vo);

	public void setMemberVisitProcess(MemberVO vo);

	public int totRecCnt(); // 전체리스트꺼, 주석처리 했지만 걍 공부하게 냅둠

	public ArrayList<MemberVO> getMemberList(int startIndexNo, int pageSize);

	public int totTermRecCnt(String mid);

	public ArrayList<MemberVO> getTermMemberList(int startIndexNo, int pageSize, String mid);

}
