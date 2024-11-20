import React, { useState, useEffect } from "react";
import axios from "axios";
import "./ChatbotInterface.css";

const ChatbotInterface = () => {
    const [chatHistory, setChatHistory] = useState([]); // 채팅 기록 상태
    const [message, setMessage] = useState("");

    // 히스토리 로드
    useEffect(() => {
        const token = localStorage.getItem("token");
        axios
            .get("http://localhost:8080/api/chat/history", {
                headers: { Authorization: `Bearer ${token}` }, // 백틱 사용
            })
            .then((response) => {
                // 히스토리를 상태에 저장
                setChatHistory(response.data);
            })
            .catch((error) => {
                console.error("Failed to load chat history:", error);
            });
    }, []); // 빈 배열: 초기 렌더링 시 한 번 실행

    // 메시지 전송
    const handleSendMessage = () => {
        const token = localStorage.getItem("token");
        const newMessage = { text: message, sender: "user" };

        if (!message.trim()) {
            console.error("Cannot send an empty message");
            return;
        }

        // 화면에 사용자 메시지 추가
        setChatHistory([...chatHistory, newMessage]);

        axios
            .post("http://localhost:8080/api/chat/send", newMessage, {
                headers: { Authorization: `Bearer ${token}` }, // 백틱 사용
            })
            .then(() => {
                // 봇 응답 요청
                return axios.post("http://localhost:8080/api/chat/bot-response", newMessage, {
                    headers: { Authorization: `Bearer ${token}` }, // 백틱 사용
                });
            })
            .then((response) => {
                // 화면에 봇 응답 추가
                setChatHistory((prevHistory) => [
                    ...prevHistory,
                    { text: response.data.message, sender: "bot" },
                ]);
            })
            .catch((error) => {
                console.error("Failed to send message:", error);
            });
        setMessage(""); // 입력 필드 초기화
    };

    return (
        <div className="chatbot-container">
            <div className="chat-area">
                {chatHistory.length > 0 ? (
                    chatHistory.map((message, index) => (
                        <div
                            key={index}
                            className={`chat-message ${message.sender}`} // 백틱 사용
                        >
                            <p>{message.text}</p>
                        </div>
                    ))
                ) : (
                    <p className="empty-chat">Your chat will appear here...</p>
                )}
            </div>
            <div className="input-area">
                <input
                    type="text"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="Type a message..."
                    className="chat-input"
                />
                <button onClick={handleSendMessage} className="send-button">
                    Send
                </button>
            </div>
        </div>
    );
};

export default ChatbotInterface;
