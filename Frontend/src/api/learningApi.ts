import { axiosClient } from './axiosClient';
import type {
  ApiSuccessResponse,
  GradeItem,
  LectureMaterialItem,
  PageResponse,
} from '../types/auth';

export const learningApi = {
  async listMyGrades(page = 0, size = 10): Promise<PageResponse<GradeItem>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<GradeItem>>>(
      '/student/grades',
      { params: { page, size } }
    );
    return response.data.data;
  },

  async listMyMaterials(page = 0, size = 10): Promise<PageResponse<LectureMaterialItem>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<LectureMaterialItem>>>(
      '/student/materials',
      { params: { page, size } }
    );
    return response.data.data;
  },
};
