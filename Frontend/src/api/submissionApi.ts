import { axiosClient } from './axiosClient';
import type { ApiSuccessResponse, PageResponse, SubmissionItem } from '../types/auth';

export const submissionApi = {
  async submitAssignment(params: {
    assignmentId: number;
    githubUrl: string;
    file: File;
  }): Promise<SubmissionItem> {
    const formData = new FormData();
    formData.append('assignmentId', String(params.assignmentId));
    formData.append('githubUrl', params.githubUrl);
    formData.append('file', params.file);

    const response = await axiosClient.post<ApiSuccessResponse<SubmissionItem>>(
      '/student/submissions',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );

    return response.data.data;
  },

  async listMySubmissions(page = 0, size = 10): Promise<PageResponse<SubmissionItem>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<SubmissionItem>>>(
      '/student/submissions',
      { params: { page, size } }
    );

    return response.data.data;
  },
};
