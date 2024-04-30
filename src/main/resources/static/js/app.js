// app.js
import {fetchWithAuth} from './api.js';
import {
    displayNoRestaurantMessage,
    populateCardWithRestaurantInfo,
    showSection,
    handleBackButtonEvent,
    execDaumPostcode
} from './ui.js';

document.addEventListener('DOMContentLoaded', () => {

    // 홈 버튼 이벤트 리스너
    document.getElementById("home_btn").addEventListener("click", function () {
        window.location.replace("/");
    });

    // 회원가입 버튼 이벤트 리스너
    document.getElementById("signup_btn").addEventListener("click", function () {
        window.location.href = '/user/signup';
    });

    // 로그인 버튼 이벤트 리스너
    document.getElementById("login_btn").addEventListener("click", function () {
        window.location.href = "/user/login-page";
    });

    document.getElementById("cancel_btn").addEventListener("click", function () {
        window.history.back();
    });

    document.getElementById('store_management_btn').addEventListener('click', () => {
        showSection('restaurant_container');
        refreshRestaurantList();
    });

    document.getElementById('zipcode').addEventListener('click', () => {
        execDaumPostcode();
    });

    document.getElementById('add_restaurant').addEventListener('click', () => {
        showSection('add_restaurant_container');
    });

    document.getElementById('phone_area_code').addEventListener('input', enforceNumericOnly);
    document.getElementById('phone_middle_digits').addEventListener('input', enforceNumericOnly);
    document.getElementById('phone_last_digits').addEventListener('input', enforceNumericOnly);

    function enforceNumericOnly(event) {
        event.target.value = event.target.value.replace(/\D/g, '');
    }

    document.getElementById('minimum_price').addEventListener('input', function (event) {
        let value = event.target.value.replace(/,/g, '');

        value = parseInt(value, 10);

        if (!isNaN(value)) {
            event.target.value = value.toLocaleString();
        } else {
            event.target.value = '';
        }
    });


    authenticateAndRoute();
    handleBackButtonEvent();
});

function authenticateAndRoute() {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) {
        console.error('Access token is missing.');
        showSection('main_section');
        return;
    }

    const payload = JSON.parse(atob(accessToken.split('.')[1]));
    if (payload.auth === 'OWNER') {
        showSection('owner_main_section');
        refreshRestaurantList();
    } else {
        showSection('main_section');
    }
}

function refreshRestaurantList() {
    fetchWithAuth('/owner/restaurant', {method: 'GET'})
        .then(restaurants => {
            const restaurantContainer = document.querySelector('.restaurant_container');
            const existingCards = restaurantContainer.querySelectorAll('.restaurant_card, .no_restaurant_notice');
            existingCards.forEach(element => element.remove());

            if (restaurants.length === 0) {
                displayNoRestaurantMessage();
            } else {
                restaurants.forEach(restaurant => {
                    const card = document.createElement('article');
                    card.classList.add('restaurant_card');
                    restaurantContainer.appendChild(populateCardWithRestaurantInfo(card, restaurant));
                    const buttonGroup = restaurantContainer.querySelector('.button_group');
                    restaurantContainer.insertBefore(card, buttonGroup);
                });
            }
        })
        .catch(error => {
            console.error(error);
            displayNoRestaurantMessage();
        });
}

document.getElementById('registration_btn').addEventListener('click', function () {
    const postData = addRestaurantFormData();
    fetchWithAuth('/owner/restaurant', {
        method: 'POST',
        body: JSON.stringify(postData)
    }).then(() => {
        alert('가게가 성공적으로 등록되었습니다.');
        document.getElementById('restaurant_form').reset();
        showSection('restaurant_container');
        refreshRestaurantList();
    }).catch(error => {
        console.error('가게 등록 실패:', error);
        if (error.message.includes("이미 동일한 이름의 가게를 보유중입니다.")) {
            document.getElementById('restaurant_name').focus();
        }
    });
});

function addRestaurantFormData() {
    // 필드를 미리 선택
    const restaurantName = document.getElementById('restaurant_name');
    const phoneAreaCode = document.getElementById('phone_area_code');
    const phoneMiddleDigits = document.getElementById('phone_middle_digits');
    const phoneLastDigits = document.getElementById('phone_last_digits');
    const restaurantDesc = document.getElementById('restaurant_desc');
    const minimumPrice = document.getElementById('minimum_price');
    const restaurantOpening = document.getElementById('restaurant_opening');
    const restaurantClosing = document.getElementById('restaurant_closing');
    const restaurantCategory = document.getElementById('restaurant_category');
    const street = document.getElementById('street');
    const streetDetail = document.getElementById('street_detail');
    const zipcode = document.getElementById('zipcode');
    const longitude = document.getElementById('longitude');
    const latitude = document.getElementById('latitude');

    // 각 필드 검증
    if (!restaurantName.value) {
        alert("가게 이름을 입력해주세요.");
        restaurantName.focus();
        return null;
    }

    if (!phoneAreaCode.value || !phoneMiddleDigits.value || !phoneLastDigits.value) {
        alert("전화번호를 올바르게 입력해주세요.");
        phoneAreaCode.focus();
        return null;
    }

    if (!zipcode.value) {
        alert("우편번호를 입력해주세요.");
        zipcode.focus();
        return null;
    }

    if (!restaurantDesc.value) {
        alert("가게 설명을 입력해주세요.");
        restaurantDesc.focus();
        return null;
    }

    if (!minimumPrice.value) {
        alert("최소 주문 가격을 입력해주세요.");
        minimumPrice.focus();
        return null;
    }

    if (!restaurantOpening.value) {
        alert("영업 시작 시간을 입력해주세요.");
        restaurantOpening.focus();
        return null;
    }

    if (!restaurantClosing.value) {
        alert("영업 종료 시간을 입력해주세요.");
        restaurantClosing.focus();
        return null;
    }

    return {
        name: restaurantName.value,
        tel: `${phoneAreaCode.value}-${phoneMiddleDigits.value}-${phoneLastDigits.value}`,
        info: restaurantDesc.value,
        minimumPrice: parseInt(minimumPrice.value.replace(/,/g, ''), 10),
        openingTime: restaurantOpening.value,
        closingTime: restaurantClosing.value,
        categoryId: parseInt(restaurantCategory.value),
        street: street.value,
        streetDetail: streetDetail.value,
        zipcode: zipcode.value,
        longitude: longitude.value,
        latitude: latitude.value
    };
}