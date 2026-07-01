import { axiosClient } from './axiosClient';
import type {
  ApiSuccessResponse,
  AuthResponseData,
  LoginPayload,
  RegisterPayload,
} from '../types/auth';

export const authApi = {
  async login(payload: LoginPayload): Promise<AuthResponseData> {
    const response = await axiosClient.post<ApiSuccessResponse<AuthResponseData>>(
      '/auth/login',
      payload
    );
    return response.data.data;
  },

  async register(payload: RegisterPayload): Promise<AuthResponseData> {
    const response = await axiosClient.post<ApiSuccessResponse<AuthResponseData>>(
      '/auth/register',
      payload
    );
    return response.data.data;
  },

  async logout(): Promise<void> {
    await axiosClient.post('/auth/logout');
  },
};