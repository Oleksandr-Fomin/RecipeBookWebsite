
import React, { useState } from 'react';

function RecipeEdit({ recipe }) {
  const [editedRecipe, setEditedRecipe] = useState({ ...recipe });

  const handleInputChange = e => {
    const { name, value } = e.target;
    setEditedRecipe(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleSave = () => {

  };

  return (
    <div>
      <h2>Edit Recipe</h2>
      <form>
        <label>
          Name:
          <input
            type="text"
            name="name"
            value={editedRecipe.name}
            onChange={handleInputChange}
          />
        </label>
        <button onClick={handleSave}>Save</button>
      </form>
    </div>
  );
}

export default RecipeEdit;
