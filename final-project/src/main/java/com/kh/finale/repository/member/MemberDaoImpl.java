package com.kh.finale.repository.member;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.finale.entity.member.MemberAuthDto;
import com.kh.finale.entity.member.MemberDto;
import com.kh.finale.vo.member.MemberVo;

@Repository
public class MemberDaoImpl implements MemberDao{
	
	@Autowired
	SqlSession sqlSession;
	
	// 회원가입 기능
	@Override
	public void join(MemberDto memberDto) {
		sqlSession.insert("member.join", memberDto);
	}

	@Override
	public MemberDto login(MemberDto memberDto) {
		return sqlSession.selectOne("member.login", memberDto);
	}

	@Override
	public MemberDto findId(MemberDto memberDto) {
		return sqlSession.selectOne("member.findId", memberDto);
	}

	@Override
	public MemberVo findPw(MemberVo memberVo) {
		return sqlSession.selectOne("member.findPw", memberVo);
	}

	@Override
	public void authInsert(MemberAuthDto memberAuthDto) {
		sqlSession.insert("member.authInsert", memberAuthDto);
		
	}

}
