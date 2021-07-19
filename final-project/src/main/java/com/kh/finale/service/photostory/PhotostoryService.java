package com.kh.finale.service.photostory;

import java.io.IOException;

import com.kh.finale.vo.photostory.PhotostoryVO;

public interface PhotostoryService {
	// 포토스토리 등록
	void insertPhotostory(PhotostoryVO photostoryVO) throws IllegalStateException, IOException;
}