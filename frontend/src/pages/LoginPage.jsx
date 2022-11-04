import React from 'react'
import '../styles/Sign/LogIn.scss'
import mainLogo from "../assets/mainicon.png";
import kakao from "../assets/kakao.png"

const LoginPage = () => {

    const client_id = process.env.REACT_APP_KAKAO_CLIENT_ID
    const redirect_uri = process.env.REACT_APP_KAKAO_REDIRECT_URI
    const KAKAO_AUTH_URL = 'https://kauth.kakao.com/oauth/authorize?client_id=45836166586d1409b29b026acd726439&redirect_uri=http://localhost:3000/oauth/callback/kakao&response_type=code'
    const Kakaologin = (response) => {
        window.location.href = KAKAO_AUTH_URL
    }


    return(
        <div>
            <div className='Login'>
            <div className='Login__Logo'>
                <img
                    className="Login__Logo__Main"
                    src={mainLogo}
                    alt="mainlogo"
                    width={"80%"}
                />
                </div>
                <div className='Login__Button' onClick={Kakaologin}><img className="Login__Button__Kakao" src={kakao} alt="카카오"></img>카카오 계정으로 계속하기</div>
            </div>
        </div>
    );
};

export default LoginPage;
