package com.kh.finale.repository.photostory;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.finale.entity.photostory.PhotostoryDto;
import com.kh.finale.vo.photostory.PhotostoryListVO;

@Repository
public class PhotostoryDaoImpl implements PhotostoryDao {

	@Autowired
	private SqlSession sqlSession;
	
	// 포토스토리 페이지 관련 파라미터 계산 기능
	@Override
	public PhotostoryListVO getPageVariable(PhotostoryListVO photostoryListVO) {
		// 페이지 번호
		int pageNo;
		try {
			pageNo = photostoryListVO.getPageNo();
			if (pageNo < 1) {
				throw new Exception();
			}
		}
		catch(Exception e) {
			pageNo = 1;
		}
		
		// 한 페이지에 보일 글의 개수
		int pageSize;
		try {
			pageSize = photostoryListVO.getPageSize();
			if (pageSize < 10) {
				throw new Exception();
			}
		}
		catch (Exception e) {
			pageSize = 8;
		}
		
		// 포토스토리 리스트 범위
		int startRow = pageNo * pageSize - (pageSize - 1);
		int endRow = pageNo * pageSize;
		
		// 페이지네이션 영역 계산
		int photostoryCount = getPhotostoryCount(photostoryListVO);
		int lastBlock = (photostoryCount - 1) / pageSize + 1; 
		
		int blockSize = 10;
		int startBlock = (pageNo - 1) / blockSize * blockSize + 1;
		int endBlock = startBlock + blockSize - 1;
		if (endBlock > lastBlock) endBlock = lastBlock;
		
		photostoryListVO.setPageNo(pageNo);
		photostoryListVO.setPageSize(pageSize);
		photostoryListVO.setStartRow(startRow);
		photostoryListVO.setEndRow(endRow);
		photostoryListVO.setStartBlock(startBlock);
		photostoryListVO.setEndBlock(endBlock);
		photostoryListVO.setLastBlock(lastBlock);
		
		return photostoryListVO;
	}
	
	// 총 포토스토리 개수 획득 기능
	@Override
	public int getPhotostoryCount(PhotostoryListVO photostoryListVO) {
		return sqlSession.selectOne("photostory.getPhotostoryCount");
	}
	
	// 포토스토리 번호 획득 기능
	@Override
	public int getSequence() {
		return sqlSession.selectOne("photostory.getSequence");
	}

	// 포토스토리 작성 기능
	@Override
	public void insertPhotostory(PhotostoryDto photostoryDto) {
		sqlSession.insert("photostory.insert", photostoryDto);
	}
	
	// 포토스토리 수정 기능
	@Override
	public void updatePhotostory(PhotostoryDto photostoryDto) {
		sqlSession.update("photostory.update", photostoryDto);
	}
	
	// 포토스토리 삭제 기능
	@Override
	public void deletePhotostory(int photostoryNo) {
		sqlSession.delete("photostory.delete", photostoryNo);
	}

	// 포토스토리 좋아요 수 갱신 기능
	@Override
	public void refreshPhotostoryLikeCount(int photostoryNo) {
		sqlSession.update("photostory.refreshPhotostoryLikeCount", photostoryNo);
	}

	// 포토스토리 댓글 수 갱신 기능
	@Override
	public void refreshPhotostoryCommentCount(int photostoryNo) {
		sqlSession.update("photostory.refreshPhotostoryCommentCount", photostoryNo);
	}
	
	@Override
	public int getPhotostoryCountWithMemberNo(int memberNo) {
		return sqlSession.selectOne("photostory.getCountWithMemberNo", memberNo);
	}
}
