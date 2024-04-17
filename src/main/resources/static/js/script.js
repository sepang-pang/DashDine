// 카카오 지오코더 객체 생성
let latitude, longitude;

document.addEventListener("DOMContentLoaded", function() {
    const mainSection = document.getElementById("mainSection");
    const signUpSection = document.querySelector(".registration-container");
    const signupBtn = document.getElementById("signupBtn");
    const registrationBtn = document.getElementById('registration-btn');
    const cancelBtn = document.getElementById("cancel-btn");

    // 회원가입 버튼 이벤트 리스너
    signupBtn.addEventListener("click", function() {
        mainSection.style.display = "none";  // 메인 섹션 숨기기
        signUpSection.style.display = "block";  // 회원가입 섹션 보이기
    });

    registrationBtn.addEventListener('click', function (event) {
        submitSignupForm(); // 회원가입 함수 호출
    });

    // 취소 버튼 이벤트 리스너
    cancelBtn.addEventListener("click", function() {
        signUpSection.style.display = "none";  // 회원가입 섹션 숨기기
        mainSection.style.display = "block";  // 메인 섹션 보이기
    });
});

function submitSignupForm() {
    const loginId = document.querySelector("input[placeholder='아이디']").value;
    const username = document.querySelector("input[placeholder='이름']").value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const email = document.querySelector("input[type='email']").value;
    const city = document.querySelector("input[placeholder='주소']").value;
    const street = document.querySelector("input[placeholder='상세주소']").value;
    const zipcode = document.querySelector("input[placeholder='우편번호']").value;

    // 유효성 검사
    if (!loginId.match(/^[a-z0-9]{4,10}$/)) {
        alert("ID는 4~10자리의 알파벳 소문자와 숫자만 포함해야 합니다.");
        return;
    }

    if (!username) {
        alert("이름을 입력해주세요.");
        return;
    }

    if (!password.match(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,16}$/)) {
        alert("비밀번호는 8~16자리이며, 영문 대소문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다.");
        return;
    }

    if (password !== confirmPassword) {
        alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        return;
    }

    if (!email.match(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)) {
        alert("올바른 이메일 주소를 입력해주세요.");
        return;
    }

    if (!city) {
        alert("주소를 입력해주세요.");
        return;
    }

    if (!zipcode) {
        alert("우편번호를 입력해주세요.");
        return;
    }

    const signupData = {
        loginId,
        username,
        password,
        email,
        city,
        street,
        zipcode,
        longitude,
        latitude
    };


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
            document.querySelector('#mainSection').style.display = 'block';
            document.querySelector('.registration-container').style.display = 'none';
        })
        .catch(error => {
            error.json().then(errData => {
                console.error('Error:', errData);
                alert(errData.errorMessage);  // 수정된 부분: errorMessage로 접근
            });
        });


}

// == 다음 카카오 주소 API == //
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
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
                document.getElementById("street-detail").value = extraAddr;
            } else {
                document.getElementById("street-detail").value = '';
            }

            document.getElementById('zipcode').value = data.zonecode;
            document.getElementById("street").value = addr;
            document.getElementById("street-detail").focus();

            // 주소로 위도와 경도 정보를 검색
            const geocoder = new kakao.maps.services.Geocoder();
            geocoder.addressSearch(addr, function(results, status) {
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

