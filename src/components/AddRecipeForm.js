import React, { useState } from 'react';

const AddRecipeForm = ({ onSave }) => {
  const [recipeData, setRecipeData] = useState({
    name: '',
    description: '',
    ingredients: '',
    instructions: '',
    photo: '', 
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setRecipeData(prevData => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (loadEvent) => {
        setRecipeData(prevData => ({
          ...prevData,
          photoBase64: loadEvent.target.result 
        }));
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave({
      ...recipeData,
      ingredients: recipeData.ingredients.split(',').map(ingredient => ingredient.trim()),
      instructions: recipeData.instructions.split('.').map(instruction => instruction.trim()),
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="recipeName">Recipe Name</label>
        <input
          type="text"
          className="form-control"
          id="recipeName"
          name="name"
          value={recipeData.name}
          onChange={handleInputChange}
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="recipePhoto">Recipe Photo</label>
        <input
          type="file"
          className="form-control"
          id="recipePhoto"
          name="photo"
          onChange={handleFileChange}
          accept="image/*"
        />
      </div>

      <div className="form-group">
        <label htmlFor="recipeDescription">Short Description</label>
        <textarea
          className="form-control"
          id="recipeDescription"
          name="description"
          value={recipeData.description}
          onChange={handleInputChange}
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="recipeIngredients">Ingredients (comma separated)</label>
        <input
          type="text"
          className="form-control"
          id="recipeIngredients"
          name="ingredients"
          value={recipeData.ingredients}
          onChange={handleInputChange}
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="recipeInstructions">Instructions (separate steps with a period)</label>
        <input
          type="text"
          className="form-control"
          id="recipeInstructions"
          name="instructions"
          value={recipeData.instructions}
          onChange={handleInputChange}
          required
        />
      </div>

      <button type="submit" className="btn btn-primary">Add Recipe</button>
    </form>
  );
};

export default AddRecipeForm;
