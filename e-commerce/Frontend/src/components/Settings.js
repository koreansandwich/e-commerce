import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Settings.css";

const Settings = () => {
    const [userData, setUserData] = useState({ email: '', name: '', birthDate: '', gender: '' });
    const [showResetModal, setShowResetModal] = useState(false); // 기록 초기화 확인 모달 상태
    const [profileImage, setProfileImage] = useState(null); // 프로필 이미지

    // States for Edit Account Modal
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [newName, setNewName] = useState("");
    const [newBirthDate, setNewBirthDate] = useState("");
    const [newGender, setNewGender] = useState("");

    // States for Change Password Modal
    const [isChangePasswordModalOpen, setIsChangePasswordModalOpen] = useState(false);
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

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

    const handleProfileImageUpload = (e) => {
        const file = e.target.files[0];
        if (file) {
            setProfileImage(URL.createObjectURL(file));
            alert("프로필 사진이 업로드되었습니다.");
            // 프로필 이미지 업로드 API 호출 추가 가능
        }
    };

    const handleResetHistory = () => {
        const token = localStorage.getItem("token");
        if (!token) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
            return;
        }

        axios
            .delete("http://localhost:8080/api/settings/reset-history", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then(() => {
                alert("기록이 초기화되었습니다.");
            })
            .catch((error) => {
                console.error("기록 초기화 실패:", error);
                alert("기록 초기화에 실패했습니다. 다시 시도해주세요.");
            });
    };

    // Handlers for Edit Account Modal
    const handleOpenEditAccount = () => {
        setNewName(userData.name);
        setNewBirthDate(userData.birthDate);
        setNewGender(userData.gender);
        setIsEditModalOpen(true);
    };

    const handleCloseEditAccount = () => {
        setIsEditModalOpen(false);
    };

    const handleSaveEditAccount = () => {
        if (!newName.trim() || !newBirthDate || !newGender) {
            alert("모든 필드를 입력해주세요.");
            return;
        }

        const token = localStorage.getItem("token");
        axios
            .put(
                `http://localhost:8080/api/settings/account/${userData.id}`,
                {
                    name: newName,
                    birthDate: newBirthDate,
                    gender: newGender,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            )
            .then((response) => {
                alert("계정 정보가 성공적으로 업데이트되었습니다.");
                setUserData(response.data);
                setIsEditModalOpen(false);
            })
            .catch((error) => {
                console.error("계정 정보 업데이트 실패:", error);
                alert("계정 정보 업데이트에 실패했습니다. 다시 시도해주세요.");
            });
    };

    // Handlers for Change Password Modal
    const handleOpenChangePassword = () => {
        setCurrentPassword("");
        setNewPassword("");
        setConfirmPassword("");
        setIsChangePasswordModalOpen(true);
    };

    const handleCloseChangePassword = () => {
        setIsChangePasswordModalOpen(false);
    };

    const handleChangePassword = () => {
        if (!currentPassword || !newPassword || !confirmPassword) {
            alert("모든 필드를 입력해주세요.");
            return;
        }

        if (newPassword !== confirmPassword) {
            alert("새 비밀번호가 일치하지 않습니다.");
            return;
        }

        const token = localStorage.getItem("token");
        axios
            .put(
                "http://localhost:8080/api/settings/account/password",
                {
                    currentPassword: currentPassword,
                    newPassword: newPassword,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            )
            .then(() => {
                alert("비밀번호가 성공적으로 변경되었습니다.");
                setIsChangePasswordModalOpen(false);
            })
            .catch((error) => {
                console.error("비밀번호 변경 실패:", error);
                alert("비밀번호 변경에 실패했습니다. 현재 비밀번호를 확인하거나 다시 시도해주세요.");
            });
    };

    return (
        <div className="settings-container">
            <h2>Settings</h2>
            <div className="profile-image">
                <img
                    src={profileImage || "https://via.placeholder.com/100"} // 기본 이미지 또는 업로드된 이미지
                    alt="Profile"
                />
                <label htmlFor="profile-upload" className="edit-profile-button">
                    Edit
                </label>
                <input
                    type="file"
                    id="profile-upload"
                    accept="image/*"
                    onChange={handleProfileImageUpload}
                    style={{display: "none"}}
                />
            </div>
            <div className="profile-actions">
                <h3>Account Information</h3>
                <p><strong>Email:</strong> {userData.email}</p>
                <p><strong>Name:</strong> {userData.name}</p>
                <p><strong>Birth Date:</strong> {userData.birthDate}</p>
                <p><strong>Gender:</strong> {userData.gender}</p>
            </div>
            <div className="actions-buttons">
                <button onClick={handleOpenEditAccount} className="edit-account-button">Edit Account Info</button>
                <button onClick={handleOpenChangePassword} className="change-password-button">Change Password</button>
            </div>
            <div className="reset-history">
                <button
                    className="reset-history-button"
                    onClick={() => setShowResetModal(true)}
                >
                    기록 초기화
                </button>
            </div>

            {/* Edit Account Modal */}
            {isEditModalOpen && (
                <div className="modal">
                    <div className="modal-content">
                        <h3>Edit Account Information</h3>
                        <label>
                            Name:
                            <input
                                type="text"
                                value={newName}
                                onChange={(e) => setNewName(e.target.value)}
                            />
                        </label>
                        <label>
                            Birth Date:
                            <input
                                type="date"
                                value={newBirthDate}
                                onChange={(e) => setNewBirthDate(e.target.value)}
                            />
                        </label>
                        <label>Gender:</label>
                        <div className="gender-buttons">
                            <button
                                className={`gender-button ${newGender === "남성" ? "selected" : ""}`}
                                onClick={() => setNewGender("남성")}
                            >
                                남성
                            </button>
                            <button
                                className={`gender-button ${newGender === "여성" ? "selected" : ""}`}
                                onClick={() => setNewGender("여성")}
                            >
                                여성
                            </button>
                        </div>
                        <div className="modal-buttons">
                            <button onClick={handleSaveEditAccount} className="save-button">Save</button>
                            <button onClick={handleCloseEditAccount} className="cancel-button">Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Change Password Modal */}
            {isChangePasswordModalOpen && (
                <div className="modal">
                    <div className="modal-content">
                        <h3>Change Password</h3>
                        <label>
                            Current Password:
                            <input
                                type="password"
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                            />
                        </label>
                        <label>
                            New Password:
                            <input
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                            />
                        </label>
                        <label>
                            Confirm New Password:
                            <input
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                            />
                        </label>
                        <div className="modal-buttons">
                            <button onClick={handleChangePassword} className="save-button">Change Password</button>
                            <button onClick={handleCloseChangePassword} className="cancel-button">Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* 기록 초기화 확인 모달 */}
            {showResetModal && (
                <div className="modal">
                    <div className="modal-content">
                        <h3>정말 기록을 초기화하시겠습니까?</h3>
                        <p>챗봇 내역과 제품 내역이 모두 삭제됩니다.</p>
                        <div className="modal-buttons">
                            <button
                                className="confirm-button"
                                onClick={handleResetHistory}
                            >
                                확인
                            </button>
                            <button
                                className="cancel-button"
                                onClick={() => setShowResetModal(false)}
                            >
                                취소
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Settings;
