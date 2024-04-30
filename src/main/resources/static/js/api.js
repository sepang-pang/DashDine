// api.js
export const fetchWithAuth = async (url, options) => {
    const accessToken = localStorage.getItem('accessToken');
    if (!options.headers) {
        options.headers = {};
    }
    options.headers['Authorization'] = accessToken;
    options.headers['Content-Type'] = 'application/json';

    let response = await fetch(url, options);
    if (response.status === 401) {
        const newAccessToken = response.headers.get('Authorization');
        if (newAccessToken) {
            localStorage.setItem('accessToken', newAccessToken);
            options.headers['Authorization'] = newAccessToken;
            response = await fetch(url, options);  // 재요청
        } else {
            clearAuthTokens();
            alert("세션 만료 ! 다시 로그인 해주세요.")
        }
    }
    if (!response.ok) {
        const errorResponse = await response.json();
        const errorMessage = errorResponse.errorMessage;
        alert(`${errorMessage}`);
        throw new Error(errorMessage);
    }

    return response.json();
};

const clearAuthTokens = () => {
    localStorage.removeItem('accessToken');
    document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/;';
    window.location.href = '/login';
};
