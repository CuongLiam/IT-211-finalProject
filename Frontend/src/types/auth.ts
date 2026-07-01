export type Role = 'ADMIN' | 'LECTURER' | 'STUDENT';

export interface AuthUser {
  id: number;
  email: string;
  role: Role;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  fullName: string;
  email: string;
  password: string;
}

export interface AuthResponseData {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: number;
  email: string;
  role: Role;
}

export interface ApiSuccessResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface ApiErrorResponse {
  success: false;
  status: number;
  error: string;
  message: string;
  path: string;
  timestamp: string;
  validationErrors?: Record<string, string>;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface AdminUser {
  id: number;
  fullName: string;
  email: string;
  role: Role;
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AdminCourse {
  id: number;
  code: string;
  name: string;
  description: string | null;
  lecturerId: number;
  lecturerName: string;
  lecturerEmail: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserPayload {
  fullName: string;
  email: string;
  password: string;
  role: Role;
  enabled: boolean;
}

export interface CreateCoursePayload {
  code: string;
  name: string;
  description?: string;
  lecturerId: number;
}

export interface StudentCourse {
  id: number;
  code: string;
  name: string;
  description: string | null;
  lecturerId: number;
  lecturerName: string;
  lecturerEmail: string;
  enrolled: boolean;
}

export interface EnrollmentCourse {
  enrollmentId: number;
  courseId: number;
  courseCode: string;
  courseName: string;
  courseDescription: string | null;
  lecturerId: number;
  lecturerName: string;
  status: string;
  enrolledAt: string;
}