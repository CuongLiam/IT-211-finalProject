import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import UserManagementPage from '../pages/admin/UserManagementPage';
import LecturerDashboardPage from '../pages/lecturer/LecturerDashboardPage';
import StudentDashboardPage from '../pages/student/StudentDashboardPage';

const ForbiddenPage: React.FC = () => (
  <div className="min-h-screen bg-rose-50 flex items-center justify-center">
    <h1 className="text-3xl font-bold text-rose-700">403 - Forbidden</h1>
  </div>
);

const RouterConfig: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/auth/login" replace />} />
      <Route path="/auth/login" element={<LoginPage />} />
      <Route path="/auth/register" element={<RegisterPage />} />
      <Route path="/forbidden" element={<ForbiddenPage />} />

      <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
        <Route path="/admin/users" element={<UserManagementPage />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={['LECTURER']} />}>
        <Route path="/lecturer/dashboard" element={<LecturerDashboardPage />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={['STUDENT']} />}>
        <Route path="/student/dashboard" element={<StudentDashboardPage />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default RouterConfig;