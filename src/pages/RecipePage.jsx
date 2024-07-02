import React, { useState, useEffect } from 'react';
import Recipe from '../components/Recipe';
import './RecipePage.css';
import { fetchAllRecipes, searchRecipesByName, fetchHighRatedRecipes, fetchLowRatedRecipes } from '../api/recipesAPI';

const Recipes = () => {
    const [recipes, setRecipes] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        setIsLoading(true);
        const token = localStorage.getItem('jwtToken');
        fetchAllRecipes(token)
            .then(data => {
                setRecipes(data); 
                setIsLoading(false);
            })
            .catch(error => {
                console.error('Error fetching recipes:', error);
                setError('Failed to fetch recipes');
                setIsLoading(false);
            });
    }, []);

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
    };

    const handleSearch = () => {
        setIsLoading(true);
        const token = localStorage.getItem('jwtToken');
        searchRecipesByName(token, searchTerm) 
            .then(data => {
                setRecipes(data.content);
                setIsLoading(false);
            })
            .catch(error => {
                console.error('Error searching recipes:', error);
                setError('Failed to search recipes');
                setIsLoading(false);
            });
    };

    const handleFilterChange = async (e) => {
        setIsLoading(true);
        try {
            const token = localStorage.getItem('jwtToken');
            let data;
            if (e.target.value === 'high') {
                data = await fetchHighRatedRecipes(token);
            } else if (e.target.value === 'low') {
                data = await fetchLowRatedRecipes(token);
            } else {
                data = await fetchAllRecipes(token);
            }
            setRecipes(data);
        } catch (error) {
            console.error('Error fetching filtered recipes:', error);
            setError('Failed to fetch filtered recipes');
        }
        setIsLoading(false);
    };


    const truncateDescription = (text, maxLength) => {
        return text.length > maxLength ? text.substring(0, maxLength - 3) + '...' : text;
    };

    if (isLoading) return <p>Loading...</p>;
    if (error) return <p>{error}</p>;

    return (
        <div className="recipe-book">
            <div className="recipe-header">
                <h1 className="recipe-title">Recipes</h1>
                <div className="search-and-filter-bar">
                    <div className="search-bar">
                        <input
                            type="text"
                            placeholder="Search recipes by name..."
                            value={searchTerm}
                            onChange={handleSearchChange}
                            className="search-input"
                        />
                        <button onClick={handleSearch} className="search-button">Search</button>
                    </div>
                    <div className="filter-bar">
                        <select onChange={handleFilterChange} className="filter-dropdown">
                            <option value="all">All Ratings</option>
                            <option value="high">High Rated (over 5)</option>
                            <option value="low">Low Rated (under 5)</option>
                        </select>
                    </div>
                </div>
            </div>
            <div className="recipe-list">
            {recipes.length > 0 ? recipes.map(recipe => (
  <Recipe 
    key={recipe.id}
    id={recipe.id}
    photoBase64={recipe.photoBase64} 
    name={recipe.name} 
    description={truncateDescription(recipe.description, 100)}
    category={recipe.category} 
    averageRating={recipe.averageRating} 
    ratingCount={recipe.ratingCount} 
    smallText={true}
  />
)) : <p>No recipes found.</p>}

            </div>
        </div>
    );
};

export default Recipes;

