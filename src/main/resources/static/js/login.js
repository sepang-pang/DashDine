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

            window.location.href = '/';
        })
        .catch(error => {
            console.error('오류:', error);
        });
});



