import { axiosClient } from './axiosClient';
import type {
  AssignmentItem,
  ApiSuccessResponse,
  EnrollmentCourse,
  PageResponse,
  StudentCourse,
} from '../types/auth';

interface ListCoursesParams {
  keyword?: string;
  page?: number;
  size?: number;
}

interface ListEnrollmentsParams {
  page?: number;
  size?: number;
}

export const studentApi = {
  async listCourses(params: ListCoursesParams): Promise<PageResponse<StudentCourse>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<StudentCourse>>>(
      '/student/courses',
      {
        params: {
          keyword: params.keyword ?? '',
          page: params.page ?? 0,
          size: params.size ?? 10,
        },
      }
    );

    return response.data.data;
  },

  async listEnrollments(params: ListEnrollmentsParams): Promise<PageResponse<EnrollmentCourse>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<EnrollmentCourse>>>(
      '/student/courses/enrollments',
      {
        params: {
          page: params.page ?? 0,
          size: params.size ?? 10,
        },
      }
    );

    return response.data.data;
  },

  async listAssignments(page = 0, size = 20): Promise<PageResponse<AssignmentItem>> {
    const response = await axiosClient.get<ApiSuccessResponse<PageResponse<AssignmentItem>>>(
      '/student/courses/assignments',
      {
        params: { page, size },
      }
    );

    return response.data.data;
  },

  async enrollCourse(courseId: number): Promise<void> {
    await axiosClient.post(`/student/courses/${courseId}/enroll`);
  },

  async cancelEnrollment(courseId: number): Promise<void> {
    await axiosClient.delete(`/student/courses/${courseId}/enroll`);
  },
};
