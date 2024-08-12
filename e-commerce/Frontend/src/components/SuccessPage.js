import React, { useEffect } from 'react';

const SuccessPage = () => {
    useEffect(() => {
        const token = localStorage.getItem('token'); // 로그인 후 저장된 토큰 확인
        if (token) {
            console.log("Login successful, token found:", token);
        } else {
            console.error("Login failed, no token found.");
        }
    }, []);

    return (
        <div>
            <h1>로그인 성공</h1>
        </div>
    );
};

export default SuccessPage;
