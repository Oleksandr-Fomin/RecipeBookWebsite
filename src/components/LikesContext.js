
import React, { createContext, useContext, useState } from 'react';

const LikesContext = createContext();

export const useLikes = () => useContext(LikesContext);

export const LikesProvider = ({ children }) => {
    const [likes, setLikes] = useState(new Set());

  const toggleLike = (id) => {
    setLikes((currentLikes) => {
      const newLikes = new Set(currentLikes);
      if (newLikes.has(id)) {
        newLikes.delete(id);
      } else {
        newLikes.add(id);
      }
      return newLikes;
    });
  };

  return (
    <LikesContext.Provider value={{ likes, setLikes }}>
      {children}
    </LikesContext.Provider>
  );
};
