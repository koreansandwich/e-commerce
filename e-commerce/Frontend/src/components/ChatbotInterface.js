import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import "./ChatbotInterface.css";

const ChatbotInterface = () => {
    const [chatHistory, setChatHistory] = useState([]); // 채팅 기록 상태
    const [message, setMessage] = useState("");
    const chatAreaRef = useRef(null); // 스크롤 제어를 위한 Ref

    // 히스토리 로드
    useEffect(() => {
        const token = localStorage.getItem("token");
        axios
            .get("http://localhost:8080/api/chat/history", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => {
                setChatHistory(response.data);
            })
            .catch((error) => {
                console.error("Failed to load chat history:", error);
            });
    }, []);

    // 새로운 메시지가 추가될 때 자동 스크롤
    useEffect(() => {
        if (chatAreaRef.current) {
            chatAreaRef.current.scrollTop = chatAreaRef.current.scrollHeight;
        }
    }, [chatHistory]);

    // 메시지 전송
    const handleSendMessage = () => {
        const token = localStorage.getItem("token");
        const newMessage = { text: message, sender: "user" };

        if (!message.trim()) {
            console.error("Cannot send an empty message");
            return;
        }

        setChatHistory([...chatHistory, newMessage]); // 사용자 메시지 추가

        axios
            .post("http://localhost:8080/api/chat/send", newMessage, {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then(() => {
                return axios.post("http://localhost:8080/api/chat/bot-response", newMessage, {
                    headers: { Authorization: `Bearer ${token}` },
                });
            })
            .then((response) => {
                // 봇 응답 추가
                setChatHistory((prevHistory) => [
                    ...prevHistory,
                    { text: response.data.message, sender: "bot" },
                ]);
            })
            .catch((error) => {
                console.error("Failed to send message:", error);
            });

        setMessage(""); // 입력 초기화
    };

    // 봇 메시지 포맷팅
    const renderBotMessage = (message) => {
        try {
            // 메시지가 JSON 포맷일 경우 파싱
            const data = JSON.parse(message.text);

            return (
                <div className="bot-message-card">
                    {data.map((item, index) => (
                        <div key={index}>
                            <p>안녕하세요! 적절한 제품을 추천해드릴게요 :)</p>
                            <img src={item.item_image_url} alt={item.item_name} className="product-image"/>
                            <h3>{item.item_name}</h3>
                            <p><strong>가격:</strong> {item.item_final_price}원</p>
                            <p><strong>브랜드:</strong> {item.brand}</p>
                            <p>
                                <strong>링크:{" "}</strong>
                                <a href={data.item_link} target="_blank" rel="noopener noreferrer" className="link">
                                    [클릭하세요]
                                </a>
                            </p>
                        </div>
                    ))}
                </div>
            );
        } catch (e) {
            // 텍스트 메시지일 경우 줄바꿈 처리 추가
            return (
                <div>
                    {message.text.split("\n").map((line, index) => (
                        <p key={index}>{line}</p> // 줄바꿈 처리
                    ))}
                </div>
            );
        }
    };

    return (
        <div className="chatbot-container">
            <div className="chat-area" ref={chatAreaRef}>
                {chatHistory.length > 0 ? (
                    chatHistory.map((message, index) => (
                        <div
                            key={index}
                            className={`chat-message ${message.sender}`}
                        >
                            {message.sender === "bot"
                                ? renderBotMessage(message)
                                : <p>{message.text}</p>}
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
