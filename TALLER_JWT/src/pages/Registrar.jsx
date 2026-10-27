import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { API_BASE_URL } from "../config/apiConfig";

function Registrar() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [rol, setRol] = useState("USER");
  const [mensaje, setMensaje] = useState("");
  const [error, setError] = useState("");
  const [enviando, setEnviando] = useState(false);

  const navigate = useNavigate();

  const manejarSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setMensaje("");
    setEnviando(true);

    try {
      const response = await fetch(`${API_BASE_URL}/auth/registrar`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password, rol }),
      });

      if (!response.ok) {
        const texto = await response.text();
        throw new Error(texto || "No se pudo registrar el usuario");
      }

      setMensaje("Usuario registrado correctamente. Redirigiendo al login...");

      setTimeout(() => {
        navigate("/login");
      }, 2000);
    } catch (err) {
      setError(err.message);
      setEnviando(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Crear cuenta</h1>
        <p className="auth-sub">Regístrate para acceder al sistema</p>

        <form onSubmit={manejarSubmit}>
          <div className="campo">
            <label>Nombre de usuario</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Ej: isam27"
              required
            />
          </div>

          <div className="campo">
            <label>Contraseña</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Mínimo 6 caracteres"
              minLength={6}
              required
            />
          </div>

          <div className="campo">
            <label>Rol en el sistema</label>
            <select value={rol} onChange={(e) => setRol(e.target.value)}>
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>

          {mensaje && <p className="msg msg-ok">{mensaje}</p>}
          {error && <p className="msg msg-error">{error}</p>}

          <button type="submit" className="btn" disabled={enviando}>
            {enviando ? "Registrando..." : "Registrarme"}
          </button>
        </form>

        <p className="auth-link">
          ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
        </p>
      </div>
    </div>
  );
}

export default Registrar;
