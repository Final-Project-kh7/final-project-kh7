package com.kh.finale.restcontroller.plan;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.finale.service.plan.PlanService;
import com.kh.finale.vo.plan.PlanInsertServiceVO;

@RestController
@RequestMapping("/plan/data")
public class PlanRestController {
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private PlanService planService;
	
	@PostMapping("/planInsertService")
	public int planInsertService(@ModelAttribute PlanInsertServiceVO planInsertServiceVO) {
		// 회원번호 세팅
		planInsertServiceVO.setMemberNo((int) session.getAttribute("memberNo"));
		
		return planService.planInsertService(planInsertServiceVO);
	}
	@PostMapping("/planUpdateService")
	public int planUpdateService(@ModelAttribute PlanInsertServiceVO planInsertServiceVO) {
		// 회원번호 세팅
		planInsertServiceVO.setMemberNo((int) session.getAttribute("memberNo"));
		
		return planService.planUpdateService(planInsertServiceVO);
	}
}
