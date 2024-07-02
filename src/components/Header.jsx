import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Header.css';
import {jwtDecode} from 'jwt-decode';

function Header() {
    const navigate = useNavigate();
    
    const handleLogout = () => {
        localStorage.removeItem('jwtToken'); 
        navigate('/login'); 
    };
    const isAdmin = () => {
        const token = localStorage.getItem('jwtToken');
        if (token) {
          try {
            const decodedToken = jwtDecode(token);
            return decodedToken.roles && decodedToken.roles.includes('ADMIN');
          } catch (error) {
            console.error('Error decoding token:', error);
            return false;
          }
        }
        return false;
      };

    const isLoggedIn = !!localStorage.getItem('jwtToken');
    const isUserAdmin = isAdmin(); 

    return (
        <div className="header-nav">
            <div className="nav-left">
                <Link to="/profile">Profile</Link>
                <Link to="/recipes">Recipe Book</Link>
                {isUserAdmin && <Link to="/admin">Administration</Link>}
            </div>
            <div className="search-bar">
            </div>
            <div className="nav-right">
                {isLoggedIn ? (
                    <>
                        <a href="#logout" onClick={handleLogout} className="nav-link">Logout</a>
                    </>
                ) : (
                    <>
                        <Link to="/login" className="nav-link">Login</Link>
                        <Link to="/register" className="nav-link">Register</Link>
                    </>
                )}
            </div>
        </div>
    );
}

export default Header;
