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
    const postData = collectFormData();
    fetchWithAuth('/owner/restaurant', {
        method: 'POST',
        body: JSON.stringify(postData)
    }).then(() => {
        alert('가게가 성공적으로 등록되었습니다.');
        showSection('restaurant_container');
        refreshRestaurantList();
    }).catch(error => {
        console.error('가게 등록 실패:', error);
    });
});

function collectFormData() {
    return {
        name: document.getElementById('restaurant_name').value,
        tel: document.getElementById('phone_area_code').value + '-' +
            document.getElementById('phone_middle_digits').value + '-' +
            document.getElementById('phone_last_digits').value,
        info: document.getElementById('restaurant_desc').value,
        minimumPrice: parseInt(document.getElementById('minimum_price').value.replace(/,/g, ''), 10), // 예제 값
        openingTime: document.getElementById('restaurant_opening').value,
        closingTime: document.getElementById('restaurant_closing').value,
        categoryId: 1, // 예제 값
        street: document.getElementById('street').value,
        streetDetail: document.getElementById('street_detail').value,
        zipcode: document.getElementById('zipcode').value,
        longitude: document.getElementById('longitude').value,
        latitude: document.getElementById('latitude').value
    };
}

