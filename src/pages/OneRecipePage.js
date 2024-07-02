import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Alert, Container, Spinner } from 'react-bootstrap';
import './OneRecipePage.css';
import { fetchRecipeById, fetchCommentsForRecipe, postComment } from '../api/recipesAPI';

const OneRecipePage = () => {
  const [recipe, setRecipe] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [error, setError] = useState('');
  const { id } = useParams();
  const [rating, setRating] = useState(1);

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      fetchRecipeById(id, token)
        .then(data => {
          setRecipe(data);
          return fetchCommentsForRecipe(id, token);
        })
        .then(commentsData => setComments(commentsData))
        .catch(error => {
          setError(error.message);
          console.error('Error:', error);
        });
    } else {
      setError('No token found. Please login.');
    }
  }, [id]);

  const handleCommentChange = (e) => {
    setNewComment(e.target.value);
  };

  const submitComment = (e) => {
    e.preventDefault();
    const token = localStorage.getItem('jwtToken');
    if (token) {
      postComment(id, newComment, rating, token)
        .then(comment => {
          setComments([...comments, comment]);
          setNewComment('');
  
          if (recipe && recipe.ratingCount != null && recipe.averageRating != null) {
            const newRating = parseInt(rating); 
            const newCount = recipe.ratingCount + 1;
            const newAverage = ((recipe.averageRating * recipe.ratingCount) + newRating) / newCount;
  
            setRecipe({
              ...recipe,
              ratingCount: newCount,
              averageRating: newAverage,
            });
          }
        })
        .catch(error => {

          setError(error.response?.data?.message || 'An error occurred while posting the comment.');
          console.error('Error posting comment:', error);
        });
    } else {
      setError('Authentication token is missing. Please login again.');
    }
  };
  


  if (error) {
    return <Alert variant="danger">Error: {error}</Alert>;
  }

  if (!recipe) {
    return <Container className="text-center py-5"><Spinner animation="border" /></Container>;
  }

  const stars = Array.from({ length: 5 }, (_, index) => {
    return <span key={index} className={index < recipe.rating ? 'star filled' : 'star'}>★</span>;
  });

  return (
    <div className="recipe-details-container">
      <div className="recipe-details-header">
        <div className="recipe-details">
          <h2 className="recipe-title">{recipe.name}</h2>
          <p className="recipe-date">Published on: {recipe.publishDate}</p>
          <div className="recipe-rating1">
        {recipe.averageRating != null
          ? `Average Rating: ${recipe.averageRating.toFixed(1)} (${recipe.ratingCount} ratings)`
          : `No ratings yet`}
      </div>
          <p className="recipe-description">{recipe.description}</p>
        </div>
        <div className="recipe-img-container">
          <img src={recipe.photoBase64} alt={recipe.name} />
        </div>
      </div>
  
      <div className="ingredients-container">
        <h3>Ingredients</h3>
        <p>{recipe.ingredients}</p>
      </div>
  
      <div className="instructions-container">
        <h3>Instructions</h3>
        <p>{recipe.instructions}</p>
      </div>

      <div className="comments-section">
        <h3>Comments</h3>
        <form onSubmit={submitComment}>
        <select value={rating} onChange={(e) => setRating(e.target.value)}>
  {[...Array(10)].map((_, i) => (
    <option key={i} value={i + 1}>
      {i + 1}
    </option>
  ))}
</select>
          <textarea
            value={newComment}
            onChange={handleCommentChange}
            placeholder="Write a comment..."
            required
          />
          <button type="submit">Post Comment</button>
        </form>
        {comments.map(comment => (
          <div key={comment.id} className="comment">
            <p><strong>{comment.user.username}:</strong> {comment.text}</p>
          </div>
        ))}
      </div>
    </div>
  );
};


export default OneRecipePage;
