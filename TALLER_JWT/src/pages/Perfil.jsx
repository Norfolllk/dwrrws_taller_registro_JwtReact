import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { API_BASE_URL } from "../config/apiConfig";

function Perfil() {
  const [datosPerfil, setDatosPerfil] = useState(null);
  const [error, setError] = useState("");

  const { token, logout } = useAuth();

  const navigate = useNavigate();

  useEffect(() => {
    const cargarPerfil = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/auth/perfil`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error("No se pudo cargar Perfil, inicie sesión nuevamente");
        }

        const datos = await response.json();

        setDatosPerfil(datos);
      } catch (err) {
        setError(err.message);
      }
    };

    cargarPerfil();
  }, [token]);

  const manejarLogOut = async () => {
    try {
      await fetch(`${API_BASE_URL}/auth/logout`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    } catch (err) {
      console.log("Error de red al intentar revocar el token: " + err);
    }

    logout();

    navigate("/login");
  };

  return (
    <div>
      <div>
        <h2>Perfil de Usuario</h2>
        <button onClick={manejarLogOut}>Cerrar Sesión</button>
      </div>

      {error && <p>{error}</p>}

      {datosPerfil && (
        <div>
          <p>{datosPerfil.Mensaje}</p>
          <p>{datosPerfil.Usuario}</p>
          <p>{datosPerfil.rol_detectado}</p>
          <p>{datosPerfil.status}</p>
        </div>
      )}
    </div>
  );
}

export default Perfil;
