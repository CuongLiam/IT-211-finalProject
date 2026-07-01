import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '../../context/AuthContext';

const LoginPage: React.FC = () => {
  const { login } = useAuthContext();
  const navigate = useNavigate();

  const handleLogin = async () => {
    await login({
      email: 'admin@example.com',
      password: '12345678',
    });
    navigate('/admin/users');
  };

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
      <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow">
        <h1 className="text-2xl font-bold mb-4">Login</h1>
        <p className="text-sm text-slate-600 mb-6">Demo login nhanh cho Bước 9</p>
        <button
          onClick={handleLogin}
          className="w-full rounded-xl bg-slate-900 px-4 py-3 text-white hover:bg-slate-700 transition"
        >
          Login as Admin (demo)
        </button>
      </div>
    </div>
  );
};

export default LoginPage;