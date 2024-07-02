
const API_BASE_URL = 'http://localhost:8080'; 

export const fetchUsers = async () => {
  const response = await fetch(`${API_BASE_URL}/admin/users`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
    },
  });
  if (!response.ok) {
    throw new Error('Error fetching users');
  }
  return response.json();
};

export const deleteUser = async (userId) => {
  const response = await fetch(`${API_BASE_URL}/admin/users/${userId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
    },
  });
  if (!response.ok) {
    throw new Error('Error deleting user');
  }
  return response.json();
};

export const getUserRecipeCount = async (userId) => {
  const token = localStorage.getItem('jwtToken');
  if (!token) {
    throw new Error('No authentication token found.');
  }

  const response = await fetch(`${API_BASE_URL}/admin/${userId}/recipe-count`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Error fetching recipe count');
  }

  return response.json();
};

export const getUserAverageRating = async (userId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/${userId}/average-rating`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Error fetching average rating: ${response.statusText}`);
    }

    const data = await response.json();
    return data.averageRating;
  } catch (error) {
    console.error('Error fetching average rating:', error);
    throw error;
  }
};





