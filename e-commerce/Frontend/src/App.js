import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Link, Route, Routes, useNavigate } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import './App.css';
import RegisterForm from "./components/RegisterForm";
import ChatbotInterface from "./components/ChatbotInterface";
import Dashboard from "./components/Dashboard";
import Settings from "./components/Settings";
import EditAccountInfo from './components/EditAccountInfo';
import ChangePassword from './components/ChangePassword';

function App() {
    return (
        <Router>
            <AppContent />
        </Router>
    );
}

function AppContent() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const navigate = useNavigate();

    // 로컬 스토리지에서 토큰을 확인하여 로그인 상태를 관리
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setIsLoggedIn(true);
        }
    }, []);

    // 로그아웃 처리 및 리다이렉트
    const handleLogout = () => {
        localStorage.removeItem("token");
        setIsLoggedIn(false);
        navigate("/login"); // 로그아웃 후 로그인 페이지로 이동
    };

    return (
        <div className="App">
            <header className="App-header">
                <nav className="navbar">
                    <div className="navbar-left">
                        <Link to="/" className="navbar-brand">E-Commerce</Link>
                    </div>
                    <div className="navbar-right">
                        {isLoggedIn ? (
                            <>
                                {/* 로그아웃 버튼 */}
                                <button className="nav-link button" onClick={handleLogout}>로그아웃</button>
                                {/* 설정 페이지로 이동하는 링크 */}
                                <Link to="/settings" className="nav-link button">설정</Link>
                            </>
                        ) : (
                            <>
                                {/* 로그인 및 회원가입 버튼 */}
                                <Link to="/login" className="nav-link button">로그인</Link>
                                <Link to="/register" className="nav-link button">회원가입</Link>
                            </>
                        )}
                    </div>
                </nav>

                {/* Routes 설정 */}
                <Routes>
                    <Route path="/login" element={<LoginForm />} />
                    <Route path="/register" element={<RegisterForm />} />
                    <Route path="/dashboard" element={<Dashboard />} /> {/* 대시보드 경로 추가 */}
                    <Route path="/chatbot" element={<ChatbotInterface />} />
                    <Route path="/settings" element={<Settings />} />
                    <Route path="/edit-account" element={<EditAccountInfo />} />
                    <Route path="/change-password" element={<ChangePassword />} />
                    <Route path="*" element={<React.Fragment><h1>404: Page Not Found</h1></React.Fragment>} />
                </Routes>
            </header>
        </div>
    );
}

export default App;
