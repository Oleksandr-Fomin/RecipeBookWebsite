import React, { useState, useEffect } from 'react';
import { Modal, Button } from 'react-bootstrap'; 

const EditRecipeModal = ({ show, onHide, recipe, onSave, mode}) => {
  const [recipeData, setRecipeData] = useState({
    name: '',
    description: '',
    ingredients: '',
    instructions: '',
    photoBase64: '',
  });

  useEffect(() => {
    if (mode === 'edit' && recipe) {
      setRecipeData({
        name: recipe.name || '',
        description: recipe.description || '',
        ingredients: recipe.ingredients || '',
        instructions: recipe.instructions || '',
        photoBase64: recipe.photoBase64 || '',
      });
    } else if (mode === 'add') {
      setRecipeData({
        name: '',
        description: '',
        ingredients: '',
        instructions: '',
        photoBase64: '',
      });
    }
  }, [mode, recipe]);
    

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setRecipeData(prevData => ({
      ...prevData,
      [name]: value,
    }));
  };
  

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setRecipeData(prevData => ({
          ...prevData,
          photoBase64: reader.result
        }));
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSave(recipeData);
  };

return (
  <Modal show={show} onHide={onHide}>
    <Modal.Header closeButton>
      <Modal.Title>{mode === 'edit' ? 'Edit Recipe' : 'Add Recipe'}</Modal.Title>
    </Modal.Header>
    <Modal.Body>
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
          />
        </div>
        <div className="form-group">
          <label htmlFor="recipeDescription">Description</label>
          <textarea
            className="form-control"
            id="recipeDescription"
            name="description"
            value={recipeData.description}
            onChange={handleInputChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor
="recipeIngredients">Ingredients (comma separated)</label>
<textarea
         className="form-control"
         id="recipeIngredients"
         name="ingredients"
         value={recipeData.ingredients}
         onChange={handleInputChange}
       />
</div>
<div className="form-group">
<label htmlFor="recipeInstructions">Instructions (separate steps with a period)</label>
<textarea
         className="form-control"
         id="recipeInstructions"
         name="instructions"
         value={recipeData.instructions}
         onChange={handleInputChange}
       />
</div>
<div className="form-group">
<label htmlFor="recipePhoto">Recipe Photo</label>
<input
         type="file"
         className="form-control-file"
         id="recipePhoto"
         onChange={handleFileChange}
       />
{recipeData.photoBase64 && (
<img src={recipeData.photoBase64} alt="Recipe" style={{ maxWidth: '100%', marginTop: '10px' }} />
)}
</div>
</form>
</Modal.Body>
<Modal.Footer>
<Button variant="secondary" onClick={onHide}>
Close
</Button>
<Button variant="primary" onClick={handleSubmit}>
Save Changes
</Button>
</Modal.Footer>
</Modal>
);
};

export default EditRecipeModal;




