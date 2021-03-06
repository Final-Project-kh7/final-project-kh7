<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<script>
	/* 아이디(영문/숫자 4~12자), 비밀번호 (영문/숫자/한글 4~12자), 이름 (한글 2~7자), 닉네임 (영문/숫자/한글 4~12자) 검사 */
	let regex = /^[0-9a-zA-Z]{4,12}$/;
	let name_regex = /^[가-힣]{2,7}$/;
	let nick_name_regex = /^[0-9a-zA-Z가-힣]{4,12}$/;
	let id = false;
	let nick = false;
	let pw = false;
	let name = false;
	$(function() {
		
		$(".idck").hide()
		$(".nickck").hide()
		
		$('#memberId').blur(function() {
			$(".idck").hide()
			if (regex.test($(this).val())) {
				$("#idck").removeClass("text-muted")
				$("#idck").removeClass("text-danger")
				$("#idck").addClass("text-success")
				let memberId = $(this).val();
				$.ajax({
					url:"idCheck",
					data : {
						memberId : memberId,
					},
					method:"POST",
					})
					.done(function(json){
						if(json) {
							id=false;
							$('#memberId').removeClass("is-valid");
							$('#memberId').addClass("is-invalid");
							$(".idck").show()
						}
						else {
							id = true;
							$('#memberId').addClass("is-valid");
							$('#memberId').removeClass("is-invalid");
							$(".idck").hide()
						}
					})
					.fail(function(){
					})
			} else {
				id = false;
				$(this).removeClass("is-valid");
				$(this).addClass("is-invalid");
				$("#idck").removeClass("text-muted")
				$("#idck").removeClass("text-success")
				$("#idck").addClass("text-danger")
			}
		})
		$('#memberPw').blur(function() {
			if (regex.test($(this).val())) {
				pw = true;
				$(this).addClass("is-valid");
				$(this).removeClass("is-invalid");
				$("#pwck").removeClass("text-muted")
				$("#pwck").removeClass("text-danger")
				$("#pwck").addClass("text-success")
			} else {
				pw = false;
				$(this).removeClass("is-valid");
				$(this).addClass("is-invalid");
				$("#pwck").removeClass("text-muted")
				$("#pwck").removeClass("text-success")
				$("#pwck").addClass("text-danger")
			}
		})
		$('#memberPw2').blur(function() {
			if ($(this).val() == $("#memberPw").val()) {
				$(this).addClass("is-valid");
				$(this).removeClass("is-invalid");
			} else {
				$(this).removeClass("is-valid");
				$(this).addClass("is-invalid");
			}
		})
		$('#memberNick').blur(function() {
			$(".nickck").hide()
			if (nick_name_regex.test($(this).val())) {
				$("#nickck").removeClass("text-muted")
				$("#nickck").removeClass("text-danger")
				$("#nickck").addClass("text-success")
				let memberNick = $(this).val();
				$.ajax({
					url:"jNickCheck",
					data : {
						memberNick : memberNick,
					},
					method:"POST",
					})
					.done(function(json){
						if(json) {
							nick=false;
							$('#memberNick').removeClass("is-valid");
							$('#memberNick').addClass("is-invalid");
							$(".nickck").show()
						}
						else {
							nick = true;
							$('#memberNick').addClass("is-valid");
							$('#memberNick').removeClass("is-invalid");
							$(".nickck").hide()
						}
					})
					.fail(function(){
					})
			} else {
				nick = false;
				$(this).removeClass("is-valid");
				$(this).addClass("is-invalid");
				$("#nickck").removeClass("text-muted")
				$("#nickck").removeClass("text-success")
				$("#nickck").addClass("text-danger")
			}
		})
		$('#memberName').blur(function() {
			if (name_regex.test($(this).val())) {
				name = true;
				$(this).addClass("is-valid");
				$(this).removeClass("is-invalid");
				$("#nameck").removeClass("text-muted")
				$("#nameck").removeClass("text-danger")
				$("#nameck").addClass("text-success")
			} else {
				name = false;
				$(this).removeClass("is-valid");
				$(this).addClass("is-invalid");
				$("#nameck").removeClass("text-muted")
				$("#nameck").removeClass("text-success")
				$("#nameck").addClass("text-danger")
			}
		})
		/* form submit 전송 검사 */
		$('.submit_btn').click(function(e) {
			if (!id) {
				e.preventDefault();
				$('#memberId').focus();
				return;
			}
			if (!pw) {
				e.preventDefault();
				$('#memberPw').focus();
				return;
			}
			if ($('#memberPw').val() != $('#memberPw2').val()) {
				e.preventDefault();
				$('#memberPw2').focus();
				return;
			}
			if (!name) {
				e.preventDefault();
				$('#memberName').focus();
				return;
			}
			if (!nick) {
				e.preventDefault();
				$('#memberNick').focus();
				return;
			}
		})
		
		function readImage(input) {
		    if(input.files && input.files[0]) {
		        const reader = new FileReader()
		        reader.onload = e => {
		            const previewImage = document.querySelector(".upload_img")
		            previewImage.src = e.target.result
		        }
		        reader.readAsDataURL(input.files[0])
		    }
		}
		$(".input_img").change(function(e){
			readImage(e.target)
		})
	})
</script>

<main>


	<div class="container-lg">
		<div class="row">
			<div class="jumbotron col-lg-6 offset-lg-3">
				<h3 class="display-5">회원가입</h3>
				<span>NAEILRO에 오신 것을 환영합니다.</span>
			</div>
			<div class="col-lg-6 offset-lg-3 text-center">
				<form action="join" method="post" class="sign_up_form encrypt-form"
					enctype="multipart/form-data">
					<!-- 	프로필 사진 업로드 -->
					<div class="form-row mb-3">
						<label for='memberProfile'>프로필 사진</label>
					</div>
					<div class="form-row mb-3 justify-content-center">
						<label for="memberProfile"> <img
							class='upload_img user_profile'
							src="${pageContext.request.contextPath}/image/default_user_profile.jpg"
							style="width: 100px; height: 100px;"> <input
							class="input_img" type="file" accept=".png, .jpg, .gif"
							id="memberProfile" name="memberProfile" style="display: none" />
						</label>
					</div>
					<div class="form-row mb-3">
						<label for="memberId">아이디 </label>
						<small class="idck form-text text-danger text-left ">
						&nbsp;&nbsp; 중복된 아이디입니다.
						</small> 
						<input type="text" class="form-control " id="memberId" name="memberId" required>
						<small id="idck" class="form-text text-muted">
						4~12자의 영문 소문자, 대문자, 숫자만 사용 가능합니다.
						</small>
					</div>
					
					<div class="form-row mb-3">
						<label for="memberPw">비밀번호</label> <input type="password"
							class="form-control " id="memberPw" name="memberPw" required>
						<small id="pwck" class="form-text text-muted">4~12자의
							영문 소문자, 대문자, 숫자만 사용 가능합니다.</small>
					</div>
					<div class="form-row mb-3">
						<label for="memberPw2">비밀번호 확인</label> <input type="password"
							class="form-control " id="memberPw2" name="memberPw2" required>
					</div>
					<div class="form-row mb-3">
						<label for="memberName">이름</label> <input type="text"
							class="form-control" id="memberName" name="memberName" required>
						<small id="nameck" class="form-text text-muted">2~7자의
							한글만 사용 가능합니다.</small>
					</div>
					<div class="form-row mb-3">
						<label for="memberNick">닉네임</label>
						<small class="nickck form-text text-danger text-left ">
						&nbsp;&nbsp; 중복된 닉네임입니다.
						</small> 
						 <input type="text"
							class="form-control" id="memberNick" name="memberNick" required>
						<small id="nickck" class="form-text text-muted">4~12자의
							영문 소문자, 대문자, 한글, 숫자만 사용 가능합니다.</small>
					</div>
					<div class="form-row mb-3">
						<label for="memberEmail">이메일</label> <input type="email"
							class="form-control" id="memberEmail" name="memberEmail" required>
					</div>
					<div class="form-row mb-3">
						<label for="memberBirth">생년월일</label> <input type="date"
							class="form-control" id="memberBirth" name="memberBirth" required>
					</div>
					<div class="form-row mb-5 justify-content-around">
						<div class="form-check">
							<input class="form-check-input" type="radio" name="memberGender"
								id="memberGender1" value="남" checked> <label
								class="form-check-label" for="memberGender1"> 남자 </label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="memberGender"
								id="memberGender2" value="여"> <label
								class="form-check-label" for="memberGender2"> 여자 </label>
						</div>

						<!-- 맴버 등급 데이터 전송창 -->
					</div>
					<div class="form-row mb-3">
						<input type="hidden" class="form-control" id="memberGrade"
							name="memberGrade" value="2">
					</div>
					<div class="form-row mb-3">
						<input type="hidden" class="form-control" id="memberIntro"
							name="memberIntro" value="자기소개를 입력해 주세요">
					</div>
					<div class="form-row mb-3">
						<input type="hidden" class="form-control" id="memberState"
							name="memberState" value="정상">
					</div>
					<div class="form-row mb-5 justify-content-around">
						<button class="btn btn-primary submit_btn btn-block" type="submit">회원가입</button>
						<button class="btn btn-secondary cancel-btn btn-block"
							type="button">취소</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</main>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>