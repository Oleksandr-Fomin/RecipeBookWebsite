import axios from 'axios';


const API_BASE_URL = 'http://localhost:8080/api';

export const checkLikeStatus = async (recipeId, token) => {
  try {
    const response = await fetch(`${API_BASE_URL}/recipes/${recipeId}/check-like`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    if (!response.ok) {
      throw new Error('Failed to fetch like status');
    }
    return response.json();
  } catch (error) {
    throw error;
  }
};

export const toggleLike = async (recipeId, liked, token) => {
  try {
    const endpoint = `${API_BASE_URL}/recipes/${recipeId}/like`;
    const method = liked ? 'DELETE' : 'POST';

    const response = await fetch(endpoint, {
      method,
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error('Failed to toggle like status');
    }
    return response.status;
  } catch (error) {
    throw error;
  }
};


export const fetchRecipeById = async (recipeId, token) => {
  try {
    const response = await fetch(`${API_BASE_URL}/recipes/${recipeId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  } catch (error) {
    throw error;
  }
};

export const fetchUserRecipes = async (userId, token) => {
    try {
      const response = await fetch(`${API_BASE_URL}/recipes/${userId}/recipes`, {
        headers: {
          'Authorization': `Bearer ${token}`, 
        },
      });
  
      if (!response.ok) {
        throw new Error('Unable to fetch user recipes');
      }
  
      return response.json();
    } catch (error) {
      throw error;
    }
  };
  
  export const fetchLikedRecipes = async (token) => {
    try {
      const response = await fetch(`${API_BASE_URL}/recipes/liked`, {
        headers: {
          'Authorization': `Bearer ${token}`
        },
      });
  
      if (!response.ok) {
        throw new Error('Unable to fetch liked recipes');
      }
  
      return response.json();
    } catch (error) {
      throw error;
    }
  };
  
  export const addRecipe = async (newRecipe, token) => {
    try {
      const formData = new FormData();
      for (const key in newRecipe) {
        formData.append(key, newRecipe[key]);
      }
  
      const response = await fetch(`${API_BASE_URL}/recipes/addRecipe`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });
  
      if (!response.ok) {
        throw new Error('Failed to add the recipe');
      }
  
      return response.ok;
    } catch (error) {
      throw error;
    }
  };

  export const fetchAllRecipes = async (token) => {
    try {
      const response = await fetch(`${API_BASE_URL}/recipes`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
  
      return response.json();
    } catch (error) {
      throw error;
    }
  };


  export const fetchCommentsForRecipe = async (recipeId, token) => {
    try {
      const response = await fetch(`${API_BASE_URL}/comments/${recipeId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        },
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
  
      return response.json();
    } catch (error) {
      throw error;
    }
  };
  
  
  
  export const postComment = async (recipeId, commentText, rating, token) => {
    try {
      const response = await fetch(`${API_BASE_URL}/comments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          recipeId, 
          text: commentText,
          rating,
        }),
      });
  
      if (!response.ok) {
        const errorBody = await response.json();
        throw new Error(errorBody.message || 'Failed to post comment');
      }
  
      return response.json();
    } catch (error) {
      throw error;
    }
  };

  export const searchRecipesByName = async (token, name) => {
    const url = `${API_BASE_URL}/recipes/search?name=${encodeURIComponent(name)}`;
    const response = await fetch(url, {
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        throw new Error('Error searching recipes by name');
    }

    return await response.json(); 
};

export const deleteRecipe = async (recipeId, token) => {
  try {
    const response = await fetch(`${API_BASE_URL}/recipes/${recipeId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      },
    });

    if (!response.ok) {
      throw new Error('Failed to delete recipe');
    }
  } catch (error) {
    console.error('Error deleting recipe:', error);
    throw error;
  }
};

export const updateRecipe = async (recipeId, updatedRecipeData, token) => {
  try {
    const formData = new FormData();
    for (const key in updatedRecipeData) {
      formData.append(key, updatedRecipeData[key]);
    }

    const response = await fetch(`${API_BASE_URL}/recipes/${recipeId}`, {
      method: 'PUT', 
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      body: formData,
    });

    if (!response.ok) {
      throw new Error('Failed to update the recipe');
    }

    return await response.json();
  } catch (error) {
    console.error('Error updating recipe:', error);
    throw error;
  }
};

export const fetchHighRatedRecipes = async (token) => {
  try {
    const response = await fetch(`${API_BASE_URL}/recipes/rating/high`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    if (!response.ok) {
      throw new Error('Failed to fetch high-rated recipes');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching high-rated recipes:', error);
    throw error;
  }
};

export const fetchLowRatedRecipes = async (token) => {
  try {
    const response = await fetch(`${API_BASE_URL}/recipes/rating/low`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    if (!response.ok) {
      throw new Error('Failed to fetch low-rated recipes');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching low-rated recipes:', error);
    throw error;
  }
};




  
  
  
  
  