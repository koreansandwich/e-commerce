import React from 'react';
import {BrowserRouter as Router, Link, Route, Routes} from "react-router-dom";
import LoginForm from "./components/LoginForm";
import './App.css';
import RegisterForm from "./components/RegisterForm";

function App() {
  return (
      <Router>
        <div className="App">
          <header className="App-header">
              <nav className="navbar">
                  <div className="navbar-left">
                      <Link to="/" className="navbar-brand">E-Commerce</Link>
                  </div>
                  <div className="navbar-right">
                      <Link to="/login" className="nav-link button">Log in</Link>
                      <Link to="/register" className="nav-link button">Sign up</Link>
                  </div>
              </nav>
                <Routes>
                  <Route path="/login" element={<LoginForm/>} />
                    <Route path="/register" element={<RegisterForm/>} />
                  <Route path="/" element={<React.Fragment><h1>Welcome to React App</h1></React.Fragment>} />
                    <Route path="*" element={<React.Fragment><h1>404: Page Not Found</h1></React.Fragment>} />
                </Routes>
              </header>
            </div>
          </Router>
      );
    }

export default App;
