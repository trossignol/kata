import React, { useEffect, useState } from 'react';
import "./ProgressBar.css";

interface ProgressBarProps {
    duration: number;
    progress: number;
}

const ProgressBar: React.FC<ProgressBarProps> = ({ progress, duration }) => {
    const [stateProgress, setProgress] = useState(progress);

    useEffect(() => {
        console.log(progress, stateProgress);
        let startTime = Date.now();

        const updateProgress = () => {
            const currentTime = Date.now();
            const elapsedTime = currentTime - startTime;
            const currentProgress = (elapsedTime / duration) * 100;

            if (currentProgress >= 100) {
                setProgress(100);
            } else {
                setProgress(currentProgress);
                requestAnimationFrame(updateProgress);
            }
        };

        const animationFrameId = requestAnimationFrame(updateProgress);

        return () => {
            // Nettoyer l'animationFrame lors du démontage du composant
            cancelAnimationFrame(animationFrameId);
        };
    }, [duration]);

    return (
        <div className="progress-bar">
            <div
                className="progress-bar-fill"
                style={{ width: `${stateProgress}%` }}
            ></div>
        </div>
    );
};

export default ProgressBar;
