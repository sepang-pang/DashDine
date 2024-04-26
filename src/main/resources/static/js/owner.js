document.getElementById('store_management_btn').addEventListener('click', function () {
    document.getElementById('owner_main_section').style.display = 'none';
    const restaurantContainer = document.querySelector('.restaurant_container');
    restaurantContainer.style.display = 'flex';

    const accessToken = localStorage.getItem('accessToken');

    // API 호출
    fetch('/owner/restaurant', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': accessToken
        }
    })
        .then(response => {
            if (response.status === 401) {
                // 토큰 만료 혹은 잘못된 토큰 처리
                const newAccessToken = response.headers.get('Authorization');
                if (newAccessToken) {
                    localStorage.setItem('accessToken', newAccessToken);
                    return fetch('/owner/restaurant', {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': newAccessToken
                        }
                    }).then(response => response.json());
                } else {
                    throw new Error("401 응답에 새로운 토큰이 제공되지 않았습니다");
                }
            } else if (response.status === 404) {
                // 데이터 없음 처리
                const noRestaurantDiv = document.createElement('article');
                noRestaurantDiv.classList.add('no_restaurant_notice');
                noRestaurantDiv.innerHTML = `<h1>아직 등록된 가게가 없어요 😵‍💫</h1>`;
                restaurantContainer.insertBefore(noRestaurantDiv, restaurantContainer.querySelector('.button_group'));
                return;  // 이후 처리 중단
            }
            return response.json();  // 정상 응답 처리
        })
        .then(restaurants => {
            if (restaurants) {
                restaurants.forEach(restaurant => {
                    const card = document.createElement('article');
                    card.classList.add('restaurant_card');
                    card.innerHTML = `
                    <div class="card_left">
                        <div class="store_info">
                            <h3>${restaurant.name}</h3>
                            <span class="category">${restaurant.category}</span>
                        </div>
                        <div class="contact_info">
                            <p>전화번호: <span>${restaurant.tel}</span></p>
                            <p>영업시간: <span>${restaurant.openingTime}</span> ~ <span>${restaurant.closingTime}</span></p>
                            <p>최소 주문 금액: <span>${restaurant.minimumPrice}</span>원</p>
                            <p>위치: <span id='restaurant_street'>${restaurant.street}</span><span id='restaurant_street_detail'>${restaurant.streetDetail}</span></p>
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
                    restaurantContainer.insertBefore(card, restaurantContainer.querySelector('.button_group'));
                });
            }
        })
        .catch(error => {
            console.error('가게 로딩 중 오류 발생:', error);
        });
});
