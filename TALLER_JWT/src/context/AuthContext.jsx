import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

export function AuthProvider(props) {
  const { children } = props;

  const [token, setToken] = useState(localStorage.getItem("token") || null);

  const login = (nuevoToken) => {
    localStorage.setItem("token", nuevoToken);

    setToken(nuevoToken);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  return useContext(AuthContext);
}
