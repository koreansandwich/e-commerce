import React from 'react';
import './Dashboard.css';
import chatbotIcon from '../assets/chatbot.png';
import ecommerceIcon from '../assets/ecommerce.png';
import analysisIcon from '../assets/analysis.png';
import settingsIcon from '../assets/settings.png';

const Dashboard = () => (
    <div className="dashboard-container">
        <div className="card-container">
            <div className="card" onClick={() => window.location.href = '/chatbot'}>
                <img src={chatbotIcon} alt="Chatbot" className="card-icon" />
                <h2>챗봇</h2>
            </div>
            <div className="card" onClick={() => window.location.href = '/review-items'}>
                <img src={ecommerceIcon} alt="E-Commerce" className="card-icon" />
                <h2>구매 제품 리뷰</h2>
            </div>
            <div className="card" onClick={() => window.location.href = '/analytics'}>
                <img src={analysisIcon} alt="Analysis" className="card-icon" />
                <h2>구매동향 분석</h2>
            </div>
            <div className="card" onClick={() => window.location.href = '/settings'}>
                <img src={settingsIcon} alt="Settings" className="card-icon" />
                <h2>설정</h2>
            </div>
        </div>
    </div>
);

export default Dashboard;