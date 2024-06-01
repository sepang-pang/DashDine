import {fetchWithAuth} from './api.js';

import {
    displayNoRestaurantsAvailableMessage,
    populateCardWithRestaurantByCategory,
    showSection
} from './ui.js';

document.addEventListener('DOMContentLoaded', () => {

    // UI 이벤트 리스너
    setupEventListeners();

});

function setupEventListeners() {
    // 모든 카테고리 아이템을 선택
    const categoryItems = document.querySelectorAll(".category_item");

    // 각 카테고리 아이템에 대해 클릭 이벤트 리스너 추가
    categoryItems.forEach(item => {
        item.addEventListener('click', function () {
            const categoryId = this.getAttribute('data-category-id');

            // 추출된 categoryId를 이용해 필요한 함수 호출
            showSection('customer_restaurant_view'); // 예시: 섹션 보이기
            loadRestaurantsByCategory(categoryId); // 예시: 해당 카테고리 ID를 이용해 레스토랑 로드
        });
    });
}

function loadRestaurantsByCategory(categoryId) {
    highlightSelectedCategory(categoryId)
    fetchWithAuth(`/category/${categoryId}/restaurant`, {
        method: 'GET'
    })
        .then(restaurants => {
            const restaurantContainer = document.querySelector('.restaurant_list');

            restaurantContainer.innerHTML = '';

            if (restaurants.length === 0) {
                restaurantContainer.style.display = "block";
                displayNoRestaurantsAvailableMessage();
            } else {
                restaurants.forEach(restaurant => {
                    restaurantContainer.style.display = "grid";
                    const card = document.createElement('div');
                    card.classList.add('restaurant_form');
                    populateCardWithRestaurantByCategory(card, restaurant);
                    restaurantContainer.appendChild(card);
                });
            }
        })
        .catch(error => {
            console.error('Error loading restaurants:', error);
        });
}

function highlightSelectedCategory(selectedCategoryId) {
    const navbarItems = document.querySelectorAll('.navbar_menu li');
    navbarItems.forEach(item => {

        item.classList.remove('selected');

        if (item.getAttribute('data-category-id') === selectedCategoryId) {
            item.classList.add('selected');
        }
    });
}