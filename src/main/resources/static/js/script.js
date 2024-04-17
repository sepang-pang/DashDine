// 카카오 지오코더 객체 생성
let latitude, longitude;

document.addEventListener("DOMContentLoaded", function () {
    const homeBtn = document.getElementById("home_btn");
    const signupBtn = document.getElementById("signup_btn");
    const registrationBtn = document.getElementById('registration_btn');
    const cancelBtn = document.getElementById("cancel_btn");

    // 홈 버튼 이벤트 리스너
    homeBtn.addEventListener("click", function () {
        window.location.href = "/";
    })

    // 회원가입 버튼 이벤트 리스너
    signupBtn.addEventListener("click", function () {
        window.location.href = '/user/signup'; // 회원가입 페이지로 리디렉트
    });

    // 회원가입 처리 버튼 이벤트 리스너
    registrationBtn.addEventListener('click', function () {
        submitSignupForm(); // 회원가입 처리 함수 호출
    });

    // 취소 버튼 이벤트 리스너
    cancelBtn.addEventListener("click", function () {
        window.history.back(); // 이전 페이지로 돌아가기
    });
});

// 회원가입 폼 제출 처리 함수
function submitSignupForm() {
    const loginId = document.getElementById("login_id");
    const username = document.getElementById("username");
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirm_password');
    const email = document.getElementById("email");
    const city = document.getElementById("street");
    const street = document.getElementById("street_detail");
    const zipcode = document.getElementById("zipcode");

    // 폼 검증
    if (!validateForm()) {
        return;
    }

    // 서버로 전송할 회원가입 데이터
    const signupData = {
        loginId: loginId.value,
        username: username.value,
        password: password.value,
        email: email.value,
        city: city.value,
        street: street.value,
        zipcode: zipcode.value,
        longitude,
        latitude
    };


    // 회원가입 요청을 서버로 보내기
    fetch('/user/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(signupData)
    })
        .then(response => {
            if (!response.ok) {
                throw response;  // 에러 응답을 catch 블록으로 넘깁니다.
            }
            return response.json();  // 정상 응답 처리
        })
        .then(data => {
            // 회원가입 성공 시
            alert("회원가입이 완료되었습니다! 환영합니다.")
            console.log(data)
            window.location.href = "/"
        })
        .catch(error => {
            error.json().then(errData => {
                console.error('Error:', errData);
                alert(errData.errorMessage);  // 수정된 부분: errorMessage로 접근

                if (errData.errorMessage.includes("중복된 사용자")) {
                    loginId.focus();
                } else if (errData.errorMessage.includes("중복된 Email")) {
                    email.focus();
                }

            });
        });
}

// 회원가입 폼 검증 함수
function validateForm() {
    const termsAgree = document.getElementById("terms_agree");
    const privacyAgree = document.getElementById("privacy_agree");
    const ageConfirm = document.getElementById("age_confirm");
    const loginId = document.getElementById("login_id");
    const username = document.getElementById("username");
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirm_password');
    const email = document.getElementById("email");
    const city = document.getElementById("street");
    const zipcode = document.getElementById("zipcode");


    if (!termsAgree.checked) {
        alert("이용약관에 동의해주세요.");
        termsAgree.focus();
        return false;
    }

    if (!privacyAgree.checked) {
        alert("개인정보 수집 및 이용에 동의해주세요.");
        privacyAgree.focus();
        return false;
    }

    if (!ageConfirm.checked) {
        alert("만 14세 이상 이용자임을 확인해주세요.");
        ageConfirm.focus();
        return false;
    }

    if (!loginId.value) {
        alert("ID를 입력해주세요.");
        loginId.focus();
        return;
    }

    if (!loginId.value.match(/^[a-z0-9]{4,10}$/)) {
        alert("ID는 4~10자리의 알파벳 소문자와 숫자만 포함해야 합니다.");
        loginId.focus();
        return;
    }

    if (!email.value) {
        alert("이메일을 입력해주세요");
        email.focus();
        return;
    }

    if (!email.value.match(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)) {
        alert("올바른 이메일 주소를 입력해주세요.");
        email.focus();
        return;
    }

    if (!password.value) {
        alert("비밀번호를 입력해주세요");
        password.focus();
        return;
    }

    if (!password.value.match(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,16}$/)) {
        alert("비밀번호는 8~16자리이며, 영문 대소문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다.");
        password.focus();
        return;
    }

    if (!confirmPassword.value) {
        alert("비밀번호 확인을 입력해주세요.");
        confirmPassword.focus();
        return;
    }

    if (password.value !== confirmPassword.value) {
        alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        confirmPassword.focus();
        return;
    }

    if (!zipcode.value) {
        alert("우편번호를 입력해주세요.");
        zipcode.focus();
        return;
    }

    if (!city.value) {
        alert("주소를 입력해주세요.");
        city.focus();
        return;
    }

    if (!username.value) {
        alert("이름을 입력해주세요.");
        username.focus();
        return;
    }

        return true; // 모든 검증 통과
    }

// == 다음 카카오 주소 API == //
    function execDaumPostcode() {
        new daum.Postcode({
            oncomplete: function (data) {
                var addr = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;

                if (data.userSelectedType === 'R') {
                    var extraAddr = '';
                    if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                        extraAddr += data.bname;
                    }
                    if (data.buildingName !== '' && data.apartment === 'Y') {
                        extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                    }
                    if (extraAddr !== '') {
                        extraAddr = ' (' + extraAddr + ')';
                    }
                    document.getElementById("street_detail").value = extraAddr;
                } else {
                    document.getElementById("street_detail").value = '';
                }

                document.getElementById('zipcode').value = data.zonecode;
                document.getElementById("street").value = addr;
                document.getElementById("street_detail").focus();

                // 주소로 위도와 경도 정보를 검색
                const geocoder = new kakao.maps.services.Geocoder();
                geocoder.addressSearch(addr, function (results, status) {
                    if (status === kakao.maps.services.Status.OK) {
                        latitude = results[0].y;  // 위도
                        longitude = results[0].x;  // 경도
                    } else {
                        console.error("주소를 찾지 못했습니다.");
                    }
                });
            }
        }).open();
    }