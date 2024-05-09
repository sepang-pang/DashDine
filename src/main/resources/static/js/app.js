// app.js
import {fetchWithAuth} from './api.js';
import {
    displayNoRestaurantMessage,
    displayNoMenuMessage,
    populateCardWithRestaurantInfo,
    showSection,
    handleBackButtonEvent,
    execDaumPostcode, populateCardWithMenuInfo
} from './ui.js';

// ==================== 초기 설정 및 이벤트 리스너 설정 ==================== //
// DOM 준비 완료 후 이벤트 리스너 설정
document.addEventListener('DOMContentLoaded', () => {
    // 인증 상태에 따른 라우팅 설정
    authenticateAndRoute();

    // UI 이벤트 리스너
    setupEventListeners();

    // 뒤로가기 버튼 이벤트 처리
    handleBackButtonEvent();
});

// 이벤트 리스너 설정
function setupEventListeners() {
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

    // 취소 버튼 이벤트 리스너
    document.getElementById('cancel_btn').addEventListener('click', function () {
        window.history.back();
    });

    // 가게 관리 버튼 이벤트 리스너
    document.getElementById('store_management_btn').addEventListener('click', () => {
        showSection('restaurant_container');
        refreshRestaurantList();
    });

    // 우편 번호 이벤트 리스너
    document.getElementById('zipcode').addEventListener('click', () => {
        execDaumPostcode();
    });

    // 가게 등록 버튼 이벤트 리스너
    document.getElementById('add_restaurant').addEventListener('click', () => {
        resetRestaurantForm()
        document.getElementById('registration_btn').style.display = 'block';
        showSection('restaurant_editor_container');
    });

    // 휴대폰 입력 필드 숫자만 입력 처리
    document.getElementById('phone_area_code').addEventListener('input', enforceNumericOnly);
    document.getElementById('phone_middle_digits').addEventListener('input', enforceNumericOnly);
    document.getElementById('phone_last_digits').addEventListener('input', enforceNumericOnly);

    // 최소 주문 금액 입력 처리
    document.getElementById('minimum_price').addEventListener('input', function (event) {
        let value = event.target.value.replace(/,/g, '');
        value = parseInt(value, 10);
        if (!isNaN(value)) {
            event.target.value = value.toLocaleString();
        } else {
            event.target.value = '';
        }
    });

    // 수정 가능한 가게 정보 카드에 이벤트 리스너 추가
    document.querySelector('.restaurant_container').addEventListener('click', event => {
        if (event.target.classList.contains('edit')) {
            const restaurantId = event.target.closest('.restaurant_card').dataset.restaurantId;
            fetchAndShowEditForm(restaurantId);
        }
    });

    // 메뉴 관리 이벤트 리스너
    document.querySelector('.restaurant_container').addEventListener('click', event => {
        if (event.target.classList.contains('manage_menu')) {
            const restaurantId = event.target.closest('.restaurant_card').dataset.restaurantId;
            showSection('restaurant_menu_container')
            refreshMenuList(restaurantId)
        }
    });

    // 브라우저 뒤로가기 이벤트 처리
    window.addEventListener('popstate', function (event) {
        if (event.state && event.state.section !== 'restaurant_editor_container') {
            document.getElementById('registration_btn').style.display = 'none';
            document.getElementById('edit_btn').style.display = 'none';
        }
    });
}

// 인증 상태에 따른 섹션 보여주기
function authenticateAndRoute() {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) {
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

// ==================== 가게 정보 처리 함수 ==================== //
// 가게 목록 갱신
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

// 선택된 가게 수정을 위한 데이터 로딩 및 폼 채우기
function fetchAndShowEditForm(restaurantId) {
    fetchWithAuth(`/owner/restaurant/${restaurantId}`, { method: 'GET' })
        .then(restaurant => {
            fillEditForm(restaurant);
            showSection('restaurant_editor_container');
            document.getElementById('restaurant_editor_form').dataset.restaurantId = restaurantId;
            document.getElementById('edit_btn').style.display = 'block';
            document.getElementById('registration_btn').style.display = 'none';
        })
        .catch(error => console.error('가게 정보 불러오기 실패:', error));
}

// 폼 데이터 채우기
function fillEditForm(restaurant) {
    document.getElementById('restaurant_name').value = restaurant.name;
    document.getElementById('phone_area_code').value = restaurant.tel.split('-')[0];
    document.getElementById('phone_middle_digits').value = restaurant.tel.split('-')[1];
    document.getElementById('phone_last_digits').value = restaurant.tel.split('-')[2];
    document.getElementById('zipcode').value = restaurant.zipcode;
    document.getElementById('street').value = restaurant.street;
    document.getElementById('street_detail').value = restaurant.streetDetail;
    document.getElementById('restaurant_category').value = restaurant.categoryId;
    document.getElementById('restaurant_desc').value = restaurant.info;
    document.getElementById('minimum_price').value = restaurant.minimumPrice.toLocaleString();
    document.getElementById('restaurant_opening').value = restaurant.openingTime;
    document.getElementById('restaurant_closing').value = restaurant.closingTime;
}

// 가게 등록 처리
document.getElementById('registration_btn').addEventListener('click', function () {
    const postData = collectRestaurantFormData();
    fetchWithAuth('/owner/restaurant', {
        method: 'POST',
        body: JSON.stringify(postData)
    }).then(() => {
        alert('가게가 성공적으로 등록되었습니다.');
        document.getElementById('restaurant_editor_form').reset();
        showSection('restaurant_container');
        refreshRestaurantList();
    }).catch(error => {
        console.error('가게 등록 실패:', error);
        if (error.message.includes("이미 동일한 이름의 가게를 보유중입니다.")) {
            document.getElementById('restaurant_name').focus();
        }
    });
});

// 가게 수정 처리
document.getElementById('edit_btn').addEventListener('click', () => {
    const restaurantId = document.getElementById('restaurant_editor_form').dataset.restaurantId;
    const postData = collectRestaurantFormData();
    fetchWithAuth(`/owner/restaurant/${restaurantId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(postData)
    })
        .then(() => {
            alert('가게가 성공적으로 수정되었습니다.');
            document.getElementById('restaurant_editor_form').reset();
            showSection('restaurant_container');
            refreshRestaurantList();
        })
        .catch(error => {
            console.error('가게 수정 실패:', error);
            if (error.message.includes("이미 동일한 이름의 가게를 보유중입니다.")) {
                document.getElementById('restaurant_name').focus();
            }
        });
});

// 가게 삭제 처리
document.addEventListener('DOMContentLoaded', function() {
    const restaurantContainer = document.querySelector('.restaurant_container');
    restaurantContainer.addEventListener('click', function(event) {
        if (event.target.classList.contains('delete')) {
            const restaurantId = event.target.closest('.restaurant_card').dataset.restaurantId;
            showModal(restaurantId);
        }
    });

    const modal = document.getElementById('deleteModal');
    const confirmDeleteBtn = document.getElementById('confirmDelete');
    const cancelDeleteBtn = document.getElementById('cancelDelete');

    confirmDeleteBtn.addEventListener('click', function() {
        const restaurantId = this.dataset.restaurantId;
        deleteRestaurant(restaurantId);
    });

    cancelDeleteBtn.addEventListener('click', function() {
        hideModal();
    });

    function showModal(restaurantId) {
        confirmDeleteBtn.dataset.restaurantId = restaurantId;
        modal.style.display = 'flex';
    }

    function hideModal() {
        modal.style.display = 'none';
    }

    function deleteRestaurant(restaurantId) {
        fetchWithAuth(`/owner/restaurant/${restaurantId}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
        })
            .then(() => {
                alert('가게가 성공적으로 삭제되었습니다.');
                refreshRestaurantList();
                hideModal();
            })
            .catch(error => {
                console.error('가게 삭제 실패:', error);
            });
    }
});

// ==================== 메뉴 처리 함수 ==================== //
function refreshMenuList(restaurantId) {
    fetchWithAuth(`/owner/restaurant/${restaurantId}/menu`, {method: 'GET'})
        .then(menus => {
            const menuContainer = document.querySelector('.restaurant_menu_container');
            const existingCards = menuContainer.querySelectorAll('.menu_card, .no_menu_notice');
            existingCards.forEach(element => element.remove());
            if (menus.length === 0) {
                displayNoMenuMessage();
            } else {
                menus.forEach(menu => {
                    const card = document.createElement('article');
                    card.classList.add('menu_card');
                    menuContainer.appendChild(populateCardWithMenuInfo(card, menu));
                    const buttonGroup = menuContainer.querySelector('.button_group');
                    menuContainer.insertBefore(card, buttonGroup);
                });
            }
        })
        .catch(error => {
            console.error(error);
            displayNoRestaurantMessage();
        });
}

// 메뉴 삭제 처리
document.addEventListener('DOMContentLoaded', function() {
    const menuContainer = document.querySelector('.restaurant_menu_container');
    menuContainer.addEventListener('click', function(event) {
        if (event.target.classList.contains('delete')) {
            const menuCard = event.target.closest('.menu_card');
            const menuId = menuCard.dataset.menuId;
            const restaurantId = menuCard.dataset.restaurantId;
            showModal(menuId, restaurantId);
        }
    });

    const modal = document.getElementById('delete_option_modal');
    const confirmDeleteBtn = document.getElementById('confirm_delete_option');
    const cancelDeleteBtn = document.getElementById('cancel_delete_option');

    confirmDeleteBtn.addEventListener('click', function() {
        const menuId = this.dataset.menuId;
        const restaurantId = this.dataset.restaurantId;
        deleteMenu(menuId, restaurantId);
    });

    cancelDeleteBtn.addEventListener('click', function() {
        hideModal();
    });

    function showModal(menuId, restaurantId) {
        confirmDeleteBtn.dataset.menuId = menuId;
        confirmDeleteBtn.dataset.restaurantId = restaurantId;
        modal.style.display = 'flex';
    }

    function hideModal() {
        modal.style.display = 'none';
    }
    function deleteMenu(menuId, restaurantId) {
        fetchWithAuth(`/owner/menu/${menuId}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
        })
            .then(() => {
                alert('메뉴가 성공적으로 삭제되었습니다.');
                refreshMenuList(restaurantId);
                hideModal();
            })
            .catch(error => {
                console.error('메뉴 삭제 실패:', error);
            });
    }
});

// ==================== 유틸리티 함수 ==================== //
// 가게 데이터 폼 준비 함수
function collectRestaurantFormData() {
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

    // 폼 데이터 객체 생성 및 반환
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

// 가게 등록 폼 초기화
function resetRestaurantForm() {
    document.getElementById('restaurant_editor_form').reset();
}

// 숫자 입력 강제화 함수
function enforceNumericOnly(event) {
    event.target.value = event.target.value.replace(/\D/g, '');
}
