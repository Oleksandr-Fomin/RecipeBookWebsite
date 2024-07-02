import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const login = async (username, password) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/login`, { username, password });
    return response.data; 
  } catch (error) {
    throw error;
  }
};

export const registerUser = async (username, password) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/register`, {
        username, 
        password,
      });
      
      return response.data; 
    } catch (error) {
      throw error;
    }
  };