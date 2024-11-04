import React from "react";
import { Formik, Form, Field, ErrorMessage } from 'formik';
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
                axios.post('http://localhost:8080/api/auth/login', values)
                    .then((response) => {
                        console.log(response.data);
                        setSubmitting(false);

                        if (response.status === 200) {
                            const token = response.data;
                            localStorage.setItem('token', token);// 응답 상태 코드와 데이터 확인
                            console.log("Login successful, token stored:", token);
                            window.location.href = '/dashboard';
                        } else {
                            console.error("Login failed");
                            window.location.href = '/register';
                        }

                    })
                    .catch((error) => {
                        console.log(error);
                        setSubmitting(false);
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
