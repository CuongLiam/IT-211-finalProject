import type { Role } from '../types/auth';

export const getDashboardPathByRole = (role: Role): string => {
  if (role === 'ADMIN') return '/admin/users';
  if (role === 'LECTURER') return '/lecturer/dashboard';
  return '/student/dashboard';
};