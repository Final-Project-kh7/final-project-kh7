<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 포토스토리 리스트 페이지 -->
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>Insert title here</title>
		<!-- 영역 구분용 임시 테두리 -->
		<style>
			.photostory {
				border: 1px dotted red;
			}
			.comment {
				border: 1px dotted purple;
			}
			.pagination {
				border: 1px dotted blue;
			}
		</style>

		<script>
			addEventListener('load', function () {
				// 글쓰기 버튼 클릭 시 페이지 이동
				document.querySelector('.btn-write').addEventListener('click', function () {
					location.href = '${pageContext.request.contextPath}/photostory/write';
				});
			});
		</script>
	</head>
	<body>
		<!-- 검색창 영역 시작 -->
		<form action="photostory" method="GET">
			<select name="searchType">
				<option value="">제목</option>
				<option value="">내용</option>
				<option value="">닉네임</option>
			</select>
			<input type="text" name="searchKeyword" />
			<input type="button" value="검색" />
		</form>
		<!-- 검색창 영역 종료 -->

		<!-- 글쓰기 버튼 -->
		<button class="btn-write">글쓰기</button>

		<!-- 포토스토리 리스트 영역 시작 -->
		<div class="photostory">
			<c:forEach var="photostoryDto" items="${list}">
				<!-- 포토스토리 게시물 영역 시작 -->
				<div class="post">
					<div class="row">${photostoryDto.photostoryTitle}</div>
					<div class="row">${photostoryDto.photostoryContent}</div>
				</div>
				<!-- 포토스토리 게시물 영역 종료 -->

				<!-- 좋아요 버튼 -->
				<span class="btn-like">♥</span>
				<span class="btn-unlike">♡</span>

				<!-- 댓글 영역 시작 -->
				<div class="comment">댓글 영역</div>
				<!-- 댓글 영역 종료 -->
			</c:forEach>
		</div>
		<!-- 포토스토리 리스트 영역 종료 -->

		<!-- 페이지네이션 영역 시작 -->
		<div class="pagination">
			<c:if test="${photostoryVO.startBlock > 1}">
				<a class="move-link">&lt;&lt;</a>
				<a class="move-link">&lt;</a>
			</c:if>
			<c:forEach
				var="i"
				begin="${photostoryVO.startBlock}"
				end="${photostoryVO.endBlock}"
				step="1"
			>
				<c:choose>
					<c:when test="${i} == ${photostoryVO.pageNo}">
						<a class="on">${i}</a>
					</c:when>
					<c:otherwise>
						<a>${i}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			<c:if test="${photostoryVO.endBlock < photostoryVO.lastBlock}">
				<a class="move-link">&gt;</a>
				<a class="move-link">&gt;&gt;</a>
			</c:if>
		</div>
		<!-- 페이지네이션 영역 종료 -->
	</body>
</html>