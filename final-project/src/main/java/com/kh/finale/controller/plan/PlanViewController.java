package com.kh.finale.controller.plan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.finale.entity.member.MemberDto;
import com.kh.finale.entity.plan.PlanListDto;
import com.kh.finale.repository.member.MemberDao;
import com.kh.finale.repository.plan.DailyDao;
import com.kh.finale.repository.plan.DailyplanDao;
import com.kh.finale.repository.plan.PlanListDao;
import com.kh.finale.repository.plan.PlannerDao;
import com.kh.finale.service.plan.PlanService;
import com.kh.finale.vo.plan.FindPhotoVO;
import com.kh.finale.vo.plan.PlanInsertServiceVO;
import com.kh.finale.vo.plan.ResultPlanVO;

@Controller
@RequestMapping("/plan")
public class PlanViewController {
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private PlanService planService;
	
	@Autowired
	private PlanListDao planListDao;
	
	@Autowired
	private DailyDao dailyDao;

	@Autowired
	private DailyplanDao dailyplanDao;
	
	@Autowired
	private PlannerDao plannerDao;
	
	@Autowired
	MemberDao memberDao;
	
	// 계획표 작성 페이지
	@GetMapping("/writeplan")
	public String writePlan(Model model, HttpSession session) {
		if (session.getAttribute("memberNo") != null) {
			MemberDto memberDto = memberDao.findInfo((int) session.getAttribute("memberNo"));
			model.addAttribute("memberDto", memberDto);
		}
		return "plan/writeplan";
	}
	
	// 계획표 수정 페이지
	@GetMapping("/editplan")
	public String editPlan(@RequestParam int plannerNo, Model model) {
		List<PlanListDto> planList = planListDao.getPlanList(plannerNo);
		model.addAttribute("planList", planList); 
		
		List<PlanInsertServiceVO> dailyList = dailyDao.getDailyList(plannerNo); 
		List<Integer> dailyplanCountList = new ArrayList<>();
		for (PlanInsertServiceVO vo : dailyList) {
			int dailyplanCount = dailyplanDao.getDailyplanCount(vo.getDailyNo());
			dailyplanCountList.add(dailyplanCount);
		}
		System.out.println("???="+dailyplanCountList);
		
		model.addAttribute("dailyplanCountList", dailyplanCountList);
		
		return "plan/editplan";
	}
	
	@GetMapping("/resultPlan")
	public String resultPlan(@ModelAttribute ResultPlanVO resultPlanVO, Model model, FindPhotoVO findPhotoVO) throws JsonProcessingException {
		resultPlanVO.setMemberNo((int) session.getAttribute("memberNo"));
		
		// 포토스토리 이미지 테스트
		findPhotoVO.setMemberNo((int) session.getAttribute("memberNo"));
		FindPhotoVO sendPhoto = planService.selectPhoto(findPhotoVO);
		System.out.println("이미지 DB값 : " + sendPhoto);
		
		List<ResultPlanVO> sendData = planService.selectPlan(resultPlanVO);
		System.out.println(sendData);
		// 자바 객체 JSON 변환 (작성자 : 정 계진)
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = mapper.writeValueAsString(sendData);
		
		model.addAttribute("list", jsonStr);
		model.addAttribute("plannerNo", resultPlanVO.getPlannerNo());
		
		return "plan/resultPlan";
	}
	
	// 이미지 다운로드 처리
	@GetMapping("/resultPlan/image")
	public ResponseEntity<ByteArrayResource> download(@PathVariable int plannerNo, @ModelAttribute FindPhotoVO findPhotoVO) throws IOException {
		FindPhotoVO sendPhoto = planService.selectPhoto(findPhotoVO);
		System.out.println("이미지 반환값 : " + sendPhoto);
		if (findPhotoVO == null) {
			System.out.println("NOT FOUND");
			return ResponseEntity.notFound().build();
		}
		System.out.println("FOUND");
		
		File target = new File("D:/upload/kh5/photostory/", sendPhoto.getPhotostoryPhotoFilePath());
		System.out.println("타겟 : " + target);
		byte[] data = FileUtils.readFileToByteArray(target);
		System.out.println("데이터 : " + data);
		ByteArrayResource resource = new ByteArrayResource(data);
		System.out.println("리소스 : " + resource);
		
		return ResponseEntity.ok()
							 .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
							.body(resource);
	}
	
	//계획표 삭제 처리
	@GetMapping("/deleteplan")
	public String deletePlan(@RequestParam int plannerNo) {
		plannerDao.plannerDelete(plannerNo);
		System.out.println("삭제 성공");
		return "redirect:/";
	}
}