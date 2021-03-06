package com.kh.finale.repository.member;

import java.util.List;
import java.util.Map;

import com.kh.finale.entity.member.MemberAuthDto;
import com.kh.finale.entity.member.MemberDto;
import com.kh.finale.vo.member.MemberVo;
import com.kh.finale.vo.report.PageVo;

public interface MemberDao {
	void join(MemberDto memberDto);
	MemberDto login(MemberDto memberDto);
	// 회원정보 조회(회원번호로)
	MemberDto findInfo(int memberNo);
	MemberDto findId(MemberDto memberDto);
	MemberVo findPw(MemberVo memberVo);
	// 회원정보 조회(닉네임으로)
	MemberDto findWithNick(String memberNick);
	void authInsert(MemberAuthDto memberAuthDto);
	Map<String,Object> resultAuth(MemberAuthDto memberAuthDto);
	MemberAuthDto checkAuthEmail(MemberAuthDto memberAuthDto);
	MemberAuthDto selectId (MemberAuthDto memberAuthDto);
	void updatePw(MemberDto memberDto);
	void editProfile(MemberVo memberVo);
	int idCheck(MemberVo memberVo);
	MemberVo pNickCheck(MemberVo memberVo);
	int jNickCheck(MemberVo memberVo);
	void exit(MemberVo memberVo);
	// 회원 정지
	void block(int memberNo);
	// 회원 정지 해제
	void unblock(int memberNo);
	int getCount();
	int getBlockCount();
	int getUnBlockCount();
	List<MemberVo> list(PageVo pageVo);
	List<MemberVo> blockList(PageVo pageVo);
	List<MemberVo> unblockList(PageVo pageVo);
	List<MemberDto> searchPreview(String keyword);
}
