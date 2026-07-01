import React, { useEffect, useMemo, useState } from 'react';
import { AxiosError } from 'axios';
import { adminApi } from '../../api/adminApi';
import { useAuthContext } from '../../context/useAuthContext';
import type {
  AdminCourse,
  AdminUser,
  ApiErrorResponse,
  CreateCoursePayload,
  CreateUserPayload,
  PageResponse,
  Role,
} from '../../types/auth';

type Tab = 'users' | 'courses';

const PAGE_SIZE = 8;

const UserManagementPage: React.FC = () => {
  const { user, logout } = useAuthContext();

  const [activeTab, setActiveTab] = useState<Tab>('users');
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const [userKeyword, setUserKeyword] = useState('');
  const [userRoleFilter, setUserRoleFilter] = useState<Role | 'ALL'>('ALL');
  const [userPage, setUserPage] = useState(0);
  const [userData, setUserData] = useState<PageResponse<AdminUser> | null>(null);

  const [courseKeyword, setCourseKeyword] = useState('');
  const [coursePage, setCoursePage] = useState(0);
  const [courseData, setCourseData] = useState<PageResponse<AdminCourse> | null>(null);

  const [userForm, setUserForm] = useState<CreateUserPayload>({
    fullName: '',
    email: '',
    password: '',
    role: 'LECTURER',
    enabled: true,
  });

  const [courseForm, setCourseForm] = useState<CreateCoursePayload>({
    code: '',
    name: '',
    description: '',
    lecturerId: 0,
  });

  const [submitting, setSubmitting] = useState(false);

  const lecturerOptions = useMemo(() => {
    const users = userData?.content ?? [];
    return users.filter((u) => u.role === 'LECTURER');
  }, [userData]);

  const extractErrorMessage = (err: unknown): string => {
    const axiosErr = err as AxiosError<ApiErrorResponse>;
    return axiosErr.response?.data?.message ?? 'Something went wrong';
  };

  const loadUsers = async () => {
    setLoading(true);
    setErrorMessage('');
    try {
      const data = await adminApi.searchUsers({
        keyword: userKeyword,
        role: userRoleFilter === 'ALL' ? undefined : userRoleFilter,
        page: userPage,
        size: PAGE_SIZE,
      });
      setUserData(data);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const loadCourses = async () => {
    setLoading(true);
    setErrorMessage('');
    try {
      const data = await adminApi.searchCourses({
        keyword: courseKeyword,
        page: coursePage,
        size: PAGE_SIZE,
      });
      setCourseData(data);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (activeTab === 'users') {
      void loadUsers();
    }
  }, [activeTab, userPage]);

  useEffect(() => {
    if (activeTab === 'courses') {
      void loadCourses();
    }
  }, [activeTab, coursePage]);

  const handleSearchUsers = async (e: React.FormEvent) => {
    e.preventDefault();
    setUserPage(0);
    await loadUsers();
  };

  const handleSearchCourses = async (e: React.FormEvent) => {
    e.preventDefault();
    setCoursePage(0);
    await loadCourses();
  };

  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setErrorMessage('');
    try {
      await adminApi.createUser(userForm);
      setUserForm({
        fullName: '',
        email: '',
        password: '',
        role: 'LECTURER',
        enabled: true,
      });
      await loadUsers();
    } catch (err) {
      setErrorMessage(extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleCreateCourse = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setErrorMessage('');
    try {
      if (!courseForm.lecturerId) {
        setErrorMessage('Please select lecturerId');
        setSubmitting(false);
        return;
      }
      await adminApi.createCourse(courseForm);
      setCourseForm({
        code: '',
        name: '',
        description: '',
        lecturerId: 0,
      });
      await loadCourses();
    } catch (err) {
      setErrorMessage(extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteUser = async (id: number) => {
    if (!window.confirm(`Delete user #${id}?`)) return;
    try {
      await adminApi.deleteUser(id);
      await loadUsers();
    } catch (err) {
      setErrorMessage(extractErrorMessage(err));
    }
  };

  const handleDeleteCourse = async (id: number) => {
    if (!window.confirm(`Delete course #${id}?`)) return;
    try {
      await adminApi.deleteCourse(id);
      await loadCourses();
    } catch (err) {
      setErrorMessage(extractErrorMessage(err));
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 p-4 md:p-6">
      <div className="mx-auto max-w-7xl rounded-2xl bg-white p-4 md:p-6 shadow">
        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold">Admin Panel</h1>
            <p className="text-slate-600 text-sm">
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

        <div className="mb-4 flex gap-2">
          <button
            onClick={() => setActiveTab('users')}
            className={`rounded-lg px-4 py-2 text-sm font-medium transition ${
              activeTab === 'users' ? 'bg-slate-900 text-white' : 'bg-slate-200 text-slate-800'
            }`}
          >
            Users
          </button>
          <button
            onClick={() => setActiveTab('courses')}
            className={`rounded-lg px-4 py-2 text-sm font-medium transition ${
              activeTab === 'courses' ? 'bg-slate-900 text-white' : 'bg-slate-200 text-slate-800'
            }`}
          >
            Courses
          </button>
        </div>

        {errorMessage && (
          <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
            {errorMessage}
          </div>
        )}

        {activeTab === 'users' && (
          <div className="space-y-6">
            <form onSubmit={handleSearchUsers} className="grid grid-cols-1 md:grid-cols-4 gap-3">
              <input
                value={userKeyword}
                onChange={(e) => setUserKeyword(e.target.value)}
                className="rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Search name/email"
              />
              <select
                value={userRoleFilter}
                onChange={(e) => setUserRoleFilter(e.target.value as Role | 'ALL')}
                className="rounded-lg border border-slate-300 px-3 py-2"
              >
                <option value="ALL">All roles</option>
                <option value="ADMIN">ADMIN</option>
                <option value="LECTURER">LECTURER</option>
                <option value="STUDENT">STUDENT</option>
              </select>
              <button className="rounded-lg bg-slate-900 px-4 py-2 text-white hover:bg-slate-700 transition">
                Search
              </button>
            </form>

            <form onSubmit={handleCreateUser} className="grid grid-cols-1 md:grid-cols-6 gap-3">
              <input
                value={userForm.fullName}
                onChange={(e) => setUserForm((p) => ({ ...p, fullName: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Full name"
                required
              />
              <input
                type="email"
                value={userForm.email}
                onChange={(e) => setUserForm((p) => ({ ...p, email: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Email"
                required
              />
              <input
                type="password"
                value={userForm.password}
                onChange={(e) => setUserForm((p) => ({ ...p, password: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Password"
                required
              />
              <select
                value={userForm.role}
                onChange={(e) => setUserForm((p) => ({ ...p, role: e.target.value as Role }))}
                className="rounded-lg border border-slate-300 px-3 py-2"
              >
                <option value="ADMIN">ADMIN</option>
                <option value="LECTURER">LECTURER</option>
                <option value="STUDENT">STUDENT</option>
              </select>
              <select
                value={userForm.enabled ? 'true' : 'false'}
                onChange={(e) => setUserForm((p) => ({ ...p, enabled: e.target.value === 'true' }))}
                className="rounded-lg border border-slate-300 px-3 py-2"
              >
                <option value="true">Enabled</option>
                <option value="false">Disabled</option>
              </select>
              <button
                type="submit"
                disabled={submitting}
                className="rounded-lg bg-emerald-600 px-4 py-2 text-white hover:bg-emerald-500 transition disabled:opacity-60"
              >
                {submitting ? 'Creating...' : 'Create User'}
              </button>
            </form>

            <div className="overflow-auto rounded-xl border border-slate-200">
              <table className="min-w-full text-sm">
                <thead className="bg-slate-50 text-left">
                  <tr>
                    <th className="px-3 py-2">ID</th>
                    <th className="px-3 py-2">Name</th>
                    <th className="px-3 py-2">Email</th>
                    <th className="px-3 py-2">Role</th>
                    <th className="px-3 py-2">Enabled</th>
                    <th className="px-3 py-2">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {!loading &&
                    (userData?.content ?? []).map((u) => (
                      <tr key={u.id} className="border-t border-slate-100">
                        <td className="px-3 py-2">{u.id}</td>
                        <td className="px-3 py-2">{u.fullName}</td>
                        <td className="px-3 py-2">{u.email}</td>
                        <td className="px-3 py-2">{u.role}</td>
                        <td className="px-3 py-2">{u.enabled ? 'Yes' : 'No'}</td>
                        <td className="px-3 py-2">
                          <button
                            onClick={() => void handleDeleteUser(u.id)}
                            className="rounded bg-red-600 px-2 py-1 text-white hover:bg-red-500"
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>
              {loading && <p className="p-3 text-slate-500">Loading users...</p>}
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => setUserPage((p) => Math.max(0, p - 1))}
                className="rounded bg-slate-200 px-3 py-1"
                disabled={userPage === 0}
              >
                Prev
              </button>
              <span className="text-sm">
                Page {(userData?.page ?? 0) + 1} / {Math.max(userData?.totalPages ?? 1, 1)}
              </span>
              <button
                onClick={() => setUserPage((p) => p + 1)}
                className="rounded bg-slate-200 px-3 py-1"
                disabled={userData?.last ?? true}
              >
                Next
              </button>
            </div>
          </div>
        )}

        {activeTab === 'courses' && (
          <div className="space-y-6">
            <form onSubmit={handleSearchCourses} className="grid grid-cols-1 md:grid-cols-3 gap-3">
              <input
                value={courseKeyword}
                onChange={(e) => setCourseKeyword(e.target.value)}
                className="rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Search code/name"
              />
              <button className="rounded-lg bg-slate-900 px-4 py-2 text-white hover:bg-slate-700 transition">
                Search
              </button>
            </form>

            <form onSubmit={handleCreateCourse} className="grid grid-cols-1 md:grid-cols-5 gap-3">
              <input
                value={courseForm.code}
                onChange={(e) => setCourseForm((p) => ({ ...p, code: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Course code"
                required
              />
              <input
                value={courseForm.name}
                onChange={(e) => setCourseForm((p) => ({ ...p, name: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Course name"
                required
              />
              <input
                value={courseForm.description ?? ''}
                onChange={(e) => setCourseForm((p) => ({ ...p, description: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Description"
              />
              <select
                value={courseForm.lecturerId || ''}
                onChange={(e) => setCourseForm((p) => ({ ...p, lecturerId: Number(e.target.value) }))}
                className="rounded-lg border border-slate-300 px-3 py-2"
              >
                <option value="">Select lecturer</option>
                {lecturerOptions.map((lec) => (
                  <option key={lec.id} value={lec.id}>
                    {lec.fullName} ({lec.email})
                  </option>
                ))}
              </select>
              <button
                type="submit"
                disabled={submitting}
                className="rounded-lg bg-emerald-600 px-4 py-2 text-white hover:bg-emerald-500 transition disabled:opacity-60"
              >
                {submitting ? 'Creating...' : 'Create Course'}
              </button>
            </form>

            <div className="overflow-auto rounded-xl border border-slate-200">
              <table className="min-w-full text-sm">
                <thead className="bg-slate-50 text-left">
                  <tr>
                    <th className="px-3 py-2">ID</th>
                    <th className="px-3 py-2">Code</th>
                    <th className="px-3 py-2">Name</th>
                    <th className="px-3 py-2">Lecturer</th>
                    <th className="px-3 py-2">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {!loading &&
                    (courseData?.content ?? []).map((c) => (
                      <tr key={c.id} className="border-t border-slate-100">
                        <td className="px-3 py-2">{c.id}</td>
                        <td className="px-3 py-2">{c.code}</td>
                        <td className="px-3 py-2">{c.name}</td>
                        <td className="px-3 py-2">{c.lecturerName}</td>
                        <td className="px-3 py-2">
                          <button
                            onClick={() => void handleDeleteCourse(c.id)}
                            className="rounded bg-red-600 px-2 py-1 text-white hover:bg-red-500"
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>
              {loading && <p className="p-3 text-slate-500">Loading courses...</p>}
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => setCoursePage((p) => Math.max(0, p - 1))}
                className="rounded bg-slate-200 px-3 py-1"
                disabled={coursePage === 0}
              >
                Prev
              </button>
              <span className="text-sm">
                Page {(courseData?.page ?? 0) + 1} / {Math.max(courseData?.totalPages ?? 1, 1)}
              </span>
              <button
                onClick={() => setCoursePage((p) => p + 1)}
                className="rounded bg-slate-200 px-3 py-1"
                disabled={courseData?.last ?? true}
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserManagementPage;