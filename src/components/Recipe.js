import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { checkLikeStatus, toggleLike } from '../api/recipesAPI'; 
import './Recipe.css';
import { useLikes } from './LikesContext';

function Recipe({ id, photoBase64, name, description, category, averageRating, ratingCount, smallText }) {
  const { likes, setLikes } = useLikes(); 
  const [liked, setLiked] = useState(false);

  useEffect(() => {
    if (likes.has(id)) {
      setLiked(true);
    } else {
      const token = localStorage.getItem('jwtToken');
      checkLikeStatus(id, token)
        .then(isLiked => setLiked(isLiked))
        .catch(error => console.error('Error fetching like status:', error));
    }
  }, [id, likes]); 

  const handleToggleLike = async () => {
    const token = localStorage.getItem('jwtToken'); 
    try {
      await toggleLike(id, liked, token);
      setLiked(!liked);

      const updatedLikes = new Set(likes);
      if (liked) {
        updatedLikes.delete(id);
      } else {
        updatedLikes.add(id);
      }
      setLikes(updatedLikes); 
    } catch (error) {
      console.error('Error toggling like status:', error);
    }
  };

  const truncateDescription = (text, maxLength) => {
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
  };
  const displayRating = averageRating != null ? averageRating.toFixed(1) : 'N/A';
  const truncatedDescription = truncateDescription(description, 100);
  const ratingClass = `recipe-rating ${smallText ? 'recipe-rating-small' : ''}`;

  return (
    <div className="recipe-card">
      <div className="recipe-image" style={{ backgroundImage: `url(${photoBase64})` }}>
      <div className="recipe-heart" onClick={handleToggleLike}>
        {liked ? '❤️' : '🤍'}
      </div>
        <div className="recipe-category">{category}</div>
      </div>
      <div className="recipe-details">
      <h3 className="recipe-title">
  <Link to={`/recipe/${id}`} className="recipe-link">{name}</Link>
</h3>
<p className="recipe-description">{truncatedDescription}</p>
<div className={ratingClass}>
        {ratingCount > 0
          ? `${averageRating.toFixed(1)} / 10 (${ratingCount} Review${ratingCount !== 1 ? 's' : ''})`
          : `No ratings / 10 (0 Reviews)`}
      </div>

      </div>
    </div>
  );
}

export default Recipe;
