import React from "react";
import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import axios from 'axios';
import './LoginForm.css';

const LoginSchema = Yup.object().shape({
    email: Yup.string().email('Invalid email address').required('Email is required'),
    password: Yup.string().min(8, 'Too Short!').required('Password is required'),
});

const LoginForm = () => (
    <div className="login-form-container">
        <h1>로그인</h1>
        <Formik
            initialValues={{ email: '', password: '' }}
            validationSchema={LoginSchema}
            onSubmit={(values, { setSubmitting }) => {
                axios.post("http://localhost:8080/api/auth/login", values)
                    .then((response) => {
                        if (response.status === 200) { // 성공적인 로그인 응답 처리
                            const token = response.data;
                            localStorage.setItem("token", token);
                            console.log("Login successful, token stored:", token);
                            window.location.href = "/dashboard"; // 대시보드로 이동
                        }
                    })
                    .catch((error) => {
                        if (error.response && error.response.status === 401) {
                            alert("로그인 실패: 이메일 또는 비밀번호가 잘못되었습니다.");
                        } else {
                            alert("로그인 실패: 이메일 또는 비밀번호가 잘못되었습니다.");
                        }
                        console.error("Login failed:", error);
                    })
                    .finally(() => {
                        setSubmitting(false); // 로딩 상태 해제
                    });
            }}
        >
            {({ isSubmitting }) => (
                <Form>
                    <div className="form-field">
                        <Field type="email" name="email" placeholder="이메일" />
                    </div>
                    <div className="form-field">
                        <Field type="password" name="password" placeholder="비밀번호" />
                    </div>
                    <button type="submit" disabled={isSubmitting}>
                        로그인
                    </button>
                </Form>
            )}
        </Formik>
        <div className="form-footer">
            <a href="/register">회원가입 하기</a>
        </div>
    </div>
);

export default LoginForm;
