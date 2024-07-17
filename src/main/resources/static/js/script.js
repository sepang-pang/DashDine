document.getElementById("modify-profile-btn").addEventListener("click", function () {
    const formData = new FormData();
    const param = {
        nickName: document.getElementById("nick-name").value,
        zipcode: document.getElementById("zip-code").value,
        address: document.getElementById("address").value,
        addressDetail: document.getElementById("address-detail").value,
        longitude: document.getElementById("longitude").value,
        latitude : document.getElementById("latitude").value
    }

    formData.append('param', new Blob([JSON.stringify(param)], {type: 'application/json'}));
    // formData.append('userImage', document.getElementById("file-input").files[0]);

    fetch("/user/profile-setup", {
        method: 'PATCH',
        body: formData
    }).then(response => response.json())
        .then(data => {
            console.log("Success: ", data);
            window.location.href = "/";
        })
        .catch((error) => {
            console.error("Error: ", error);
            alert("프로필 업데이트에 실패했습니다.");
        });
});

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
                    extraAddr = '(' + extraAddr + ')';
                }
                document.getElementById("address-detail").value = extraAddr;
            } else {
                document.getElementById("address-detail").value = '';
            }

            document.getElementById('zip-code').value = data.zonecode;
            document.getElementById("address").value = addr;
            document.getElementById("address-detail").focus();

            // 주소로 위도와 경도 정보를 검색
            const geocoder = new kakao.maps.services.Geocoder();
            geocoder.addressSearch(addr, function (results, status) {
                if (status === kakao.maps.services.Status.OK) {
                    document.getElementById('latitude').value = results[0].y;  // 위도
                    document.getElementById('longitude').value = results[0].x; // 경도
                } else {
                    console.error("주소를 찾지 못했습니다.");
                }
            });
        }
    }).open();
}