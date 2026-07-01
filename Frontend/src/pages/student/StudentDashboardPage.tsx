import React, { useCallback, useEffect, useState } from 'react';
import { AxiosError } from 'axios';
import { useAuthContext } from '../../context/useAuthContext';
import { studentApi } from '../../api/studentApi';
import SubmissionPage from './SubmissionPage';
import StudentMaterialsPage from './StudentMaterialsPage';
import type {
  ApiErrorResponse,
  EnrollmentCourse,
  PageResponse,
  StudentCourse,
} from '../../types/auth';

const PAGE_SIZE = 8;

const StudentDashboardPage: React.FC = () => {
  const { user, logout } = useAuthContext();

  const [keyword, setKeyword] = useState('');
  const [coursesPage, setCoursesPage] = useState(0);
  const [enrollmentsPage, setEnrollmentsPage] = useState(0);

  const [coursesData, setCoursesData] = useState<PageResponse<StudentCourse> | null>(null);
  const [enrollmentsData, setEnrollmentsData] = useState<PageResponse<EnrollmentCourse> | null>(null);

  const [loadingCourses, setLoadingCourses] = useState(false);
  const [loadingEnrollments, setLoadingEnrollments] = useState(false);
  const [actionLoadingCourseId, setActionLoadingCourseId] = useState<number | null>(null);
  const [errorMessage, setErrorMessage] = useState('');

  const extractErrorMessage = (err: unknown): string => {
    const axiosErr = err as AxiosError<ApiErrorResponse>;
    return axiosErr.response?.data?.message ?? 'Something went wrong';
  };

  const loadCourses = useCallback(async () => {
    setLoadingCourses(true);
    setErrorMessage('');
    try {
      const data = await studentApi.listCourses({
        keyword,
        page: coursesPage,
        size: PAGE_SIZE,
      });
      setCoursesData(data);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err));
    } finally {
      setLoadingCourses(false);
    }
  }, [keyword, coursesPage]);

  const loadEnrollments = useCallback(async () => {
    setLoadingEnrollments(true);
    setErrorMessage('');
    try {
      const data = await studentApi.listEnrollments({
        page: enrollmentsPage,
        size: PAGE_SIZE,
      });
      setEnrollmentsData(data);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err));
    } finally {
      setLoadingEnrollments(false);
    }
  }, [enrollmentsPage]);

  useEffect(() => {
    queueMicrotask(() => {
      void loadCourses();
    });
  }, [loadCourses]);

  useEffect(() => {
    queueMicrotask(() => {
      void loadEnrollments();
    });
  }, [loadEnrollments]);

  const handleSearchCourses = async (e: React.FormEvent) => {
    e.preventDefault();
    setCoursesPage(0);
    await loadCourses();
  };

  const handleEnrollToggle = async (course: StudentCourse) => {
    setActionLoadingCourseId(course.id);
    setErrorMessage('');
    try {
      if (course.enrolled) {
        await studentApi.cancelEnrollment(course.id);
      } else {
        await studentApi.enrollCourse(course.id);
      }

      await Promise.all([loadCourses(), loadEnrollments()]);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err));
    } finally {
      setActionLoadingCourseId(null);
    }
  };

  return (
    <div className="min-h-screen bg-cyan-50 p-4 md:p-6">
      <div className="mx-auto max-w-7xl rounded-2xl bg-white p-4 md:p-6 shadow">
        <div className="mb-6 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div>
            <h1 className="text-2xl font-bold">Student - Course Enrollment</h1>
            <p className="text-sm text-slate-600">
              Logged in as: {user?.email} ({user?.role})
            </p>
          </div>

          <button
            onClick={logout}
            className="rounded-lg bg-red-600 px-4 py-2 text-white hover:bg-red-500 transition"
          >
            Logout
          </button>
        </div>

        {errorMessage && (
          <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
            {errorMessage}
          </div>
        )}

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <section className="space-y-4 rounded-xl border border-slate-200 p-4">
            <h2 className="text-lg font-semibold">Available Courses</h2>

            <form onSubmit={handleSearchCourses} className="flex gap-2">
              <input
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                className="flex-1 rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Search by code or name"
              />
              <button className="rounded-lg bg-slate-900 px-4 py-2 text-white hover:bg-slate-700 transition">
                Search
              </button>
            </form>

            <div className="overflow-auto rounded-xl border border-slate-200">
              <table className="min-w-full text-sm">
                <thead className="bg-slate-50 text-left">
                  <tr>
                    <th className="px-3 py-2">Code</th>
                    <th className="px-3 py-2">Name</th>
                    <th className="px-3 py-2">Lecturer</th>
                    <th className="px-3 py-2">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {!loadingCourses &&
                    (coursesData?.content ?? []).map((course) => {
                      const loading = actionLoadingCourseId === course.id;

                      return (
                        <tr key={course.id} className="border-t border-slate-100">
                          <td className="px-3 py-2">{course.code}</td>
                          <td className="px-3 py-2">
                            <div className="font-medium">{course.name}</div>
                            {course.description && (
                              <div className="text-xs text-slate-500">{course.description}</div>
                            )}
                          </td>
                          <td className="px-3 py-2">{course.lecturerName}</td>
                          <td className="px-3 py-2">
                            <button
                              onClick={() => void handleEnrollToggle(course)}
                              disabled={loading}
                              className={`rounded px-3 py-1 text-white transition disabled:opacity-60 ${
                                course.enrolled
                                  ? 'bg-amber-600 hover:bg-amber-500'
                                  : 'bg-emerald-600 hover:bg-emerald-500'
                              }`}
                            >
                              {loading ? 'Processing...' : course.enrolled ? 'Cancel' : 'Enroll'}
                            </button>
                          </td>
                        </tr>
                      );
                    })}
                </tbody>
              </table>

              {loadingCourses && <p className="p-3 text-slate-500">Loading courses...</p>}
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => setCoursesPage((p) => Math.max(0, p - 1))}
                className="rounded bg-slate-200 px-3 py-1"
                disabled={coursesPage === 0}
              >
                Prev
              </button>
              <span className="text-sm">
                Page {(coursesData?.page ?? 0) + 1} / {Math.max(coursesData?.totalPages ?? 1, 1)}
              </span>
              <button
                onClick={() => setCoursesPage((p) => p + 1)}
                className="rounded bg-slate-200 px-3 py-1"
                disabled={coursesData?.last ?? true}
              >
                Next
              </button>
            </div>
          </section>

          <section className="space-y-4 rounded-xl border border-slate-200 p-4">
            <h2 className="text-lg font-semibold">My Enrollments</h2>

            <div className="overflow-auto rounded-xl border border-slate-200">
              <table className="min-w-full text-sm">
                <thead className="bg-slate-50 text-left">
                  <tr>
                    <th className="px-3 py-2">Course</th>
                    <th className="px-3 py-2">Lecturer</th>
                    <th className="px-3 py-2">Enrolled At</th>
                  </tr>
                </thead>
                <tbody>
                  {!loadingEnrollments &&
                    (enrollmentsData?.content ?? []).map((item) => (
                      <tr key={item.enrollmentId} className="border-t border-slate-100">
                        <td className="px-3 py-2">
                          <div className="font-medium">{item.courseCode} - {item.courseName}</div>
                          {item.courseDescription && (
                            <div className="text-xs text-slate-500">{item.courseDescription}</div>
                          )}
                        </td>
                        <td className="px-3 py-2">{item.lecturerName}</td>
                        <td className="px-3 py-2">{new Date(item.enrolledAt).toLocaleString()}</td>
                      </tr>
                    ))}
                </tbody>
              </table>

              {loadingEnrollments && <p className="p-3 text-slate-500">Loading enrollments...</p>}
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => setEnrollmentsPage((p) => Math.max(0, p - 1))}
                className="rounded bg-slate-200 px-3 py-1"
                disabled={enrollmentsPage === 0}
              >
                Prev
              </button>
              <span className="text-sm">
                Page {(enrollmentsData?.page ?? 0) + 1} / {Math.max(enrollmentsData?.totalPages ?? 1, 1)}
              </span>
              <button
                onClick={() => setEnrollmentsPage((p) => p + 1)}
                className="rounded bg-slate-200 px-3 py-1"
                disabled={enrollmentsData?.last ?? true}
              >
                Next
              </button>
            </div>
          </section>
        </div>

        <div className="mt-6 grid grid-cols-1 gap-6">
          <SubmissionPage />
          <StudentMaterialsPage />
        </div>
      </div>
    </div>
  );
};

export default StudentDashboardPage;