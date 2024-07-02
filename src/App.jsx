import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import Login from './pages/LogIn';
import Register from './pages/Register';
import Profile from './pages/Profile';
import ProtectedRoute from './components/Routes/ProtectedRoute';
import Administration from './pages/Administration';
import RecipePage from './pages/RecipePage';
import { LikesProvider } from './components/LikesContext';
import OneRecipePage from './pages/OneRecipePage';
import NotificationList from './components/NotificationList';
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';
import { jwtDecode } from 'jwt-decode';
import Notification from './components/Notification';





function App() {
  const [notification, setNotification] = useState('');

  useEffect(() => {
    const getCurrentUserId = () => {
      const token = localStorage.getItem('jwtToken');
      if (token) {
        try {
          const decodedToken = jwtDecode(token);
          return decodedToken.userID;
        } catch (error) {
          console.error('Error decoding token:', error);
          return null;
        }
      }
      return null;
    };

    const userId = getCurrentUserId();

    if (userId) {
      const socket = new SockJS('http://localhost:8080/ws');
      const stompClient = Stomp.over(socket);
      const token = localStorage.getItem('jwtToken');

      stompClient.connect({'Authorization': 'Bearer ' + token}, () => {
        stompClient.subscribe(`/topic/notifications/${userId}`, (msg) => {
          const notificationMsg = JSON.parse(msg.body).content; 
          setNotification(notificationMsg);
        });
      });

      return () => {
        if (stompClient.connected) {
          stompClient.disconnect();
        }
      };
    }
  }, []);

  const handleCloseNotification = () => {
    setNotification('');
  };

  return (
    <LikesProvider>
    <Router>
      <div className="App">
        <Header />
        {notification && (
            <Notification message={notification} onClose={handleCloseNotification} />
          )}
        <div className="App-content">
          <Routes>
            <Route path="/register" element={<Register />} /> 
            <Route path="/login" element={<Login />} /> 
            <Route path="/profile" element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            } />
            <Route path="/recipes" element={
              <ProtectedRoute>
                <RecipePage />
              </ProtectedRoute>
            } />
            <Route path="/admin" element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <Administration />
              </ProtectedRoute>
            } />
            <Route path="/recipe/:id" element={
              <ProtectedRoute>
                <OneRecipePage />
              </ProtectedRoute>
            } />
          </Routes>
        </div>
      </div>
    </Router>
    </LikesProvider>
  );
}

export default App;