// // owner.js
// const accessToken = localStorage.getItem('accessToken');
//
// // 페이지 로드 시 상태 확인 및 섹션 표시
// window.onload = function () {
//     let currentSection = history.state ? history.state.section : 'owner_main_section';
//     showSection(currentSection, false);
// };
//
// // 뒤로 가기 이벤트 처리
// window.onpopstate = function (event) {
//     if (event.state && event.state.section) {
//         showSection(event.state.section, false);
//     }
// };
//
// // 섹션 표시 함수
// function showSection(sectionClass, pushHistory = true) {
//     document.querySelector('.owner_main_section').style.display = 'none';
//     document.querySelector('.restaurant_container').style.display = 'none';
//     document.querySelector('.add_restaurant_container').style.display = 'none';
//     document.querySelector('.main_section').style.display = 'none';
//
//     document.querySelector(`.${sectionClass}`).style.display = 'flex';
//
//     if (pushHistory) {
//         history.pushState({ section: sectionClass }, '', '/');
//     }
// }
//
// // 가게관리 클릭 이벤트 핸들러
// document.getElementById('store_management_btn').addEventListener('click', function () {
//     showSection('restaurant_container');
//     refreshRestaurantList();
// });
//
// // 가게 목록 새로고침
// function refreshRestaurantList() {
//     const restaurantContainer = document.querySelector('.restaurant_container');
//     const existingCards = restaurantContainer.querySelectorAll('.restaurant_card, .no_restaurant_notice');
//     existingCards.forEach(card => card.remove());
//
//     fetch('/owner/restaurant', {
//         method: 'GET',
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': accessToken
//         }
//     })
//         .then(response => handleResponse(response, accessToken))
//         .then(restaurants => {
//             if (restaurants) {
//                 restaurants.forEach(restaurant => {
//                     const card = document.createElement('article');
//                     card.classList.add('restaurant_card');
//                     populateCardWithRestaurantInfo(card, restaurant);
//                     restaurantContainer.insertBefore(card, restaurantContainer.querySelector('.button_group'));
//                 });
//             }
//         })
//         .catch(error => {
//             console.error('가게 로딩 중 오류 발생:', error);
//         });
// }
//
// // 가게 등록 클릭 이벤트 리스너
// document.getElementById('add_restaurant').addEventListener('click', function () {
//     showSection('add_restaurant_container');
// });
//
// document.getElementById('registration_btn').addEventListener('click', function () {
//     const postData = collectFormData();
//     const requestInit = {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': accessToken
//         },
//         body: JSON.stringify(postData)
//     };
//
//     fetch('/owner/restaurant', requestInit)
//         .then(response => handleResponse(response, requestInit, '/owner/restaurant'))
//         .then(() => {
//             alert('가게가 성공적으로 등록되었습니다.');
//             showSection('restaurant_container');
//             refreshRestaurantList();
//         })
//         .catch(error => {
//             console.error('가게 등록 실패:', error);
//             alert('가게 등록에 실패하였습니다. 에러를 확인해주세요.');
//         });
// });
//
// function collectFormData() {
//     return {
//         name: document.getElementById('restaurant_name').value,
//         tel: document.getElementById('phone_area_code').value + '-' +
//             document.getElementById('phone_middle_digits').value + '-' +
//             document.getElementById('phone_last_digits').value,
//         info: document.getElementById('restaurant_desc').value,
//         minimumPrice: 0, // 예제 값
//         openingTime: document.getElementById('restaurant_opening').value,
//         closingTime: document.getElementById('restaurant_closing').value,
//         categoryId: 1,
//         street: document.getElementById('street').value,
//         streetDetail: document.getElementById('street_detail').value,
//         zipcode: document.getElementById('zipcode').value,
//         longitude: longitude, // 해당 값 설정 필요
//         latitude: latitude // 해당 값 설정 필요
//     };
// }
//
// function handleResponse(response, requestInit, retryRequest) {
//     if (response.status === 401) {
//         const newAccessToken = response.headers.get('Authorization');
//         if (newAccessToken) {
//             localStorage.setItem('accessToken', newAccessToken);
//             requestInit.headers['Authorization'] = newAccessToken;
//             return fetch(retryRequest, requestInit).then(res => handleResponse(res, requestInit, retryRequest));
//         } else {
//             clearAuthTokens();
//             throw new Error('Session expired. Please login again.');
//         }
//     } else if (response.status === 404) {
//         displayNoRestaurantMessage();
//         return Promise.resolve(null);
//     } else if (!response.ok) {
//         throw new Error(`Server error: ${response.status}`);
//     }
//     return response.json();
// }
//
// function displayNoRestaurantMessage() {
//     const noRestaurantDiv = document.createElement('article');
//     noRestaurantDiv.classList.add('no_restaurant_notice');
//     noRestaurantDiv.innerHTML = `<h1>아직 등록된 가게가 없어요 😵‍💫</h1>`;
//     const restaurantContainer = document.querySelector('.restaurant_container');
//     restaurantContainer.insertBefore(noRestaurantDiv, restaurantContainer.querySelector('.button_group'));
// }
//
// function populateCardWithRestaurantInfo(card, restaurant) {
//     card.innerHTML = `
//     <div class="card_left">
//         <div class="store_info">
//             <h3>${restaurant.name}</h3>
//             <span class="category">${restaurant.category}</span>
//         </div>
//         <div class="contact_info">
//             <p>전화번호: <span>${restaurant.tel}</span></p>
//             <p>영업시간: <span>${restaurant.openingTime}</span> ~ <span>${restaurant.closingTime}</span></p>
//             <p>최소 주문 금액: <span>${restaurant.minimumPrice}</span>원</p>
//             <p>위치: <span id='restaurant_street'>${restaurant.street}</span><span id='restaurant_street_detail'>${restaurant.streetDetail}</span></p>
//         </div>
//     </div>
//     <div class="card_right">
//         <div class="card_right_content">
//             <button type="button" class="manage_menu">메뉴 관리</button>
//             <div class="store_image"></div>
//             <div class="edit_delete_buttons">
//                 <button class="edit">수정</button>
//                 <button class="delete">삭제</button>
//             </div>
//         </div>
//     </div>
//     `;
// }
//
// function clearAuthTokens() {
//     localStorage.removeItem('accessToken');
//     document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/;';
//     window.location.href = '/login';
// }
