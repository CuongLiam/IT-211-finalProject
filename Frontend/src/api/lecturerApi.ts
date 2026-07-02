import { axiosClient } from './axiosClient';
import type {
  AssignmentItem,
  ApiSuccessResponse,
  CreateAssignmentPayload,
  GradeItem,
  GradePayload,
  LectureMaterialItem,
  LecturerSubmissionItem,
  PageResponse,
} from '../types/auth';

export const lecturerApi = {
  async createAssignment(payload: CreateAssignmentPayload): Promise<AssignmentItem> {
    const response = await axiosClient.post<ApiSuccessResponse<AssignmentItem>>(
      '/lecturer/assignments',
      payload
    );
    return response.data.data;
  },

  async listAssignments(page = 0, size = 10): Promise<PageResponse<AssignmentItem>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<AssignmentItem>>>(
      '/lecturer/assignments',
      { params: { page, size } }
    );
    return response.data.data;
  },

  async listSubmissions(page = 0, size = 10): Promise<PageResponse<LecturerSubmissionItem>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<LecturerSubmissionItem>>>(
      '/lecturer/submissions',
      { params: { page, size } }
    );
    return response.data.data;
  },

  async gradeSubmission(submissionId: number, payload: GradePayload): Promise<GradeItem> {
    const response = await axiosClient.post<ApiSuccessResponse<GradeItem>>(
      `/lecturer/submissions/${submissionId}/grade`,
      payload
    );
    return response.data.data;
  },

  async listGrades(page = 0, size = 10): Promise<PageResponse<GradeItem>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<GradeItem>>>(
      '/lecturer/grades',
      { params: { page, size } }
    );
    return response.data.data;
  },

  async uploadMaterial(params: {
    courseId: number;
    title: string;
    description?: string;
    file: File;
  }): Promise<LectureMaterialItem> {
    const formData = new FormData();
    formData.append('courseId', String(params.courseId));
    formData.append('title', params.title);
    if (params.description) {
      formData.append('description', params.description);
    }
    formData.append('file', params.file);

    const response = await axiosClient.post<ApiSuccessResponse<LectureMaterialItem>>(
      '/lecturer/materials',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );

    return response.data.data;
  },

  async listMaterials(page = 0, size = 10): Promise<PageResponse<LectureMaterialItem>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<LectureMaterialItem>>>(
      '/lecturer/materials',
      { params: { page, size } }
    );
    return response.data.data;
  },
};
