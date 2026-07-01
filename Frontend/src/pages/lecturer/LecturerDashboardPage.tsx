import React, { useCallback, useEffect, useState } from 'react';
import { AxiosError } from 'axios';
import { lecturerApi } from '../../api/lecturerApi';
import type {
  ApiErrorResponse,
  GradeItem,
  LectureMaterialItem,
  LecturerSubmissionItem,
  PageResponse,
} from '../../types/auth';

const LecturerDashboardPage: React.FC = () => {
  const [submissionData, setSubmissionData] = useState<PageResponse<LecturerSubmissionItem> | null>(null);
  const [gradeData, setGradeData] = useState<PageResponse<GradeItem> | null>(null);
  const [materialData, setMaterialData] = useState<PageResponse<LectureMaterialItem> | null>(null);

  const [submissionPage, setSubmissionPage] = useState(0);
  const [gradePage, setGradePage] = useState(0);
  const [materialPage, setMaterialPage] = useState(0);

  const [scoreMap, setScoreMap] = useState<Record<number, string>>({});
  const [feedbackMap, setFeedbackMap] = useState<Record<number, string>>({});

  const [courseId, setCourseId] = useState('');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [file, setFile] = useState<File | null>(null);

  const [message, setMessage] = useState('');

  const extractError = (err: unknown): string => {
    const axiosErr = err as AxiosError<ApiErrorResponse>;
    return axiosErr.response?.data?.message ?? 'Request failed';
  };

  const loadSubmissions = useCallback(async () => {
    try {
      const data = await lecturerApi.listSubmissions(submissionPage, 10);
      setSubmissionData(data);
    } catch (err) {
      setMessage(extractError(err));
    }
  }, [submissionPage]);

  const loadGrades = useCallback(async () => {
    try {
      const data = await lecturerApi.listGrades(gradePage, 10);
      setGradeData(data);
    } catch (err) {
      setMessage(extractError(err));
    }
  }, [gradePage]);

  const loadMaterials = useCallback(async () => {
    try {
      const data = await lecturerApi.listMaterials(materialPage, 10);
      setMaterialData(data);
    } catch (err) {
      setMessage(extractError(err));
    }
  }, [materialPage]);

  useEffect(() => {
    queueMicrotask(() => {
      void loadSubmissions();
      void loadGrades();
      void loadMaterials();
    });
  }, [loadSubmissions, loadGrades, loadMaterials]);

  const handleGrade = async (submissionId: number) => {
    const score = Number(scoreMap[submissionId]);
    if (Number.isNaN(score)) {
      setMessage('Score is required');
      return;
    }

    try {
      await lecturerApi.gradeSubmission(submissionId, {
        score,
        feedback: feedbackMap[submissionId] ?? '',
      });
      setMessage('Grade submitted');
      await Promise.all([loadSubmissions(), loadGrades()]);
    } catch (err) {
      setMessage(extractError(err));
    }
  };

  const handleUploadMaterial = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) {
      setMessage('Please choose file');
      return;
    }
    const id = Number(courseId);
    if (!id) {
      setMessage('Course ID is required');
      return;
    }

    try {
      await lecturerApi.uploadMaterial({
        courseId: id,
        title,
        description,
        file,
      });
      setCourseId('');
      setTitle('');
      setDescription('');
      setFile(null);
      setMessage('Upload material successful');
      await loadMaterials();
    } catch (err) {
      setMessage(extractError(err));
    }
  };

  return (
    <div className="min-h-screen bg-amber-50 p-4 md:p-6">
      <div className="mx-auto max-w-7xl space-y-6 rounded-2xl bg-white p-4 md:p-6 shadow">
        <h1 className="text-2xl font-bold">Lecturer - Grading & Materials</h1>

        {message && <div className="rounded bg-slate-100 px-3 py-2 text-sm">{message}</div>}

        <section className="space-y-3 rounded-xl border border-slate-200 p-4">
          <h2 className="text-lg font-semibold">Pending Submissions & Grading</h2>

          <div className="overflow-auto rounded border border-slate-200">
            <table className="min-w-full text-sm">
              <thead className="bg-slate-50 text-left">
                <tr>
                  <th className="px-3 py-2">Student</th>
                  <th className="px-3 py-2">Assignment</th>
                  <th className="px-3 py-2">Links</th>
                  <th className="px-3 py-2">Score</th>
                  <th className="px-3 py-2">Feedback</th>
                  <th className="px-3 py-2">Action</th>
                </tr>
              </thead>
              <tbody>
                {(submissionData?.content ?? []).map((item) => (
                  <tr key={item.submissionId} className="border-t border-slate-100">
                    <td className="px-3 py-2">{item.studentName}</td>
                    <td className="px-3 py-2">{item.assignmentTitle}</td>
                    <td className="px-3 py-2">
                      <div className="flex flex-col gap-1">
                        <a className="text-blue-600 underline" href={item.githubUrl} target="_blank" rel="noreferrer">GitHub</a>
                        <a className="text-blue-600 underline" href={item.fileUrl} target="_blank" rel="noreferrer">{item.originalFileName}</a>
                      </div>
                    </td>
                    <td className="px-3 py-2">
                      <input
                        value={scoreMap[item.submissionId] ?? ''}
                        onChange={(e) => setScoreMap((prev) => ({ ...prev, [item.submissionId]: e.target.value }))}
                        className="w-20 rounded border border-slate-300 px-2 py-1"
                        placeholder="0-10"
                      />
                    </td>
                    <td className="px-3 py-2">
                      <input
                        value={feedbackMap[item.submissionId] ?? ''}
                        onChange={(e) => setFeedbackMap((prev) => ({ ...prev, [item.submissionId]: e.target.value }))}
                        className="w-52 rounded border border-slate-300 px-2 py-1"
                        placeholder="Feedback"
                      />
                    </td>
                    <td className="px-3 py-2">
                      <button
                        onClick={() => void handleGrade(item.submissionId)}
                        className="rounded bg-emerald-600 px-3 py-1 text-white hover:bg-emerald-500"
                      >
                        Grade
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="space-y-3 rounded-xl border border-slate-200 p-4">
          <h2 className="text-lg font-semibold">Grade Summary</h2>

          <div className="overflow-auto rounded border border-slate-200">
            <table className="min-w-full text-sm">
              <thead className="bg-slate-50 text-left">
                <tr>
                  <th className="px-3 py-2">Student</th>
                  <th className="px-3 py-2">Assignment</th>
                  <th className="px-3 py-2">Score</th>
                  <th className="px-3 py-2">Feedback</th>
                </tr>
              </thead>
              <tbody>
                {(gradeData?.content ?? []).map((item) => (
                  <tr key={item.gradeId} className="border-t border-slate-100">
                    <td className="px-3 py-2">{item.studentName}</td>
                    <td className="px-3 py-2">{item.assignmentTitle}</td>
                    <td className="px-3 py-2">{item.score}</td>
                    <td className="px-3 py-2">{item.feedback}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="space-y-3 rounded-xl border border-slate-200 p-4">
          <h2 className="text-lg font-semibold">Lecture Materials</h2>

          <form onSubmit={handleUploadMaterial} className="grid gap-3 md:grid-cols-5">
            <input
              value={courseId}
              onChange={(e) => setCourseId(e.target.value)}
              className="rounded border border-slate-300 px-3 py-2"
              placeholder="Course ID"
            />
            <input
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="rounded border border-slate-300 px-3 py-2"
              placeholder="Title"
            />
            <input
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="rounded border border-slate-300 px-3 py-2"
              placeholder="Description"
            />
            <input
              type="file"
              onChange={(e) => setFile(e.target.files?.[0] ?? null)}
              className="rounded border border-slate-300 px-3 py-2"
              accept=".pdf,.doc,.docx,.ppt,.pptx"
            />
            <button className="rounded bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-500">
              Upload
            </button>
          </form>

          <div className="overflow-auto rounded border border-slate-200">
            <table className="min-w-full text-sm">
              <thead className="bg-slate-50 text-left">
                <tr>
                  <th className="px-3 py-2">Title</th>
                  <th className="px-3 py-2">Course</th>
                  <th className="px-3 py-2">File</th>
                </tr>
              </thead>
              <tbody>
                {(materialData?.content ?? []).map((item) => (
                  <tr key={item.id} className="border-t border-slate-100">
                    <td className="px-3 py-2">{item.title}</td>
                    <td className="px-3 py-2">{item.courseCode}</td>
                    <td className="px-3 py-2">
                      <a className="text-blue-600 underline" href={item.fileUrl} target="_blank" rel="noreferrer">
                        {item.originalFileName}
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </div>
  );
};

export default LecturerDashboardPage;