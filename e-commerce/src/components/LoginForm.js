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
    <div>
    <h1>Login</h1>
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
                <Field type = "email" name = "email" placeholder="Email" />
                <Field type = "password" name = "password" placeholder="Password" />
                <button type="submit" disabled={isSubmitting}>
                    Login</button>
            </Form>
        )}
    </Formik>
    </div>
);

export default LoginForm;