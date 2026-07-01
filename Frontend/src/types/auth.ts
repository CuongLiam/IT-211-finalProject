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

export interface SubmissionItem {
  submissionId: number;
  assignmentId: number;
  assignmentTitle: string;
  courseId: number;
  courseCode: string;
  courseName: string;
  githubUrl: string;
  fileUrl: string;
  originalFileName: string;
  status: string;
  submittedAt: string;
}

export interface LecturerSubmissionItem {
  submissionId: number;
  assignmentId: number;
  assignmentTitle: string;
  courseId: number;
  courseCode: string;
  courseName: string;
  studentId: number;
  studentName: string;
  studentEmail: string;
  githubUrl: string;
  fileUrl: string;
  originalFileName: string;
  status: string;
  submittedAt: string;
  graded: boolean;
}

export interface GradeItem {
  gradeId: number;
  submissionId: number;
  assignmentId: number;
  assignmentTitle: string;
  studentId: number;
  studentName: string;
  studentEmail: string;
  score: number;
  feedback: string;
  gradedAt: string;
}

export interface GradePayload {
  score: number;
  feedback?: string;
}

export interface LectureMaterialItem {
  id: number;
  courseId: number;
  courseCode: string;
  courseName: string;
  lecturerId: number;
  lecturerName: string;
  title: string;
  description: string | null;
  fileUrl: string;
  originalFileName: string;
  uploadedAt: string;
  updatedAt: string;
}