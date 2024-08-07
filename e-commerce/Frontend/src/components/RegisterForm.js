import React from 'react';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import axios from 'axios';
import './RegisterForm.css';

const RegisterSchema = Yup.object().shape({
    email: Yup.string().email('Invalid email address').required('Email is required'),
    name: Yup.string().required('Name is required'),
    password: Yup.string()
        .min(8, 'Too Short!')
        .matches(/^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{8,}$/, 'Password must contain both letters and numbers')
        .required('Password is required'),
    confirmPassword: Yup.string()
        .oneOf([Yup.ref('password'), null], 'Passwords must match')
        .required('Confirm Password is required'),
});

const RegisterForm = () => {
    /*const [isEmailValid, setIsEmailValid] = useState(true);

    const checkEmail = async email => {
        try {
            const response = await axios.post('http://localhost:5000/register', email);
            setIsEmailValid(response.data.isUnique);
        } catch (error) {
            console.error(error);
            setIsEmailValid(false);
        }
    }; */
    return (
        <div className="register-form-container">
            <h1>Register</h1>
            <Formik
                initialValues={{email: '', password: '', confirmPassword: ''}}
                validationSchema={RegisterSchema}
                onSubmit={(values, {setSubmitting}) => {
                    const {confirmPassword, ...data} = values;
                    axios.post('http://localhost:5000/register', data)
                        .then((response) => {
                            console.log(response.data);
                            setSubmitting(false);
                            alert('회원가입이 완료되었습니다. 이메일을 확인해주세요.');
                        })
                        .catch((error) => {
                            console.error(error);
                            setSubmitting(false);
                            alert('회원가입에 실패했습니다.');
                        });
                }}
            >
                {({isSubmitting, values, handleChange}) => (
                    <Form>
                        <div className="form-field">
                            <Field
                                type="email"
                                name="email"
                                placeholder="Email"
                                onChange={handleChange}
                            />
                        </div>
                        <div className="form-field">
                            <Field type="text" name="name" placeholder="Name"/>
                            <ErrorMessage name="name" component="div" className="error-message"/>
                        </div>
                        <div className="form-field">
                            <Field type="password" name="password" placeholder="Password"/>
                            <ErrorMessage name="password" component="div" className="error-message"/>
                        </div>
                        <div className="form-field">
                            <Field type="password" name="confirmPassword" placeholder="Confirm Password"/>
                            <ErrorMessage name="confirmPassword" component="div" className="error-message"/>
                        </div>
                        <button type="submit"
                                disabled={isSubmitting || !values.email || !values.name || !values.password || !values.confirmPassword}>
                            Register
                        </button>
                    </Form>
                )}
            </Formik>
        </div>
    );
};

export default RegisterForm;