import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage.jsx";
import SignUpPage from "./pages/SignUpPage.jsx";
import KakaoLogin from "./components/Login/KakaoLogin.jsx";
import MainPage from "./pages/MainPage.jsx";
import MyPage from "./pages/MyPage.jsx";
import UserFontInfo from "./components/Mypage/UserFontInfo"
import EditUserInfo from "./components/Mypage/EditUserInfo"
import "../src/styles/Common/App.scss";


function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/oauth/callback/kakao" element={<KakaoLogin />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="" element={<MainPage />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/userinfo" element={<EditUserInfo />} />
          <Route path="/fontinfo/:id" element={<UserFontInfo />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
