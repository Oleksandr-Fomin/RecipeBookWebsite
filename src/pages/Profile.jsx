import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useState, useEffect } from 'react';
import RecipeEdit from '../components/RecipeEdit';
import Recipe from '../components/Recipe';
import AddRecipeForm from '../components/AddRecipeForm';
import './RecipePage.css';
import { fetchUserProfile, updateUserProfile } from '../api/userAPI';
import { fetchUserRecipes, fetchLikedRecipes, addRecipe, deleteRecipe } from '../api/recipesAPI';
import EditRecipeModal from '../components/EditRecipeForm';
import { updateRecipe } from '../api/recipesAPI';

const Profile = () => {
  const [activeTab, setActiveTab] = useState('profile');
  const [likedRecipes, setLikedRecipes] = useState([]);
  const [user, setUser] = useState(null);
  const [recipes, setRecipes] = useState([]);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const username = "Username"; 
  const userPhoto = "https://via.placeholder.com/150";
  const [newUsername, setNewUsername] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [editingRecipe, setEditingRecipe] = useState(null);
  const [modalMode, setModalMode] = useState(null); 
  const truncateDescription = (text, maxLength) => {
    return text.length > maxLength ? text.substring(0, maxLength - 3) + '...' : text;
};


const ProfileSettings = () => (
  <div>
    {user ? (
      <form onSubmit={handleUpdateProfile}>
        <h3>{user.username}'s Profile</h3>
        <div className="form-group">
          <input
            type="text"
            className="form-control"
            placeholder="New username"
            value={newUsername}
            onChange={(e) => setNewUsername(e.target.value)}
          />
        </div>
        <div className="form-group">
          <input
            type="password"
            className="form-control"
            placeholder="New password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
        </div>
        <div className="form-group">
          <input
            type="password"
            className="form-control"
            placeholder="Confirm new password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />
        </div>
        <button type="submit" className="btn btn-primary" style={{ backgroundColor: '#218838' }}>Update Profile</button>
        {error && <p>{error}</p>}
      </form>
    ) : (
      <p>Loading profile...</p>
    )}
  </div>
);


  const ChangePassword = () => <div>Change Password Content</div>;
  const MyRecipes = () => {
    const [alert, setAlert] = useState({ type: '', message: '' });


    const handleCloseEdit = () => {
      setEditingRecipe(null);
      setIsModalOpen(false);
    };

    const handleOpenAddModal = () => {
      console.log('Opening modal in add mode');
      setModalMode('add');
      setEditingRecipe(null);
      setIsModalOpen(true);
    };
    
    const handleOpenEditModal = (recipe) => {
      console.log('Opening modal in edit mode', recipe);
      setModalMode('edit');
      setEditingRecipe(recipe);
      setIsModalOpen(true);
    };
    

    const handleUpdateRecipe = async (updatedRecipeData) => {
      const token = localStorage.getItem('jwtToken');
      try {
        const updatedRecipe = await updateRecipe(editingRecipe.id, updatedRecipeData, token);
        setRecipes(recipes.map((recipe) => (recipe.id === updatedRecipe.id ? updatedRecipe : recipe)));
        handleCloseEdit();
      } catch (error) {
        console.error('Error updating recipe:', error);
      }
    };

    const handleDeleteRecipe = async (recipeId) => {
      const token = localStorage.getItem('jwtToken');
      try {
        await deleteRecipe(recipeId, token); 
        setRecipes(recipes.filter((recipe) => recipe.id !== recipeId));
        setAlert({ type: 'success', message: 'Recipe deleted successfully.' });
      } catch (error) {
        console.error('Error deleting recipe:', error);
        setAlert({ type: 'danger', message: 'Error deleting recipe. Please try again.' });
      }
    };
  
    return (
      <div>
        <button onClick={handleOpenAddModal} className="btn btn-success mb-3">Add Recipe</button>
        <div className="recipe-book">
          <h1>My Recipes</h1>
          <div className="recipe-list">
            {recipes.length > 0 ? (
              recipes.map((recipe) => (
                <div key={recipe.id} className="recipe-item">
                  <Recipe
                    id={recipe.id}
                    photoBase64={recipe.photoBase64}
                    name={recipe.name}
                    description={truncateDescription(recipe.description, 100)}
                    category={recipe.category}
                    averageRating={recipe.averageRating} 
                    ratingCount={recipe.ratingCount}
                  />
                  <button className="btn btn-primary" onClick={() => handleOpenEditModal(recipe)}>Edit</button>
                  <button className="btn btn-danger" onClick={() => handleDeleteRecipe(recipe.id)}>Delete</button>
                </div>
              ))
            ) : (
              <p>No recipes found.</p>
            )}
          </div>
        </div>
        
<EditRecipeModal
  show={isModalOpen}
  onHide={() => {
    setEditingRecipe(null); 
    setIsModalOpen(false);  
    setModalMode(null);     
  }}
  recipe={editingRecipe}
  onSave={modalMode === 'edit' ? handleUpdateRecipe : handleAddRecipe} 
mode={modalMode}
/>
      </div>
    );
  };
  
  
  const LikedRecipes = () => (
    <div className="recipe-book">
      <h1>Liked Recipes</h1>
      <div className="recipe-list">
        {likedRecipes.length > 0 ? (
          likedRecipes.map((recipe, index) => (
            <Recipe
              key={index}
              id={recipe.id}
              photoBase64={recipe.photoBase64}
              name={recipe.name}
              description={truncateDescription(recipe.description, 100)}
              category={recipe.category}
              averageRating={recipe.averageRating} 
              ratingCount={recipe.ratingCount}
            />
          ))
        ) : (
          <p>No liked recipes found.</p>
        )}
      </div>
    </div>
  );
  
  

  const renderContent = () => {
    switch (activeTab) {
      case 'profile':
        return <ProfileSettings />;
      case 'password':
        return <ChangePassword />;
      case 'myRecipes':
        return <MyRecipes />;
      case 'likedRecipes':
        return <LikedRecipes />;
      default:
        return null;
    }
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    try {
      const token = localStorage.getItem('jwtToken');
      const updatedUser = await updateUserProfile({
        username: newUsername,
        password: newPassword
      }, token);
      setUser(updatedUser);
      setNewUsername('');
      setNewPassword('');
      setConfirmPassword('');
      setError('');
    } catch (error) {
      setError('Failed to update profile. Please try again.');
      console.error('Profile update error:', error);
    }
  };


  const handleAddRecipe = async (newRecipe) => {
    const token = localStorage.getItem('jwtToken');
    try {
      const response = await addRecipe(newRecipe, token);
      if (response) {
        setIsModalOpen(false); 
        if (user && user.id) {
          const userRecipes = await fetchUserRecipes(user.id, token);
          setRecipes(userRecipes); 
        }
      } else {
        setError('Failed to add the recipe. Please try again later.');
      }
    } catch (error) {
      setError('An error occurred while adding the recipe. Please try again later.');
      console.error('Error adding recipe:', error);
    }
  };
  
  

  
  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    fetchUserProfile(token)
      .then(data => setUser(data))
      .catch(error => {
        console.error('Error fetching user profile:', error);
        setError('Error fetching user profile. Please try again later.');
      });
  }, []);
  
useEffect(() => {
  if (activeTab === 'likedRecipes') {
    const token = localStorage.getItem('jwtToken');
    fetchLikedRecipes(token)
      .then(recipes => setLikedRecipes(recipes))
      .catch(error => {
        console.error('Error:', error);
        setError('An error occurred while fetching liked recipes. Please try again later.');
      });
  }
}, [activeTab]);


useEffect(() => {
  if (user && user.id) {
    const token = localStorage.getItem('jwtToken');
    fetchUserRecipes(user.id, token)
      .then(userRecipes => setRecipes(userRecipes))
      .catch(error => {
        console.error('Error fetching recipes:', error);
        setError('An error occurred while fetching recipes. Please try again later.');
      });
  }
}, [user]);

  return (
    
    <div className="container my-5">
      <div className="row">

        <div className="col-md-3">
          <div className="card">
            <img src={userPhoto} alt="User" className="card-img-top" /> 
            <div className="card-body">
            <h5 className="card-title text-center">Hi, {user ? user.username : 'Loading...'}</h5>  
              <div className="list-group">
                <button onClick={() => setActiveTab('profile')} className="list-group-item list-group-item-action">
                  Profile Settings
                </button>
                <button onClick={() => setActiveTab('myRecipes')} className="list-group-item list-group-item-action">
                  My Recipes
                </button>
                <button onClick={() => setActiveTab('likedRecipes')} className="list-group-item list-group-item-action">
                  Liked Recipes
                </button>
              </div>
            </div>
          </div>
        </div>
  

        <div className="col-md-9">
          <div className="card">
            <div className="card-body">
              {renderContent()}
            </div>
          </div>
        </div>
      </div>
  
{/*
      <div className={`modal ${isModalOpen ? 'show' : ''}`} style={{ display: isModalOpen ? 'block' : 'none' }} tabIndex="-1" role="dialog">
        <div className="modal-dialog" role="document">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">Add a New Recipe</h5>
              <button type="button" className="close" onClick={() => setIsModalOpen(false)} aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div className="modal-body">
              <AddRecipeForm onSave={handleAddRecipe} />
            </div>
          </div>
        </div>
      </div>
      {isModalOpen && <div className="modal-backdrop fade show"></div>}
      */}
    </div>
  
    
  );

  


  
}

export default Profile;
