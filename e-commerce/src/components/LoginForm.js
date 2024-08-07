import React from "react";
import {Formik, Form, Field, ErrorMessage} from 'formik';
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
            axios.post('http://localhost:5000/login', values)
                .then((response) => {
                    console.log(response.data);
                    setSubmitting(false);
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
                    <Field type = "email" name = "email" placeholder="Email" />
                </div>
                <div className="form-field">
                    <Field type = "password" name = "password" placeholder="Password" />
                </div>
                <button type="submit" disabled={isSubmitting}>
                    Login</button>
            </Form>
        )}
    </Formik>
        <div className="form-footer">
            <a href="/signup">회원가입 하기</a>
        </div>
    </div>
);

export default LoginForm;