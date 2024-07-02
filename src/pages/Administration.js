import React, { useState, useEffect } from 'react';
import { fetchUsers, deleteUser, getUserRecipeCount, getUserAverageRating } from '../api/adminAPI'; 
import  './Administration.css';

const Administration = () => {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    
    const loadUsers = async () => {
      try {
        const userData = await fetchUsers();
        const usersWithAdditionalData = await Promise.all(userData.map(async (user) => {
          const recipeCount = await getUserRecipeCount(user.id);
          const averageRating = await getUserAverageRating(user.id); 
          return {
            ...user,
            recipeCount,
            averageRating 
          };
        }));
        setUsers(usersWithAdditionalData);
      } catch (err) {
        setError('Failed to fetch users');
        console.error(err);
      }
    };

    loadUsers();
  }, []);

 const formatAverageRating = (rating) => {
  return typeof rating === 'number' ? rating.toFixed(1) : 'N/A';
};

  const handleDeleteUser = async (userId) => {
    try {
      await deleteUser(userId);
      setUsers(users.filter((user) => user.id !== userId));
    } catch (err) {
      setError('Failed to delete user');
      console.error(err);
    }
  };

 

  return (
    <div>
      <h1>Admin Dashboard</h1>
      <p>Welcome to the administration dashboard. Here you can manage users, view statistics, and perform other administrative tasks.</p>
      {error && <p className="error">{error}</p>}
      <table>
        <thead>
          <tr>
            <th>Username</th>
            <th>Number of Recipes</th>
            <th>Average Rating</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
        {users.map((user) => {
          const formattedRating = formatAverageRating(user.averageRating); 
          return (
            <tr key={user.id}>
              <td>{user.username}</td>
              <td>{user.recipeCount}</td>
              <td>{formattedRating}</td> 
              <td>
                <button onClick={() => handleDeleteUser(user.id)}>Delete</button>
              </td>
            </tr>
            );
})}
        </tbody>
      </table>
    </div>
  );
};

export default Administration;
