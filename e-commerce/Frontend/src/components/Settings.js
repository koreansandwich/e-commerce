import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Settings.css';

const Settings = () => {
    const [userId] = useState(1); // 임시로 설정된 사용자 ID
    const [chatHistory, setChatHistory] = useState([]);
    const [newName, setNewName] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [newAge, setNewAge] = useState('');
    const [newGender, setNewGender] = useState('');

    useEffect(() => {

        // 사용자 챗봇 대화 내역 가져오기
        axios.get(`/api/settings/chat-history/${userId}`)
            .then(response => {
                setChatHistory(response.data);
            })
            .catch(error => console.error('Error fetching chat history:', error));
    }, [userId]);

    const handleDeleteChatHistory = () => {
        axios.delete(`/api/settings/chat-history/${userId}`)
            .then(() => {
                alert('Chat history deleted successfully');
                setChatHistory([]); // 대화 내역 비우기
            })
            .catch(error => console.error('Error deleting chat history:', error));
    };

    const handleUpdateAccount = () => {
        if (newPassword !== confirmPassword) {
            alert('Passwords do not match');
            return;
        }

        axios.put(`/api/settings/account/${userId}`, null, {
            params: {
                newName,
                newPassword,
                confirmPassword,
                newAge,
                newGender
            }
        })
            .then(() => alert('Account updated successfully'))
            .catch(error => console.error('Error updating account:', error));
    };

    return (
        <div className="settings-container">
            <h2>Settings</h2>
            <div className="section">
                <h3>Chat History</h3>
                <button onClick={handleDeleteChatHistory}>Delete Chat History</button>
                <ul>
                    {chatHistory.map((message, index) => (
                        <li key={index}>{message.sender}: {message.message}</li>
                    ))}
                </ul>
            </div>
            <div className="section">
                <h3>Update Account</h3>
                <input
                    type="text"
                    placeholder="New Name"
                    value={newName}
                    onChange={e => setNewName(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="New Password"
                    value={newPassword}
                    onChange={e => setNewPassword(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Confirm Password"
                    value={confirmPassword}
                    onChange={e => setConfirmPassword(e.target.value)}
                />
                <input
                    type="number"
                    placeholder="New Age"
                    value={newAge}
                    onChange={e => setNewAge(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="New Gender"
                    value={newGender}
                    onChange={e => setNewGender(e.target.value)}
                />
                <button onClick={handleUpdateAccount}>Update Account</button>
            </div>
        </div>
    );
};

export default Settings;
