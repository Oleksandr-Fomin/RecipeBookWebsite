
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const toggleRecipeLike = async (recipeId, liked) => {
  const endpoint = `${API_BASE_URL}/recipes/${recipeId}/like`;
  if (liked) {
    return axios.delete(endpoint);
  } else {
    return axios.post(endpoint);
  }
};

export default toggleRecipeLike;

