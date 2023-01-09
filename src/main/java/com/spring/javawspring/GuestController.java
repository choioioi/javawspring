package com.spring.javawspring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.javawspring.pagination.PageProcess;
import com.spring.javawspring.pagination.PageVO;
import com.spring.javawspring.service.GuestService;
import com.spring.javawspring.vo.GuestVO;
import com.spring.javawspring.vo.MemberVO;

@Controller
@RequestMapping("/guest")
public class GuestController {

	@Autowired
	GuestService guestService;
	
	@Autowired
	PageProcess pageProcess;
	
	/*
	@RequestMapping(value = "/guestList", method=RequestMethod.GET)
	public String guestListGet(Model model,
			@RequestParam(name="pag", defaultValue = "1" , required = false) int pag) {
		// 1. 페이지(pag)를 결정한다.
		int pageSize = 3; // 2. 한 페이지의 분량을 결정한다.
		int totRecCnt = guestService.totRecCnt(); // 3. 총 레코드 건수를 구한다.
		int totPage = (totRecCnt % pageSize)==0 ? totRecCnt / pageSize : (totRecCnt / pageSize) + 1; // 4. 총 페이지 건수를 구한다.
		int startIndexNo = (pag - 1) * pageSize; // 5. 현재 페이지의 시작 인덱스 번호를 구한다.
		int curScrStartNo = totRecCnt - startIndexNo; // 6. 현재 화면에 보여주는 시작 번호를 구한다.
		
		int blockSize = 3; // 1. 블록의 크기를 결정한다. (여기선 3으로 지정)
		int curBlock = (pag - 1) / blockSize; // 2. 현재 페이지가 위치하고 있는 블록 번호를 구한다.(예:1페이지는 0블록, 3페이지는 0블록, 5페이지는 1블록) 123페이지, 0블록 456페이지 1블록, 789페이지 2블록으로 쌤이 설정하심
		int lastBlock = (totPage - 1) / blockSize; // 3. 마지막 블록을 구한다.
		
		// ArrayList<GuestVO> vos = dao.getGuestList(); // 여러건 가져올 수 있으니 ArrayList사용, 데이터베이스꺼 가져와서 vos에 넣음, 이건 전부 가져온거라 밑에껀 3개씩 가져오는 것
		ArrayList<GuestVO> vos = guestService.getGuestList(startIndexNo, pageSize); // 시작인덱스번호, 가져올건수
		
		model.addAttribute("vos", vos); // 5건 담김
		model.addAttribute("pag", pag); 
		model.addAttribute("totPage", totPage); 
		model.addAttribute("curScrStartNo", curScrStartNo);
		model.addAttribute("blockSize", blockSize);
		model.addAttribute("curBlock", curBlock);
		model.addAttribute("lastBlock", lastBlock);
		
		return "guest/guestList";
	}
	*/
	// Pagination을 이용한 리스트 처리
	@RequestMapping(value = "/guestList", method=RequestMethod.GET)
	public String guestListGet(Model model,
			@RequestParam(name="pag", defaultValue = "1" , required = false) int pag,
			@RequestParam(name="pageSize", defaultValue = "3", required = false) int pageSize) {
		
		PageVO pageVo = pageProcess.totRecCnt(pag, pageSize, "guest", "", "");
		
		List<GuestVO> vos = guestService.getGuestList(pageVo.getStartIndexNo(), pageSize);
		
		model.addAttribute("vos", vos); // 5건 담김
		model.addAttribute("pageVo", pageVo); 
		
		return "guest/guestList";
	}
	
	/*
	 @RequestMapping(value = "/memberList", method = RequestMethod.GET)
		public String memberListGet(Model model,
				@RequestParam(name="mid", defaultValue = "", required = false) String mid, // 젤 첨엔 아디 안들어오니 "" 널값처리 해준 것
				@RequestParam(name="pag", defaultValue = "1", required = false) int pag,
				@RequestParam(name="pageSize", defaultValue = "3", required = false) int pageSize) {
			
			//int totRecCnt = memberService.totTermRecCnt(); 전체검색은 이거인데 조건검색을 위해 매개변수 mid 넘김
			PageVO pageVo = pageProcess.totRecCnt(pag, pageSize, "member", "", ""); // totRecCnt를 pageProcess 얘를 통해서 구해오겠다
			
			List<MemberVO> vos = memberService.getMemberList(pageVo.getStartIndexNo(), pageSize); // 시작인덱스번호, 가져올건수, 조건검색 위한 mid
																																					// 249번 라인에서 pageSize는 구했음				
			model.addAttribute("vos", vos); // 5건 담김
			model.addAttribute("pageVo", pageVo); 
			
			
			return "member/memberList";
	 */
	
	@RequestMapping(value = "/guestInput", method=RequestMethod.GET)
	public String guestListGet() {
		return "guest/guestInput";
	}
	
	@RequestMapping(value = "/guestInput", method=RequestMethod.POST)
	public String guestListPost(GuestVO vo) {
		guestService.setGuestInput(vo);
		
		return "redirect:/msg/guestInputOk";
	}
	
	// 방명록 삭제
	@RequestMapping(value = "/guestDelete", method = RequestMethod.GET)
	public String guestDeleteGet(int idx) {
		guestService.setGuestDelete(idx);
		
		return "redirect:/msg/guestDeleteOk";
	}
}
