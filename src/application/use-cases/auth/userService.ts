// Simulación de usuarios en memoria
const users: { email: string; password: string }[] = [];

export const registerUser = (email: string, password: string) => {
  if (users.find(u => u.email === email)) {
    throw new Error("Usuario ya existe");
  }
  users.push({ email, password });
  return { mensaje: "Registro exitoso" };
};

export const loginUser = (email: string, password: string) => {
  const user = users.find(u => u.email === email && u.password === password);
  if (!user) {
    throw new Error("Credenciales inválidas");
  }
  return { mensaje: "Login exitoso" };
};
