
import React, { useContext } from 'react';
import { NotificationContext } from '../NotificationContext';

export default function NotificationList() {
  const { notifications } = useContext(NotificationContext);

  return (
    <div className="notifications">
      {notifications.map((notification, index) => (
        <div key={index} className="notification">
          {notification.message}
        </div>
      ))}
    </div>
  );

}
