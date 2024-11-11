import React, { useState } from 'react';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import axios from 'axios';
import './RegisterForm.css';

const RegisterSchemaStep1 = Yup.object().shape({
    email: Yup.string().email('유효하지 않은 이메일 형식입니다.').required('이메일을 입력하세요'),
    name: Yup.string().required('이름을 입력하세요'),
    password: Yup.string()
        .min(8, '비밀번호는 8자리 이상이어야 합니다.')
        .matches(/^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{8,}$/, '비밀번호는 영문자와 숫자를 포함해야 합니다.')
        .required('비밀번호를 입력하세요'),
    confirmPassword: Yup.string()
        .oneOf([Yup.ref('password'), null], '패스워드가 일치하지 않습니다.')
        .required('비밀번호를 입력하세요'),
});

const RegisterSchemaStep2 = Yup.object().shape({
    birthDate: Yup.date().required('생년월일을 입력하세요').max(new Date(), '유효한 생년월일을 입력하세요'),
    gender: Yup.string().required('성별을 선택하세요'),
});

const RegisterForm = () => {
    const [step, setStep] = useState(1);

    return (
        <div className="register-form-container">
            {step === 1 ? (
                <Formik
                    initialValues={{ email: '', name: '', password: '', confirmPassword: '' }}
                    validationSchema={RegisterSchemaStep1}
                    onSubmit={(values, { setSubmitting }) => {
                        setStep(2);
                        setSubmitting(false);
                    }}
                >
                    {({ isSubmitting }) => (
                        <Form>
                            <div className="form-field">
                                <Field type="email" name="email" placeholder="이메일" />
                                <ErrorMessage name="email" component="div" className="error-message" />
                            </div>
                            <div className="form-field">
                                <Field type="text" name="name" placeholder="이름" />
                                <ErrorMessage name="name" component="div" className="error-message" />
                            </div>
                            <div className="form-field">
                                <Field type="password" name="password" placeholder="비밀번호" />
                                <ErrorMessage name="password" component="div" className="error-message" />
                            </div>
                            <div className="form-field">
                                <Field type="password" name="confirmPassword" placeholder="비밀번호 확인" />
                                <ErrorMessage name="confirmPassword" component="div" className="error-message" />
                            </div>
                            <button type="submit" disabled={isSubmitting}>다음</button>
                        </Form>
                    )}
                </Formik>
            ) : (
                <Formik
                    initialValues={{ birthDate: '', gender: '' }}
                    validationSchema={RegisterSchemaStep2}
                    onSubmit={(values, { setSubmitting }) => {
                        axios.post('http://localhost:8080/api/auth/register', values)
                            .then((response) => {
                                console.log(response.data);
                                alert('회원가입이 완료되었습니다.');
                                setSubmitting(false);
                            })
                            .catch((error) => {
                                console.error(error);
                                alert('회원가입에 실패했습니다.');
                                setSubmitting(false);
                            });
                    }}
                >
                    {({ isSubmitting, setFieldValue, values }) => (
                        <Form>
                            <div className="form-field">
                                <Field type="date" name="birthDate" placeholder="생년월일" />
                                <ErrorMessage name="birthDate" component="div" className="error-message" />
                            </div>
                            <div className="form-field gender-select">
                                <label
                                    className={`gender-button ${values.gender === '남성' ? 'selected' : ''}`}
                                    onClick={() => setFieldValue('gender', '남성')}
                                >
                                    남성
                                </label>
                                <label
                                    className={`gender-button ${values.gender === '여성' ? 'selected' : ''}`}
                                    onClick={() => setFieldValue('gender', '여성')}
                                >
                                    여성
                                </label>
                                <ErrorMessage name="gender" component="div" className="error-message" />
                            </div>
                            <button type="submit" disabled={isSubmitting}>회원가입</button>
                        </Form>
                    )}
                </Formik>
            )}
        </div>
    );
};

export default RegisterForm;
