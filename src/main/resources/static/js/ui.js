//  ui.js
export const displayNoRestaurantMessage = () => {
    const restaurantContainer = document.querySelector('.restaurant_container');

    const existingMessage = restaurantContainer.querySelector('.no_restaurant_notice');
    if (existingMessage) {
        existingMessage.remove();
    }

    const noRestaurantDiv = document.createElement('article');
    noRestaurantDiv.classList.add('no_restaurant_notice');
    noRestaurantDiv.innerHTML = `<h1>아직 등록된 가게가 없어요 😵‍💫</h1>`;
    restaurantContainer.appendChild(noRestaurantDiv);
    restaurantContainer.insertBefore(noRestaurantDiv, restaurantContainer.querySelector('.button_group'));
};


export const showSection = (sectionClass, pushHistory = true) => {
    const sections = document.querySelectorAll('section');
    sections.forEach(section => section.style.display = 'none');
    const targetSection = document.querySelector(`.${sectionClass}`);
    targetSection.style.display = 'flex';

    if (pushHistory) {
        history.pushState({ section: sectionClass }, '', window.location.pathname);
    }
};

export function handleBackButtonEvent() {
    window.onpopstate = function (event) {
        if (event.state && event.state.section) {
            showSection(event.state.section, false);
        }
    };
}

export const populateCardWithRestaurantInfo = (card, restaurant) => {
    card.innerHTML = `
    <div class="card_left">
        <div class="store_info">
            <h3>${restaurant.name}</h3>
             <span class="category category-${restaurant.categoryId}">${restaurant.category}</span>
        </div>
        <div class="contact_info">
            <p>전화번호: <span>${restaurant.tel}</span></p>
            <p>영업시간: <span>${restaurant.openingTime}</span> ~ <span>${restaurant.closingTime}</span></p>
            <p>최소 주문 금액: <span>${parseInt(restaurant.minimumPrice).toLocaleString()}</span>원</p>
            <p>위치: <span>${restaurant.street}</span><span> ${restaurant.streetDetail}</span></p>
        </div>
    </div>
    <div class="card_right">
        <div class="card_right_content">
            <button type="button" class="manage_menu">메뉴 관리</button>
            <div class="store_image"></div>
            <div class="edit_delete_buttons">
                <button class="edit">수정</button>
                <button class="delete">삭제</button>
            </div>
        </div>
    </div>
    `;
    return card;
};

export function execDaumPostcode() {
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
                    document.getElementById("latitude").value = results[0].y;
                    document.getElementById("longitude").value = results[0].x;
                } else {
                    console.error("주소를 찾지 못했습니다.");
                }
            });
        }
    }).open();
}


