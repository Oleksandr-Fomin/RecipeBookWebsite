import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { registerUser } from '../api/authAPI';

const Register = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError('');
        setSuccess('');
        try {
            await registerUser(username, password);
            setSuccess('Registration successful!');
        } catch (error) {
            if (error.response) {
                setError('Registration failed: ' + error.response.data);
            } else {
                setError('Registration failed: ' + error.message);
            }
        }
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-4"> 
                    <div className="card p-4 mt-5"> 
                        <h3 className="text-center mb-4">Create an Account</h3> 
                        {error && <div className="alert alert-danger">{error}</div>}
                        {success && <div className="alert alert-success">{success}</div>}
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
                            <button type="submit" className="btn btn-primary w-100">Register</button>
                        </form>
                        <div className="mt-4 text-center">
                            Already have an account? 
                            <Link to="/login"> Log In</Link> 
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Register;