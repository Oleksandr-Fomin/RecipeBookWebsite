import React, { createContext, useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';

export const NotificationContext = createContext();

export const NotificationProvider = ({ children }) => {
  const [notifications, setNotifications] = useState([]);
  
  useEffect(() => {
    const token = localStorage.getItem('jwtToken'); // or your method of retrieving the auth token
    const stompClient = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      connectHeaders: {
        Authorization: `Bearer ${token}`, // Include the token if required
      },
      onConnect: () => {
        console.log('Connected to WS');
        stompClient.subscribe('/user/queue/notifications', (message) => {
          const notification = JSON.parse(message.body);
          setNotifications((prev) => [...prev, notification]);
          // You can also use a state management library or context to distribute notifications
        });
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      },
      // ...other configurations...
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
      console.log('Disconnected from WS');
    };
  }, []);

  return (
    <NotificationContext.Provider value={{ notifications, setNotifications }}>
      {children}
    </NotificationContext.Provider>
  );
};
