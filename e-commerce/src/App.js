import React from 'react';
import { BrowserRouter as Router, Route, Routes} from "react-router-dom";
import LoginForm from "./components/LoginForm";
import './App.css';

function App() {
  return (
      <Router>
        <div className="App">
          <header className="App-header">
            <Routes>
              <Route path="/login" element={<LoginForm/>} />
              <Route path="/" element={<React.Fragment><h1>Welcome to React App</h1></React.Fragment>} />
                <Route path="*" element={<React.Fragment><h1>404: Page Not Found</h1></React.Fragment>} />
            </Routes>
          </header>
        </div>
      </Router>
  );
}

export default App;
