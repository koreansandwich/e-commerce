import React, {useState} from "react";
import axios from "axios";

const ChatbotInterface = () => {
    const [chatHistory, setChatHistory] = useState([]);
    const [message, setMessage] = useState("");

    const handleViewHistory = () => {
        const token = localStorage.getItem("token");
        axios.get("http://localhost:8080/api/chat/history?days=7", {
            headers: {Authorization: `Bearer ${token}`}
        })
            .then((response) => {
                setChatHistory(response.data);
            })
            .catch((error) => {
                console.error("Failed to get chat history:", error);
            });
        };

    const handelSendMessage = () => {
        const token = localStorage.getItem("token");
        const newMessage = { text: message, sender: "user"};

        setChatHistory([...chatHistory, newMessage]);

        axios.post("http://localhost:8080/api/chat/send", newMessage, {
            headers: {Authorization: `Bearer ${token}`}
        })
            .then((response) => {
                setChatHistory([...chatHistory, newMessage], {text: response.data, sender: "bot"});
                setMessage("");
            })
            .catch((error) => {
                console.error("Failed to send Message:", error);
            });
    };


    return (
        <div className="chat-bot-interface">
            <h1>Welcome to the E-Commerce Chatbot</h1>
            <div className="chat-area">
                {chatHistory.length > 0 ? (
                    chatHistory.map((message, index) => (
                        <div key={index} className="chat-message ${message.sender}">
                            <strong>{message.sender === "user" ? "You" : "Bot"}:</strong>
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
                <button onClick={handelSendMessage} className="send-button">Send</button>
            </div>
            <button onClick={handleViewHistory} className="view-history-button">
                View Chat History (Last 7 Days)
            </button>
        </div>
    );
};

export default ChatbotInterface;