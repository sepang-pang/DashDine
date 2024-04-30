
document.addEventListener("DOMContentLoaded", function () {
    const homeBtn = document.getElementById("home_btn");
    const signupBtn = document.getElementById("signup_btn");
    const loginBtn = document.getElementById("login_btn");

    // 홈 버튼 이벤트 리스너
    homeBtn.addEventListener("click", function () {
        window.location.replace("/");
    })

    // 회원가입 버튼 이벤트 리스너
    signupBtn.addEventListener("click", function () {
        window.location.href = '/user/signup';
    });

    // 로그인 버튼 이벤트 리스너
    loginBtn.addEventListener("click", function () {
        window.location.href = "/user/login-page";
    });
});

document.getElementById('page_login_btn').addEventListener('click', function() {
    const loginId = document.getElementById('loginId').value;
    const password = document.getElementById('password').value;

    const loginData = {
        loginId: loginId,
        password: password
    };

    fetch('/user/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginData)
    })
        .then(response => {
            if (response.ok) {
                return response.headers.get('Authorization');
            } else {
                throw new Error('로그인 실패');
            }
        })
        .then(accessToken => {
            localStorage.setItem('accessToken', accessToken);
            console.log('로그인 성공 및 토큰 저장');

            window.location.replace("/");
        })
        .catch(error => {
            console.error('오류:', error);
        });
});



