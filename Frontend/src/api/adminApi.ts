import { axiosClient } from './axiosClient';
import type {
  AdminCourse,
  AdminUser,
  ApiSuccessResponse,
  CreateCoursePayload,
  CreateUserPayload,
  PageResponse,
  Role,
  UpdateCoursePayload,
  UpdateUserPayload,
} from '../types/auth';

interface SearchUsersParams {
  keyword?: string;
  role?: Role;
  page?: number;
  size?: number;
}

interface SearchCoursesParams {
  keyword?: string;
  page?: number;
  size?: number;
}

export const adminApi = {
  async searchUsers(params: SearchUsersParams): Promise<PageResponse<AdminUser>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<AdminUser>>>('/admin/users', {
      params: {
        keyword: params.keyword ?? '',
        role: params.role,
        page: params.page ?? 0,
        size: params.size ?? 10,
      },
    });
    return response.data.data;
  },

  async createUser(payload: CreateUserPayload): Promise<AdminUser> {
    const response = await axiosClient.post<ApiSuccessResponse<AdminUser>>('/admin/users', payload);
    return response.data.data;
  },

  async deleteUser(userId: number): Promise<void> {
    await axiosClient.delete(`/admin/users/${userId}`);
  },

  async updateUser(userId: number, payload: UpdateUserPayload): Promise<AdminUser> {
    const response = await axiosClient.put<ApiSuccessResponse<AdminUser>>(`/admin/users/${userId}`, payload);
    return response.data.data;
  },

  async searchCourses(params: SearchCoursesParams): Promise<PageResponse<AdminCourse>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<AdminCourse>>>('/admin/courses', {
      params: {
        keyword: params.keyword ?? '',
        page: params.page ?? 0,
        size: params.size ?? 10,
      },
    });
    return response.data.data;
  },

  async createCourse(payload: CreateCoursePayload): Promise<AdminCourse> {
    const response = await axiosClient.post<ApiSuccessResponse<AdminCourse>>('/admin/courses', payload);
    return response.data.data;
  },

  async deleteCourse(courseId: number): Promise<void> {
    await axiosClient.delete(`/admin/courses/${courseId}`);
  },

  async updateCourse(courseId: number, payload: UpdateCoursePayload): Promise<AdminCourse> {
    const response = await axiosClient.put<ApiSuccessResponse<AdminCourse>>(`/admin/courses/${courseId}`, payload);
    return response.data.data;
  },
};