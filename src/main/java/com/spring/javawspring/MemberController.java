package com.spring.javawspring;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.javawspring.pagination.PageProcess;
import com.spring.javawspring.pagination.PageVO;
import com.spring.javawspring.service.MemberService;
import com.spring.javawspring.vo.MemberVO;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	MemberService memberService;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	PageProcess pageProcess; // 여태는 인터페이스를 불렀는데 클래스가 와도 상관없다
	
	@RequestMapping(value = "/memberLogin", method=RequestMethod.GET) // 로그인창 띄움
	public String memberLoginGet(HttpServletRequest request) {
		// 로그인 폼 호출시에 기존의 저장된 쿠키가 있다면 불러와서 mid에 담아서 넘겨준다
		Cookie[] cookies = request.getCookies();
		for(int i=0; i<cookies.length; i++) {
			if(cookies[i].getName().equals("cMid")) {
				request.setAttribute("mid", cookies[i].getValue());
				break;
			}
		}
		return "member/memberLogin";
	}
	@RequestMapping(value = "/memberLogin", method=RequestMethod.POST) // 로그인하러 감
	public String memberLoginPost(HttpServletRequest request, HttpServletResponse response, HttpSession session, // 세션 사용 위해 필요
			@RequestParam(name="mid", defaultValue = "", required = false) String mid, // 널값처리한 걸 mid에 담음
			@RequestParam(name="pwd", defaultValue = "", required = false) String pwd,
			@RequestParam(name="idCheck", defaultValue = "", required = false) String idCheck) {
		
		MemberVO vo = memberService.getMemberIdCheck(mid);
		
		if(vo != null && passwordEncoder.matches(pwd, vo.getPwd()) && vo.getUserDel().equals("NO")) { // 정상적으로 로그인 된 회원
			// 회원 인증처리된 경우 수행할 내용? strLevel처리, session에 필요한 자료를 저장, 쿠키값 처리, 그 날 방문자수 1 증가(방문포인트도 증가), ..
			
			// strLevel처리
			String strLevel = "";
			if(vo.getLevel() == 0) strLevel = "관리자";
			else if(vo.getLevel() == 1) strLevel = "운영자";
			else if(vo.getLevel() == 2) strLevel = "우수회원";
			else if(vo.getLevel() == 3) strLevel = "정회원";
			else if(vo.getLevel() == 4) strLevel = "준회원";
			
			// session에 필요한 자료를 저장
			session.setAttribute("sLevel", vo.getLevel());
			session.setAttribute("sStrLevel", strLevel);
			session.setAttribute("sMid", mid); // 그냥 mid로 적어도 되고 vo에서 가져온거니 밑에처럼 적어도 되고
			session.setAttribute("sNickName", vo.getNickName());
			
			// 쿠키값 처리
			if(idCheck.equals("on")) { // idCheck는 멤버로긴.jsp 체크박스의 아이디, on이면 아이디를 기억하겠다
				Cookie cookie = new Cookie("cMid", mid);
				cookie.setMaxAge(60*60*24*7); // 쿠키의 만료시간을 일주일로
				response.addCookie(cookie); // 이걸 올리기위해서 42번 라인 괄호안에 HttpServletResponse response 올려줬음
			}
			else { // 아이디 저장에 체크 안했을 때
				Cookie[] cookies = request.getCookies(); // 저장된 쿠키 꺼내옴, 저장할 땐 1개지만 꺼내올 땐 여러개일 수 있어서 배열 사용
				for(int i=0; i<cookies.length; i++) { // 쿠키의 길이만큼 돌아라
					if(cookies[i].getName().equals("cMid")) {
						cookies[i].setMaxAge(0); // 쿠키 만료시간 0으로 주고 기존 아이디 지움
						response.addCookie(cookies[i]); 
						break;
					}
				}
			}
			
			// 로그인한 사용자의 방문횟수(&포인트) 누적...컨트롤러 일을 너무 많이 시켜서 이건 서비스로 보냄. 위에 쿠키도 서비스에서 해도됨
			// 누적하기 위해서 날짜를 가져옴
			memberService.setMemberVisitProcess(vo);
			
			return "redirect:/msg/memberLoginOk?mid="+mid;
		}
		else {
			return "redirect:/msg/memberLoginNo";
		}
	}
	
	@RequestMapping(value = "/memberLogout", method = RequestMethod.GET)
	public String memberLogoutGet(HttpSession session) {
		String mid = (String) session.getAttribute("sMid");
		
		session.invalidate();
		
		return "redirect:/msg/memberLogout?mid="+mid;
	}
	
	@RequestMapping(value = "/memberMain", method=RequestMethod.GET) 
	public String memberMainGet(HttpSession session, Model model) {
		String mid = (String) session.getAttribute("sMid");
		
		MemberVO vo = memberService.getMemberIdCheck(mid);
		
		model.addAttribute("vo", vo);
		
		return "member/memberMain";
			
	}
		@RequestMapping(value = "/adminLogout", method=RequestMethod.GET) 
		public String adminLogoutGet(HttpSession session) {
			String mid = (String) session.getAttribute("sAMid");
			
			session.invalidate();
			
			return "redirect:/msg/memberLogout?mid="+mid;
		}
		
		// 회원가입폼
		@RequestMapping(value = "/memberJoin", method = RequestMethod.GET)
		public String memberJoinGet() {
			
			return "member/memberJoin";
		}
		
		// 회원가입처리
		@RequestMapping(value = "/memberJoin", method = RequestMethod.POST)
		public String memberJoinPost(MemberVO vo) {
			//System.out.println("memberVO : " + vo); 잘넘어오나 값 찍어본 것
			// 아이디 중복 체크
			if(memberService.getMemberIdCheck(vo.getMid()) != null) { // 널이 아니면 값이 있으니 아이디 중복이라 메세지로 보냄
			return "redirect:/msg/memberIdCheckNo"; 
		}
			// 닉네임 중복 체크
			if(memberService.getMemberNickNameCheck(vo.getNickName()) != null) {
				return "redirect:/msg/memberNickNameCheckNo"; 
			}
			
			// 비밀번호 암호화(BCryptPasswordEncoder)
			vo.setPwd(passwordEncoder.encode(vo.getPwd()));
			
			// 체크가 완료되면 vo에 담긴 자료를 DB에 저장시켜준다.(회원 가입)
			int res = memberService.setMemberJoinOk(vo);
			
			if(res == 1) return "redirect:/msg/memberJoinOk";
			else return "redirect:/msg/memberJoinNo";
		}
		
		@ResponseBody
		@RequestMapping(value = "/memberIdCheck", method = RequestMethod.POST)
		public String memberIdCheckPost(String mid) {
			String res = "0";
			MemberVO vo = memberService.getMemberIdCheck(mid);
			
			if(vo != null) res = "1"; // vo가 널이 아니면 자료가 있다
			
			return res;
		}
		
		@ResponseBody
		@RequestMapping(value = "/memberNickNameCheck", method=RequestMethod.POST)
		public String memberNickNameCheckGet(String nickName) {
			String res = "0";
			MemberVO vo = memberService.getMemberNickNameCheck(nickName);
			
			if(vo != null) res = "1";
			
			return res;
		}
		/* 이건 전체 검색만, 밑에껀 전체리스트와 검색리스트를 하나의 메소드로 처리
		@RequestMapping(value = "/memberList", method = RequestMethod.GET)
		public String memberListGet(Model model,
				@RequestParam(name="pag", defaultValue = "1", required = false) int pag,
				@RequestParam(name="pageSize", defaultValue = "3", required = false) int pageSize) {
			
			int totRecCnt = memberService.totRecCnt();
			int totPage = (totRecCnt % pageSize)==0 ? totRecCnt / pageSize : (totRecCnt / pageSize) + 1; 
			int startIndexNo = (pag - 1) * pageSize; // 5. 현재 페이지의 시작 인덱스 번호를 구한다.
			int curScrStartNo = totRecCnt - startIndexNo; // 6. 현재 화면에 보여주는 시작 번호를 구한다.
			
			int blockSize = 3; // 1. 블록의 크기를 결정한다. (여기선 3으로 지정)
			int curBlock = (pag - 1) / blockSize; // 2. 현재 페이지가 위치하고 있는 블록 번호를 구한다.(예:1페이지는 0블록, 3페이지는 0블록, 5페이지는 1블록) 123페이지, 0블록 456페이지 1블록, 789페이지 2블록으로 쌤이 설정하심
			int lastBlock = (totPage - 1) / blockSize; // 3. 마지막 블록을 구한다.
			
			ArrayList<MemberVO> vos = memberService.getMemberList(startIndexNo, pageSize); // 시작인덱스번호, 가져올건수
			
			model.addAttribute("vos", vos); // 5건 담김
			model.addAttribute("pag", pag); 
			model.addAttribute("totPage", totPage); 
			model.addAttribute("curScrStartNo", curScrStartNo);
			model.addAttribute("blockSize", blockSize);
			model.addAttribute("curBlock", curBlock);
			model.addAttribute("lastBlock", lastBlock);
			
			return "member/memberList";
		}
		*/
		// 전체리스트와 검색리스트를 하나의 메소드로 처리... --> 동적쿼리
		/*
		@RequestMapping(value = "/memberList", method = RequestMethod.GET)
		public String memberListGet(Model model,
				@RequestParam(name="mid", defaultValue = "", required = false) String mid, // 젤 첨엔 아디 안들어오니 "" 널값처리 해준 것
				@RequestParam(name="pag", defaultValue = "1", required = false) int pag,
				@RequestParam(name="pageSize", defaultValue = "3", required = false) int pageSize) {
			
			//int totRecCnt = memberService.totTermRecCnt(); 전체검색은 이거인데 조건검색을 위해 매개변수 mid 넘김
			int totRecCnt = memberService.totTermRecCnt(mid);
			int totPage = (totRecCnt % pageSize)==0 ? totRecCnt / pageSize : (totRecCnt / pageSize) + 1; 
			int startIndexNo = (pag - 1) * pageSize; // 5. 현재 페이지의 시작 인덱스 번호를 구한다.
			int curScrStartNo = totRecCnt - startIndexNo; // 6. 현재 화면에 보여주는 시작 번호를 구한다.
			
			int blockSize = 3; // 1. 블록의 크기를 결정한다. (여기선 3으로 지정)
			int curBlock = (pag - 1) / blockSize; // 2. 현재 페이지가 위치하고 있는 블록 번호를 구한다.(예:1페이지는 0블록, 3페이지는 0블록, 5페이지는 1블록) 123페이지, 0블록 456페이지 1블록, 789페이지 2블록으로 쌤이 설정하심
			int lastBlock = (totPage - 1) / blockSize; // 3. 마지막 블록을 구한다.
			
			ArrayList<MemberVO> vos = memberService.getTermMemberList(startIndexNo, pageSize, mid); // 시작인덱스번호, 가져올건수, 조건검색 위한 mid
			
			model.addAttribute("vos", vos); // 5건 담김
			model.addAttribute("pag", pag); 
			model.addAttribute("totPage", totPage); 
			model.addAttribute("totRecCnt", totRecCnt); 
			model.addAttribute("curScrStartNo", curScrStartNo);
			model.addAttribute("blockSize", blockSize);
			model.addAttribute("curBlock", curBlock);
			model.addAttribute("lastBlock", lastBlock);
			
			model.addAttribute("mid",mid);
			
			return "member/memberList";
		}
		*/
		// Pagination 이용하기...
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
		}
	}
