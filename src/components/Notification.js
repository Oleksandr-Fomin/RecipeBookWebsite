import React from 'react';
import './Notification.css'; 

const Notification = ({ message, onClose }) => {
  if (!message) return null;

  return (
    <div className="notification">
      {message}
      <button onClick={onClose}>Close</button>
    </div>
  );
};

export default Notification;
