package com.spring.javawspring.pagination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.javawspring.dao.GuestDAO;
import com.spring.javawspring.dao.MemberDAO;

@Service
public class PageProcess {
	@Autowired
	GuestDAO guestDAO;
	
	@Autowired
	MemberDAO memberDAO;

	public PageVO totRecCnt(int pag, int pageSize, String section, String part, String searchString) {
		PageVO pageVO = new PageVO();
		
		int totRecCnt = 0;
		
		if(section.equals("member")) { // 섹션이 멤버면
			totRecCnt = memberDAO.totRecCnt(); // 멤버 dao에서 totRecCnt 가져옴
		}	
		else if(section.equals("guest")) {
			totRecCnt = guestDAO.totRecCnt();
		}
		
		int totPage = (totRecCnt % pageSize)==0 ? totRecCnt / pageSize : (totRecCnt / pageSize) + 1; 
		int startIndexNo = (pag - 1) * pageSize; // 5. 현재 페이지의 시작 인덱스 번호를 구한다.
		int curScrStartNo = totRecCnt - startIndexNo; // 6. 현재 화면에 보여주는 시작 번호를 구한다.
		
		int blockSize = 3; // 1. 블록의 크기를 결정한다. (여기선 3으로 지정)
		int curBlock = (pag - 1) / blockSize; // 2. 현재 페이지가 위치하고 있는 블록 번호를 구한다.(예:1페이지는 0블록, 3페이지는 0블록, 5페이지는 1블록) 123페이지, 0블록 456페이지 1블록, 789페이지 2블록으로 쌤이 설정하심
		int lastBlock = (totPage - 1) / blockSize; // 3. 마지막 블록을 구한다.
		
		pageVO.setPag(pag);
		pageVO.setPageSize(pageSize);
		pageVO.setTotRecCnt(totRecCnt);
		pageVO.setTotPage(totPage);
		pageVO.setStartIndexNo(startIndexNo);
		pageVO.setCurScrStartNo(curScrStartNo);
		pageVO.setBlockSize(blockSize);
		pageVO.setCurBlock(curBlock);
		pageVO.setLastBlock(lastBlock);
		
		return pageVO;
	}
}
