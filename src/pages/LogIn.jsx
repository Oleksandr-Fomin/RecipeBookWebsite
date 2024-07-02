import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom'; 
import { jwtDecode } from 'jwt-decode';
import { login } from '../api/authAPI';


const LogIn = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            const token = await login(username, password);
            localStorage.setItem('jwtToken', token);
            
            const decodedToken = jwtDecode(token);
            localStorage.setItem('userId', decodedToken.userID);
            const roles = decodedToken.roles; 
    
            if (roles && roles.includes('ADMIN')) {
                navigate('/admin'); 
            } else {    
                navigate('/profile');
            }
        } catch (error) {
            if (error.response) {
                setError('Login failed: ' + error.response.data);
            } else {
                setError('Login failed: ' + error.message);
            }
        }
    };
    

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-4"> 
                    <div className="card p-4 mt-5">
                        <h3 className="text-center mb-4">Welcome Back!</h3> 
                        {error && <div className="alert alert-danger">{error}</div>}
                        <form onSubmit={handleSubmit}>
                            <div className="form-group mb-3">
                                <label htmlFor="username">Username</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="username"
                                    value={username}
                                    onChange={e => setUsername(e.target.value)}
                                    placeholder="Enter username"
                                />
                            </div>
                            <div className="form-group mb-3">
                                <label htmlFor="password">Password</label>
                                <input
                                    type="password"
                                    className="form-control"
                                    id="password"
                                    value={password}
                                    onChange={e => setPassword(e.target.value)}
                                    placeholder="Enter password"
                                />
                            </div>
                            <button type="submit" className="btn btn-primary w-100">Login</button>
                        </form>
                        <div className="mt-4 text-center">
                            Don't have an account?
                            <Link to="/register"> Sign Up</Link> 
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default LogIn;