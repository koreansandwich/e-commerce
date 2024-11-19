import React, { useState } from "react";
import axios from "axios";
import "./ChangePassword.css";

const ChangePassword = () => {
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const handleChangePassword = () => {
        if (newPassword !== confirmPassword) {
            alert("New passwords do not match");
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
                alert("Password changed successfully");
            })
            .catch((error) => {
                console.error("Error changing password:", error);
                alert("Failed to change password. Please try again.");
            });
    };

    return (
        <div className="change-password-container">
            <h2>Change Password</h2>
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
            <button onClick={handleChangePassword}>Change Password</button>
        </div>
    );
};

export default ChangePassword;
