// == 회원 가입버튼 이벤트 리스너 == //
document.getElementById('signupButton').addEventListener('click', function() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/signup', true);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            document.getElementById('mainSection').innerHTML = xhr.responseText;
            document.getElementById('mainSection').style.display = 'block';
        }
    };
    xhr.send();
});