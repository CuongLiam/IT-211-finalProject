import React, { createContext, useContext, useMemo, useState } from 'react';
import { authApi } from '../api/authApi';
import { tokenStorage } from '../api/tokenStorage';
import type { AuthUser, LoginPayload, RegisterPayload } from '../types/auth';

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (payload: LoginPayload) => Promise<void>;
  register: (payload: RegisterPayload) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const loadInitialUser = (): AuthUser | null => {
  const storedUser = tokenStorage.getUser();
  const accessToken = tokenStorage.getAccessToken();
  if (!storedUser || !accessToken) return null;
  return {
    id: storedUser.id,
    email: storedUser.email,
    role: storedUser.role,
  };
};

export const AuthProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
  const [user, setUser] = useState<AuthUser | null>(loadInitialUser());

  const login = async (payload: LoginPayload) => {
    const data = await authApi.login(payload);
    tokenStorage.setTokens(data.accessToken, data.refreshToken);
    tokenStorage.setUser({
      id: data.userId,
      email: data.email,
      role: data.role,
    });
    setUser({
      id: data.userId,
      email: data.email,
      role: data.role,
    });
  };

  const register = async (payload: RegisterPayload) => {
    const data = await authApi.register(payload);
    tokenStorage.setTokens(data.accessToken, data.refreshToken);
    tokenStorage.setUser({
      id: data.userId,
      email: data.email,
      role: data.role,
    });
    setUser({
      id: data.userId,
      email: data.email,
      role: data.role,
    });
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } finally {
      tokenStorage.clearAll();
      setUser(null);
    }
  };

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isAuthenticated: !!user,
      login,
      register,
      logout,
    }),
    [user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuthContext = (): AuthContextValue => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuthContext must be used within AuthProvider');
  }
  return ctx;
};