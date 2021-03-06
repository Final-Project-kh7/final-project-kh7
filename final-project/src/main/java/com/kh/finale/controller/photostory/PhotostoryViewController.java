package com.kh.finale.controller.photostory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.finale.entity.block.MemberBlockDto;
import com.kh.finale.entity.hashtag.HashtagDto;
import com.kh.finale.entity.member.FollowDto;
import com.kh.finale.entity.member.MemberDto;
import com.kh.finale.entity.photostory.PhotostoryCommentListDto;
import com.kh.finale.entity.photostory.PhotostoryLikeDto;
import com.kh.finale.entity.photostory.PhotostoryListDto;
import com.kh.finale.entity.photostory.PhotostoryPhotoDto;
import com.kh.finale.repository.block.MemberBlockDao;
import com.kh.finale.repository.hashtag.HashtagDao;
import com.kh.finale.repository.member.FollowDao;
import com.kh.finale.repository.member.MemberDao;
import com.kh.finale.repository.photostory.PhotostoryCommentListDao;
import com.kh.finale.repository.photostory.PhotostoryDao;
import com.kh.finale.repository.photostory.PhotostoryLikeDao;
import com.kh.finale.repository.photostory.PhotostoryListDao;
import com.kh.finale.repository.photostory.PhotostoryPhotoDao;
import com.kh.finale.service.block.MemberBlockService;
import com.kh.finale.service.photostory.PhotostoryService;
import com.kh.finale.vo.member.LikeFollowVo;
import com.kh.finale.vo.photostory.PhotostoryListVO;
import com.kh.finale.vo.photostory.PhotostoryVO;

@Controller
@RequestMapping("/photostory")
public class PhotostoryViewController {

	@Autowired
	private PhotostoryDao photostoryDao;
	
	@Autowired
	private PhotostoryListDao photostoryListDao;

	@Autowired
	private PhotostoryCommentListDao photostoryCommentListDao;
	
	@Autowired
	private PhotostoryLikeDao photostoryLikeDao;
	
	@Autowired
	private PhotostoryService photostoryService;

	@Autowired
	private PhotostoryPhotoDao photostoryPhotoDao;
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private MemberBlockDao memberBlockDao;

	@Autowired
	private FollowDao followDao;
	
	@Autowired
	private HashtagDao hashtagDao;

	@Autowired
	private MemberBlockService memberBlockService;
	
	// ??????????????? ????????? ?????????
	@GetMapping("")
	public String home(@ModelAttribute PhotostoryListVO photostoryListVO, Model model, HttpSession session, HttpServletRequest request) throws Exception {
		photostoryListVO = photostoryDao.getPageVariable(photostoryListVO);
		List<PhotostoryListDto> photostoryList = photostoryListDao.list(photostoryListVO);
		model.addAttribute("searchKeyword",photostoryListVO.getSearchKeyword());
		// ?????? ?????? ??? ?????? ?????? ?????? ??????
		int memberNo = 0;
		if (session.getAttribute("memberNo") != null) {
			memberNo = (int) session.getAttribute("memberNo");
			MemberDto memberDto = memberDao.findInfo(memberNo);
			MemberBlockDto memberBlockDto = memberBlockDao.getBlockInfo(memberNo);
			
			model.addAttribute("memberDto", memberDto);
			model.addAttribute("memberBlockDto", memberBlockDto);
			
			// ?????? ????????? ?????? ??????
			if (memberDto.getMemberState().equals("??????")) {
				// ?????? ?????? ??????
				boolean blockCheck = memberBlockDao.checkBlock(memberNo);
				// ?????? ????????? ????????? ??????
				if (blockCheck) {
					memberBlockService.unblock(memberDto.getMemberNo());
				}
				// ?????? ????????? ????????? ????????? ??????
				else {
					// ?????? ???????????? ?????????, ?????? ??? ?????? ???????????? ?????? ????????? ?????? 
					// ???????????? ?????????????????? ??????
					model.addAttribute("block", memberBlockDto);
					model.addAttribute("msg", "???????????? ?????? ????????? ?????? ???????????????.");
					model.addAttribute("reason", memberBlockDto.getBlockReason());
					model.addAttribute("blockEndDate", memberBlockDto.getBlockEndDate());
					model.addAttribute("url", request.getContextPath());
					session.removeAttribute("memberNo");
					return "member/block";
				}
				return "redirect:/";
			}
		}
		
		for (int i = 0; i < photostoryList.size(); i++) {
			PhotostoryListDto photostoryListDto = photostoryList.get(i);
			
			// ????????? ??????
			PhotostoryLikeDto photostoryLikeDto = PhotostoryLikeDto.builder()
					.photostoryNo(photostoryListDto.getPhotostoryNo())
					.memberNo(memberNo)
					.build();
			Boolean isLike = photostoryLikeDao.checkPhotostoryLike(photostoryLikeDto);
			if (isLike != null) {
				photostoryListDto.setIsLike(isLike);
			}
			
			// ?????? ??????
			List<PhotostoryCommentListDto> recentCommentList = 
					photostoryCommentListDao.recentList(photostoryListDto.getPhotostoryNo());
			for (int j = 0; j < recentCommentList.size(); j++) {
				photostoryListDto.setPhotostoryCommentList(recentCommentList);
			}
			
			// ????????? ??????
			List<PhotostoryPhotoDto> photostoryPhotoList = photostoryPhotoDao.get(photostoryListDto.getPhotostoryNo());
			if (!photostoryPhotoList.isEmpty()) {
				photostoryListDto.setPhotostoryPhotoNo(photostoryPhotoList.get(0).getPhotostoryPhotoNo());
			}
			
			//????????? ????????? ??????
			List<MemberDto> tlikeList = 
					photostoryLikeDao.getLikeList(photostoryListDto.getPhotostoryNo());
			
			List<LikeFollowVo> likeList = new ArrayList<>();
			for(MemberDto m : tlikeList) {
				LikeFollowVo lfv = LikeFollowVo.builder()
						.member(m)
						.isFollow(false)
						.build();
				likeList.add(lfv);
			}
			if((Integer)session.getAttribute("memberNo")!=null) {
				for(LikeFollowVo f : likeList) {
					FollowDto followDto = FollowDto.builder()
							.followFrom((Integer)session.getAttribute("memberNo"))
							.followTo(f.getMember().getMemberNo())
							.build();
					if(followDao.isFollow(followDto)!=null) {
						f.setFollow(true);
					}
				}
			}
			photostoryList.get(i).setPhotostoryLikeMemberList(likeList);
		}

		model.addAttribute("photostoryList", photostoryList);

		return "photostory/photostory";
	}
	
	// ??????????????? ?????? ?????????
	@GetMapping("/detail")
	public String detail(@RequestParam int photostoryNo, Model model, HttpSession session) {
		PhotostoryListDto photostoryListDto = photostoryListDao.get(photostoryNo);
		List<PhotostoryCommentListDto> photostoryCommentList = photostoryCommentListDao.list(photostoryNo);
		
		// ?????? ?????? ??? ?????? ?????? ?????? ??????
		int memberNo = 0;
		if (session.getAttribute("memberNo") != null) {
			memberNo = (int) session.getAttribute("memberNo");
			MemberDto memberDto = memberDao.findInfo(memberNo);
			MemberBlockDto memberBlockDto = memberBlockDao.getBlockInfo(memberNo);
			
			model.addAttribute("memberDto", memberDto);
			model.addAttribute("memberBlockDto", memberBlockDto);
		}
		
		// ????????? ?????????
		List<PhotostoryPhotoDto> photostoryPhotoList = photostoryPhotoDao.get(photostoryNo);
		
		// ????????? ??????
		PhotostoryLikeDto photostoryLikeDto = PhotostoryLikeDto.builder()
				.photostoryNo(photostoryListDto.getPhotostoryNo())
				.memberNo(memberNo)
				.build();
		Boolean isLike = photostoryLikeDao.checkPhotostoryLike(photostoryLikeDto);
		if (isLike != null) {
			photostoryListDto.setIsLike(isLike);
		}
		
		
		//????????? ????????? ??????
		List<MemberDto> tlikeList = 
				photostoryLikeDao.getLikeList(photostoryListDto.getPhotostoryNo());
		
		List<LikeFollowVo> likeList = new ArrayList<>();
		for(MemberDto m : tlikeList) {
			LikeFollowVo lfv = LikeFollowVo.builder()
					.member(m)
					.isFollow(false)
					.build();
			likeList.add(lfv);
		}
		if((Integer)session.getAttribute("memberNo")!=null) {
			for(LikeFollowVo f : likeList) {
				FollowDto followDto = FollowDto.builder()
						.followFrom((Integer)session.getAttribute("memberNo"))
						.followTo(f.getMember().getMemberNo())
						.build();
				if(followDao.isFollow(followDto)!=null) {
					f.setFollow(true);
				}
			}
		}
		photostoryListDto.setPhotostoryLikeMemberList(likeList);
		model.addAttribute("photostoryListDto", photostoryListDto);
		model.addAttribute("photostoryCommentList", photostoryCommentList);
		model.addAttribute("photostoryPhotoList", photostoryPhotoList);
		
		return "photostory/detail";
	}
	
	// ??????????????? ?????? ?????????
	@GetMapping("/write")
	public String write(HttpSession session, Model model,Integer plannerNo) {
		// ?????? ?????? ??????
		if (session.getAttribute("memberNo") != null) {
			MemberDto memberDto = memberDao.findInfo((int) session.getAttribute("memberNo"));
			model.addAttribute("memberDto", memberDto);
		}
		model.addAttribute("plannerNo",plannerNo);
		return "photostory/write";
	}
	
	// ??????????????? ?????? ??????
	@PostMapping("/write")
	public String write(@ModelAttribute PhotostoryVO photostoryVO,
			HttpSession session,
			String[] hashtag) throws IllegalStateException, IOException {
		int memberNo = (int) session.getAttribute("memberNo");
		photostoryVO.setMemberNo(memberNo);
		int storyNo = photostoryService.insertPhotostory(photostoryVO);
		Set<String> set = new HashSet<>();
		if (hashtag != null) {
			for(String h : hashtag) {
				set.add(h);
			}
			for(String s : set) {
				HashtagDto hash = HashtagDto.builder()
						.hashtagTag(s)
						.photostoryNo(storyNo)
						.build();
				hashtagDao.insert(hash);
			}
		}
		
		
		return "redirect:/photostory";
	}
	
	// ??????????????? ?????? ?????????
	@GetMapping("/edit")
	public String edit(@RequestParam int photostoryNo, Model model) {
		// ??????????????? ??????
		PhotostoryListDto photostoryListDto = photostoryListDao.get(photostoryNo);
		
		// ????????? ?????????
		List<PhotostoryPhotoDto> photostoryPhotoList = photostoryPhotoDao.get(photostoryNo);
		
		model.addAttribute("photostoryListDto", photostoryListDto);
		String val = photostoryListDto.getPhotostoryContent().replaceAll("&nbsp;", "");
		photostoryListDto.setPhotostoryContent(val);
		model.addAttribute("photostoryPhotoList", photostoryPhotoList);
		
		// ?????? ?????? ??????
		MemberDto memberDto = memberDao.findInfo(photostoryListDto.getMemberNo());
		model.addAttribute("memberDto", memberDto);
		
    	return "photostory/edit";
	}
	
	// ??????????????? ?????? ??????
	@PostMapping("/edit")
	public String edit(@ModelAttribute PhotostoryVO photostoryVO,
			HttpSession session,
			String[] hashtag) throws IllegalStateException, IOException {
		int memberNo = (int) session.getAttribute("memberNo");
		photostoryVO.setMemberNo(memberNo);
		photostoryService.updatePhotostory(photostoryVO);
		
		hashtagDao.delete(photostoryVO.getPhotostoryNo());
		
		if (hashtag != null) {
			Set<String> set = new HashSet<>();
			for(String h : hashtag) {
				set.add(h);
			}
			for(String s : set) {
				HashtagDto hash = HashtagDto.builder()
						.hashtagTag(s)
						.photostoryNo(photostoryVO.getPhotostoryNo())
						.build();
				hashtagDao.insert(hash);
			}
		}
		
		return "redirect:/photostory/detail?photostoryNo=" + photostoryVO.getPhotostoryNo();
	}
	
	// ??????????????? ?????? ??????
	@GetMapping("/delete")
	public String delete(@RequestParam int photostoryNo, @RequestParam(required = false) String home) {
		photostoryService.deletePhotostory(photostoryNo);
		
		if (home == null) {
			return "redirect:/photostory";
		}
		return "redirect:/";
	}
	
	// ????????? ???????????? ??????
	@GetMapping("/photo/{photostoryPhotoNo}")
	public ResponseEntity<ByteArrayResource> download(@PathVariable int photostoryPhotoNo,HttpServletRequest req) throws IOException {
		PhotostoryPhotoDto photostoryPhotoDto = photostoryPhotoDao.getSingle(photostoryPhotoNo);
		if (photostoryPhotoDto == null) {
			return ResponseEntity.notFound().build();
		}
		if(photostoryPhotoDto.getPhotostoryPhotoFilePath().equals("delete")) {
			URL url = new URL("http://"+req.getServerName()+":"+req.getServerPort()+req.getContextPath()+"/image/delete_img.jpg");
            BufferedImage img = ImageIO.read(url);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img,"jpg",baos);
            baos.flush();
            byte[] data1 = baos.toByteArray();
            baos.close();
			
			ByteArrayResource resource = new ByteArrayResource(data1);
			return ResponseEntity.ok()
									.header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
									.body(resource);
		}
		
		File target = new File("D:/upload/kh7e/photostory/", photostoryPhotoDto.getPhotostoryPhotoFilePath());
		
		byte[] data = FileUtils.readFileToByteArray(target);
		
		ByteArrayResource resource = new ByteArrayResource(data);
		
		
		
		return ResponseEntity.ok()
						 	 .contentLength(photostoryPhotoDto.getPhotostoryPhotoFileSize())
							 .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
							.body(resource);
	}
}
