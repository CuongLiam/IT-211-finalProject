import React from 'react';
import { useAuthContext } from '../../context/AuthContext';

const UserManagementPage: React.FC = () => {
  const { user, logout } = useAuthContext();

  return (
    <div className="min-h-screen bg-slate-50 p-6">
      <div className="mx-auto max-w-5xl rounded-2xl bg-white p-6 shadow">
        <h1 className="text-2xl font-bold mb-2">Admin - User Management</h1>
        <p className="text-slate-600 mb-6">
          Logged in as: {user?.email} ({user?.role})
        </p>
        <button
          onClick={logout}
          className="rounded-lg bg-red-600 px-4 py-2 text-white hover:bg-red-500 transition"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

export default UserManagementPage;