// ProtectedRoute.js
import React from 'react';
import { Navigate } from 'react-router-dom';
import {jwtDecode} from 'jwt-decode'; 

const ProtectedRoute = ({ children, requiredRoles }) => {
    const jwtToken = localStorage.getItem('jwtToken');
    let isAuthenticated = false;
    let userRoles = [];

    if (jwtToken) {
        try {
            const decodedToken = jwtDecode(jwtToken);
            userRoles = decodedToken.roles || []; 
            isAuthenticated = true;
        } catch (error) {
            console.error('Invalid token', error);
            localStorage.removeItem('jwtToken');
        }
    }

   
    const hasRequiredRole = requiredRoles ? requiredRoles.some(role => userRoles.includes(role)) : true;

    if (!isAuthenticated || !hasRequiredRole) {
        return <Navigate to="/login" replace />;
    }

    return children;
};

export default ProtectedRoute;
