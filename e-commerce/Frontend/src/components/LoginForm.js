import React from "react";
import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import axios from 'axios';
import styles from './LoginForm.module.css'; // 모듈화된 CSS import

const LoginSchema = Yup.object().shape({
    email: Yup.string().email('Invalid email address').required('Email is required'),
    password: Yup.string().min(8, 'Too Short!').required('Password is required'),
});

const LoginForm = () => (
    <div className={styles.container}>
        <h1 className={styles.title}>로그인</h1>
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
                            window.location.href = "/mainboard"; // 대시보드로 이동
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
                    <div className={styles.field}>
                        <Field type="email" name="email" placeholder="이메일" className={styles.input} />
                    </div>
                    <div className={styles.field}>
                        <Field type="password" name="password" placeholder="비밀번호" className={styles.input} />
                    </div>
                    <button type="submit" className={styles.button} disabled={isSubmitting}>
                        로그인
                    </button>
                </Form>
            )}
        </Formik>
        <div className={styles.footer}>
            <a href="/register" className={styles.link}>회원가입 하기</a>
        </div>
    </div>
);

export default LoginForm;
