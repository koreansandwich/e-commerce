import React from 'react';
import styles from './MainBoard.module.css'; // 모듈화된 CSS import

// S3 버킷의 이미지 링크
const S3_BASE_URL = "https://koreansandwich-ecommerce-item-image.s3.amazonaws.com/ecommerce-frontend";

const MainBoard = () => (
    <div className={styles.dashboardContainer}>
        <div className={styles.cardContainer}>
            <div className={styles.card} onClick={() => window.location.href = '/chatbot'}>
                <img src={`${S3_BASE_URL}/chatbot.png`} alt="Chatbot" className={styles.cardIcon} />
                <h2 className={styles.cardTitle}>챗봇</h2>
            </div>
            <div className={styles.card} onClick={() => window.location.href = '/review-items'}>
                <img src={`${S3_BASE_URL}/ecommerce.png`} alt="E-Commerce" className={styles.cardIcon} />
                <h2 className={styles.cardTitle}>구매 제품 리뷰</h2>
            </div>
            <div className={styles.card} onClick={() => window.location.href = '/dashboard'}>
                <img src={`${S3_BASE_URL}/analysis.png`} alt="Analysis" className={styles.cardIcon} />
                <h2 className={styles.cardTitle}>구매동향 분석</h2>
            </div>
            <div className={styles.card} onClick={() => window.location.href = '/settings'}>
                <img src={`${S3_BASE_URL}/settings.png`} alt="Settings" className={styles.cardIcon} />
                <h2 className={styles.cardTitle}>설정</h2>
            </div>
        </div>
    </div>
);

export default MainBoard;
