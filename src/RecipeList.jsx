{/*
import React, { useState, useEffect } from 'react';
import { fetchAllRecipes } from './api/recipesAPI';

function RecipeList() {
    const [recipes, setRecipes] = useState([]);

    useEffect(() => {
        async function getRecipes() {
            try {
                const data = await fetchAllRecipes();
                setRecipes(data);
            } catch (error) {
                console.error("Error fetching recipes:", error);
            }
        }

        getRecipes();
    }, []);

    return (
        <div>
            <h1>Recipes</h1>
            <ul>
                {recipes.map(recipe => (
                    <li key={recipe.id}>
                        {recipe.name}: {recipe.ingredients}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default RecipeList;
*/}




