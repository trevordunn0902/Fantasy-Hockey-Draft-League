import { createContext, useState, useEffect } from "react";
import { loginUser, registerUser } from "../api/api";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(
    JSON.parse(localStorage.getItem("fantasyUser")) || null
  );
  useEffect(() => {
    if (user) {
      localStorage.setItem("fantasyUser", JSON.stringify(user));
    } else {
      localStorage.removeItem("fantasyUser");
    }
  }, [user]);

  const login = async (email, password) => {
    const userData = await loginUser(email, password);
    setUser(userData);
    return userData;
  };

  const register = async (email, password) => {
    const userData = await registerUser(email, password);
    setUser(userData);
    return userData;
  };

  const logout = () => setUser(null);

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
