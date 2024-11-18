import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Settings.css";

const Settings = () => {
    const [userData, setUserData] = useState({ email: '', name: '', birthDate: '', gender: '' });

    useEffect(() => {
        const token = localStorage.getItem("token"); // JWT 토큰 가져오기
        if (!token) {
            console.error("No token found. Please log in again.");
            alert("로그인이 필요합니다. 다시 로그인해 주세요.");
            window.location.href = "/login";
            return;
        }

        // 사용자 정보를 가져오는 API 호출
        axios
            .get("http://localhost:8080/api/settings/account", {
                headers: { Authorization: `Bearer ${token}` }, // Authorization 헤더에 토큰 추가
            })
            .then((response) => {
                setUserData(response.data);
            })
            .catch((error) => {
                console.error("사용자 정보 가져오기 오류:", error);
                alert("사용자 정보를 불러오지 못했습니다. 다시 시도해 주세요.");
            });
    }, []);

    const handleDeleteChatHistory = () => {
        const token = localStorage.getItem("token"); // JWT 토큰 가져오기
        axios
            .delete("http://localhost:8080/api/settings/chat-history", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then(() => {
                alert("채팅 기록이 성공적으로 삭제되었습니다.");
            })
            .catch((error) => {
                console.error("채팅 기록 삭제 중 오류 발생:", error);
                alert("채팅 기록 삭제에 실패했습니다.");
            });
    };

    const handleEditAccount = () => {
        window.location.href = "/edit-account"; // 사용자 정보 수정 페이지로 이동
    };

    const handleChangePassword = () => {
        window.location.href = "/change-password"; // 비밀번호 변경 페이지로 이동
    };

    return (
        <div className="settings-container">
            <h2>Settings</h2>
            <div className="account-info">
                <h3>Account Information</h3>
                <p><strong>Email:</strong> {userData.email}</p>
                <p><strong>Name:</strong> {userData.name}</p>
                <p><strong>Birth Date:</strong> {userData.birthDate}</p>
                <p><strong>Gender:</strong> {userData.gender}</p>
            </div>
            <div className="buttons-section">
                <button onClick={handleDeleteChatHistory}>Delete Chat History</button>
                <button onClick={handleEditAccount}>Edit Account Info</button>
                <button onClick={handleChangePassword}>Change Password</button>
            </div>
        </div>
    );
};

export default Settings;
