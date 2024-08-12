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
        <h1>Log in to <span className="brand">E-Commerce</span> </h1>
        <Formik
            initialValues={{ email: '', password: '' }}
            validationSchema={LoginSchema}
            onSubmit={(values, { setSubmitting }) => {
                axios.post('http://localhost:8080/login', values)
                    .then((response) => {
                        console.log(response.data);
                        setSubmitting(false);

                        if (response.status === 200 && response.data.success) {
                            const token = `Bearer ${response.data.token}`;
                            localStorage.setItem('token', token);// 응답 상태 코드와 데이터 확인
                            console.log("Login successful, token stored:", token);
                            window.location.href = '/success';
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
                        <Field type="email" name="email" placeholder="Email" />
                        <ErrorMessage name="email" component="div" className="error-message" />
                    </div>
                    <div className="form-field">
                        <Field type="password" name="password" placeholder="Password" />
                        <ErrorMessage name="password" component="div" className="error-message" />
                    </div>
                    <button type="submit" disabled={isSubmitting}>
                        Login
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
