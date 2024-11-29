import React, { useState, useEffect } from "react";
import axios from "axios";
import styles from "./Settings.module.css";

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
        const token = localStorage.getItem("token");
        if (!token) {
            alert("로그인이 필요합니다. 다시 로그인해 주세요.");
            window.location.href = "/login";
            return;
        }

        axios
            .get("http://localhost:8080/api/settings/account", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => setUserData(response.data))
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
            .then(() => alert("기록이 초기화되었습니다."))
            .catch((error) => {
                console.error("기록 초기화 실패:", error);
                alert("기록 초기화에 실패했습니다. 다시 시도해주세요.");
            });
    };

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
                { name: newName, birthDate: newBirthDate, gender: newGender },
                { headers: { Authorization: `Bearer ${token}` } }
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
                { currentPassword, newPassword },
                { headers: { Authorization: `Bearer ${token}` } }
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
        <div className={styles.settingsContainer}>
            <h2 className={styles.title}>설정</h2>
            <div className={styles.profileImage}>
                <img
                    src="https://koreansandwich-ecommerce-item-image.s3-ap-southeast-2.amazonaws.com/cute-profile-image.png"
                    alt="Profile"
                />
                <label htmlFor="profile-upload" className={styles.editProfileButton}>
                    Edit
                </label>
                <input
                    type="file"
                    id="profile-upload"
                    accept="image/*"
                    onChange={handleProfileImageUpload}
                    style={{ display: "none" }}
                />
            </div>
            <div className={styles.profileActions}>
                <p><strong>이메일:</strong> {userData.email}</p>
                <p><strong>이름:</strong> {userData.name}</p>
                <p><strong>생년월일:</strong> {userData.birthDate}</p>
                <p><strong>성별:</strong> {userData.gender}</p>
            </div>
            <div className={styles.actionsButtons}>
                <button onClick={handleOpenEditAccount} className={styles.editAccountButton}>회원정보 수정</button>
                <button onClick={handleOpenChangePassword} className={styles.changePasswordButton}>비밀번호 수정</button>
            </div>
            <div className={styles.resetHistory}>
                <button
                    className={styles.resetHistoryButton}
                    onClick={() => setShowResetModal(true)}
                >
                    기록 초기화
                </button>
            </div>

            {isEditModalOpen && (
                <div className={styles.modal}>
                    <div className={styles.modalContent}>
                        <h3>회원정보 수정</h3>
                        <label>
                            이름&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <input
                                type="text"
                                value={newName}
                                onChange={(e) => setNewName(e.target.value)}
                            />
                        </label>
                        <label>
                            생년월일
                            <input
                                type="date"
                                value={newBirthDate}
                                onChange={(e) => setNewBirthDate(e.target.value)}
                            />
                        </label>
                        <label>성별</label>
                        <div className={styles.genderButtons}>
                            <button
                                className={`${styles.genderButton} ${newGender === "남성" ? styles.selected : ""}`}
                                onClick={() => setNewGender("남성")}
                            >
                                남성
                            </button>
                            <button
                                className={`${styles.genderButton} ${newGender === "여성" ? styles.selected : ""}`}
                                onClick={() => setNewGender("여성")}
                            >
                                여성
                            </button>
                        </div>
                        <div className={styles.modalButtons}>
                            <button onClick={handleSaveEditAccount} className={styles.saveButton}>저장</button>
                            <button onClick={handleCloseEditAccount} className={styles.cancelButton}>취소</button>
                        </div>
                    </div>
                </div>
            )}

            {isChangePasswordModalOpen && (
                <div className={styles.modal}>
                    <div className={styles.modalContent}>
                        <h3>비밀번호 변경</h3>
                        <label>
                            현재 비밀번호&nbsp;&nbsp;&nbsp;&nbsp;
                            <input
                                type="password"
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                            />
                        </label>
                        <label>
                            새 비밀번호&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <input
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                            />
                        </label>
                        <label>
                            새 비밀번호 확인
                            <input
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                            />
                        </label>
                        <div className={styles.modalButtons}>
                            <button onClick={handleChangePassword} className={styles.saveButton}>저장</button>
                            <button onClick={handleCloseChangePassword} className={styles.cancelButton}>취소</button>
                        </div>
                    </div>
                </div>
            )}

            {showResetModal && (
                <div className={styles.modal}>
                    <div className={styles.modalContent}>
                        <h3>정말 기록을 초기화하시겠습니까?</h3>
                        <p>챗봇 내역과 제품 내역이 모두 삭제됩니다.</p>
                        <div className={styles.modalButtons}>
                            <button className={styles.confirmButton} onClick={handleResetHistory}>확인</button>
                            <button className={styles.cancelButton} onClick={() => setShowResetModal(false)}>취소</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Settings;
