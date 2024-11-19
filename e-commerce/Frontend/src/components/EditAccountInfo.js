import React, { useState, useEffect } from "react";
import axios from "axios";
import "./EditAccountInfo.css";

const EditAccountInfo = () => {
    const [newName, setNewName] = useState("");
    const [newBirthDate, setNewBirthDate] = useState("");
    const [newGender, setNewGender] = useState(""); // Gender를 관리하는 상태
    const [userId, setUserId] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            axios
                .get("http://localhost:8080/api/settings/account", {
                    headers: { Authorization: `Bearer ${token}` },
                })
                .then((response) => {
                    const { id, name, birthDate, gender } = response.data;
                    setUserId(id);
                    setNewName(name);
                    setNewBirthDate(birthDate);
                    setNewGender(gender); // 초기 Gender 설정
                })
                .catch((error) => {
                    console.error("Failed to fetch user data:", error);
                });
        } else {
            alert("Please log in again.");
            window.location.href = "/login";
        }
    }, []);

    const handleSave = () => {
        if (!userId) {
            console.error("User ID is not set.");
            return;
        }

        const token = localStorage.getItem("token");
        axios
            .put(
                `http://localhost:8080/api/settings/account/${userId}`,
                {
                    name: newName,
                    birthDate: newBirthDate,
                    gender: newGender,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            )
            .then(() => {
                alert("Account updated successfully!");
            })
            .catch((error) => {
                console.error("Failed to update account info:", error);
                alert("Failed to update account info. Please try again.");
            });
    };

    return (
        <div className="edit-account-container">
            <h2>Edit Account Info</h2>
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
                    className={`gender-button ${
                        newGender === "남성" ? "selected" : ""
                    }`}
                    onClick={() => setNewGender("남성")}
                >
                    남성
                </button>
                <button
                    className={`gender-button ${
                        newGender === "여성" ? "selected" : ""
                    }`}
                    onClick={() => setNewGender("여성")}
                >
                    여성
                </button>
            </div>
            <button className="save-button" onClick={handleSave}>
                Save
            </button>
        </div>
    );
};

export default EditAccountInfo;
