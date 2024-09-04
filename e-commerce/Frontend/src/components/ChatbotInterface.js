import React, { useState } from "react";
import axios from "axios";
import "./ChatbotInterface.css";

const ChatbotInterface = () => {
    const [chatHistory, setChatHistory] = useState([]);
    const [message, setMessage] = useState("");

    // 채팅 기록 불러오기
    const handleViewHistory = () => {
        const token = localStorage.getItem("token");
        axios
            .get("http://localhost:8080/api/chat/history?days=7", {
                headers: { Authorization: `Bearer ${token}` } // 템플릿 리터럴 수정
            })
            .then((response) => {
                setChatHistory(response.data); // 서버에서 받은 대화 기록을 설정
            })
            .catch((error) => {
                console.error("Failed to get chat history:", error);
            });
    };

    // 메시지 전송
    const handleSendMessage = () => {
        const token = localStorage.getItem("token");
        const newMessage = { text: message, sender: "user" };

        setChatHistory([...chatHistory, newMessage]); // 새로운 메시지를 대화 기록에 추가

        axios
            .post("http://localhost:8080/api/chat/send", newMessage, {
                headers: { Authorization: `Bearer ${token}` }, // 템플릿 리터럴 수정
            })
            .then((response) => {
                setChatHistory((prevHistory) => [
                    ...prevHistory,
                    { text: response.data, sender: "bot" }, // 봇의 응답을 대화 기록에 추가
                ]);
                setMessage(""); // 입력 필드 초기화
            })
            .catch((error) => {
                console.error("Failed to send message:", error);
            });
    };

    return (
        <div className="chatbot-interface">
            <header className="chatbot-header">
                <h1>Design Thinking</h1>
            </header>
            <div className="chat-area">
                {chatHistory.length > 0 ? (
                    chatHistory.map((message, index) => (
                        <div
                            key={index}
                            className={`chat-message ${message.sender}`} // 템플릿 리터럴 수정
                        >
                            <p>{message.text}</p>
                        </div>
                    ))
                ) : (
                    <p>Your chat will appear here...</p>
                )}
            </div>
            <div className="input-area">
                <input
                    type="text"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="Type message..."
                    className="chat-input"
                />
                <button onClick={handleSendMessage} className="send-button">
                    Send
                </button>
            </div>
            <button
                onClick={handleViewHistory}
                className="view-history-button"
            >
                View Chat History (Last 7 Days)
            </button>
        </div>
    );
};

export default ChatbotInterface;
