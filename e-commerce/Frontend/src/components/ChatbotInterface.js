import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import styles from "./ChatbotInterface.module.css"; // 모듈화된 CSS import

const ChatbotInterface = () => {
    const [chatHistory, setChatHistory] = useState([]);
    const [message, setMessage] = useState("");
    const chatAreaRef = useRef(null);

    useEffect(() => {
        const token = localStorage.getItem("token");
        axios
            .get("http://localhost:8080/api/chat/history", {
                headers: {Authorization: `Bearer ${token}`},
            })
            .then((response) => {
                setChatHistory(response.data);
            })
            .catch((error) => {
                console.error("Failed to load chat history:", error);
            });
    }, []);

    useEffect(() => {
        if (chatAreaRef.current) {
            chatAreaRef.current.scrollTop = chatAreaRef.current.scrollHeight;
        }
    }, [chatHistory]);

    const handleSendMessage = () => {
        const token = localStorage.getItem("token");
        const newMessage = {text: message, sender: "user"};

        if (!message.trim()) {
            console.error("Cannot send an empty message");
            return;
        }

        setChatHistory([...chatHistory, newMessage]);

        axios
            .post("http://localhost:8080/api/chat/send", newMessage, {
                headers: {Authorization: `Bearer ${token}`},
            })
            .then(() => {
                return axios.post("http://localhost:8080/api/chat/bot-response", newMessage, {
                    headers: {Authorization: `Bearer ${token}`},
                });
            })
            .then((response) => {
                setChatHistory((prevHistory) => [
                    ...prevHistory,
                    {text: response.data.message, sender: "bot"},
                ]);
            })
            .catch((error) => {
                console.error("Failed to send message:", error);
            });

        setMessage("");
    };

    const renderBotMessage = (message) => {
        try {
            const data = JSON.parse(message.text);

            return (
                <div className={styles.chatMessageBot}>
                    {data.map((item, index) => (
                        <div key={index} className={styles.botMessageCard}>
                            <p>안녕하세요! 적절한 제품을 추천해드릴게요 :)</p>
                            <img
                                src={item.item_image_url}
                                alt={item.item_name}
                                className={styles.productImage}
                            />
                            <h3>{item.item_name}</h3>
                            <p>
                                <strong>가격:</strong> {item.item_final_price}원
                            </p>
                            <p>
                                <strong>브랜드:</strong> {item.brand}
                            </p>
                            <p>
                                <strong>링크:{" "}</strong>
                                <a
                                    href={data.item_link}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className={styles.link}
                                >
                                    [클릭하세요]
                                </a>
                            </p>
                        </div>
                    ))}
                </div>
            );
        } catch (e) {
            return (
                <div>
                    {message.text.split("\n").map((line, index) => (
                        <p key={index}>{line}</p>
                    ))}
                </div>
            );
        }
    };

    return (
        <div className={styles.container}>
            {/* 좌측 설명 섹션 */}
            <div className={styles.infoSection}>
                <img
                    src="https://koreansandwich-ecommerce-item-image.s3.amazonaws.com/ecommerce-frontend/bulp.png"
                    alt="Guide Icon"
                    className={styles.infoIcon}
                />
                <h2 className={styles.infoTitle}>챗봇에 대해 알려드릴게요!</h2>
                <p className={styles.infoText}>
                    <strong>1. 키워드 추천:</strong> 제품의 특징 + 카테고리를 기반으로 말씀해주세요! <br/>
                    <em>ex: 수분 많고 유분 적은 로션을 추천해주세요</em>
                </p>
                <p className={styles.infoText}>
                    <strong>2. 유사 추천:</strong> 제품명 + 카테고리를 기반으로 말씀해주세요! <br/>
                    <em>ex: 'EANVIE 엔비 솔루션 수딩 로션 120ml, 120ml, 2개'랑 유사한 스킨을 추천해주세요</em>
                </p>
            </div>

            {/* 챗봇 컨테이너 */}
            <div className={styles.chatbotContainer}>
                <div className={styles.chatArea} ref={chatAreaRef}>
                    {chatHistory.length > 0 ? (
                        chatHistory.map((message, index) => (
                            <div
                                key={index}
                                className={`${styles.chatMessage} ${
                                    message.sender === "user"
                                        ? styles.chatMessageUser
                                        : styles.chatMessageBot
                                }`}
                            >
                                {message.sender === "bot"
                                    ? renderBotMessage(message)
                                    : <p>{message.text}</p>}
                            </div>
                        ))
                    ) : (
                        <p className={styles.emptyChat}>Your chat will appear here...</p>
                    )}
                </div>
                <div className={styles.inputArea}>
                    <input
                        type="text"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        placeholder="Type a message..."
                        className={styles.chatInput}
                    />
                    <button onClick={handleSendMessage} className={styles.sendButton}>
                        Send
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ChatbotInterface;
