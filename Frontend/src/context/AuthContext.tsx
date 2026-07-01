import React, { createContext, useMemo, useState } from 'react';
import { authApi } from '../api/authApi';
import { tokenStorage } from '../api/tokenStorage';
import type { AuthUser, LoginPayload, RegisterPayload } from '../types/auth';

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (payload: LoginPayload) => Promise<AuthUser>;
  register: (payload: RegisterPayload) => Promise<AuthUser>;
  logout: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

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

  const login = async (payload: LoginPayload): Promise<AuthUser> => {
    const data = await authApi.login(payload);
    const nextUser: AuthUser = {
      id: data.userId,
      email: data.email,
      role: data.role,
    };

    tokenStorage.setTokens(data.accessToken, data.refreshToken);
    tokenStorage.setUser(nextUser);
    setUser(nextUser);

    return nextUser;
  };

  const register = async (payload: RegisterPayload): Promise<AuthUser> => {
    const data = await authApi.register(payload);
    const nextUser: AuthUser = {
      id: data.userId,
      email: data.email,
      role: data.role,
    };

    tokenStorage.setTokens(data.accessToken, data.refreshToken);
    tokenStorage.setUser(nextUser);
    setUser(nextUser);

    return nextUser;
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