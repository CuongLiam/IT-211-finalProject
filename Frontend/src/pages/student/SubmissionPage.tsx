import React, { useCallback, useEffect, useState } from 'react';
import { AxiosError } from 'axios';
import { submissionApi } from '../../api/submissionApi';
import { studentApi } from '../../api/studentApi';
import type { ApiErrorResponse, AssignmentItem, PageResponse, SubmissionItem } from '../../types/auth';

const SubmissionPage: React.FC = () => {
  const [assignmentId, setAssignmentId] = useState('');
  const [githubUrl, setGithubUrl] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [assignments, setAssignments] = useState<AssignmentItem[]>([]);

  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState('');

  const [page, setPage] = useState(0);
  const [history, setHistory] = useState<PageResponse<SubmissionItem> | null>(null);
  const [loadingHistory, setLoadingHistory] = useState(false);

  const extractError = (err: unknown): string => {
    const axiosErr = err as AxiosError<ApiErrorResponse>;
    return axiosErr.response?.data?.message ?? 'Request failed';
  };

  const loadHistory = useCallback(async () => {
    setLoadingHistory(true);
    try {
      const data = await submissionApi.listMySubmissions(page, 10);
      setHistory(data);
    } catch (err) {
      setMessage(extractError(err));
    } finally {
      setLoadingHistory(false);
    }
  }, [page]);

  const loadAssignments = useCallback(async () => {
    try {
      const data = await studentApi.listAssignments(0, 100);
      setAssignments(data.content);
    } catch (err) {
      setMessage(extractError(err));
    }
  }, []);

  useEffect(() => {
    queueMicrotask(() => {
      void loadHistory();
      void loadAssignments();
    });
  }, [loadHistory, loadAssignments]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage('');

    if (!file) {
      setMessage('Please choose file');
      return;
    }

    const id = Number(assignmentId);
    if (!id) {
      setMessage('Assignment ID is required');
      return;
    }

    setSubmitting(true);
    try {
      await submissionApi.submitAssignment({
        assignmentId: id,
        githubUrl,
        file,
      });
      setMessage('Submit assignment successful');
      setAssignmentId('');
      setGithubUrl('');
      setFile(null);
      await loadHistory();
    } catch (err) {
      setMessage(extractError(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-4 rounded-xl border border-slate-200 p-4">
      <h2 className="text-lg font-semibold">Assignment Submission</h2>

      {message && (
        <div className="rounded border border-slate-300 bg-slate-50 px-3 py-2 text-sm">
          {message}
        </div>
      )}

      <form onSubmit={handleSubmit} className="grid gap-3 md:grid-cols-4">
        <select
          value={assignmentId}
          onChange={(e) => setAssignmentId(e.target.value)}
          className="rounded border border-slate-300 px-3 py-2"
        >
          <option value="">Select assignment</option>
          {assignments.map((item) => (
            <option key={item.assignmentId} value={item.assignmentId}>
              {item.courseCode} - {item.title}
            </option>
          ))}
        </select>
        <input
          value={githubUrl}
          onChange={(e) => setGithubUrl(e.target.value)}
          className="rounded border border-slate-300 px-3 py-2"
          placeholder="GitHub URL"
        />
        <input
          type="file"
          onChange={(e) => setFile(e.target.files?.[0] ?? null)}
          className="rounded border border-slate-300 px-3 py-2"
          accept=".pdf,.doc,.docx,.ppt,.pptx"
        />
        <button
          type="submit"
          disabled={submitting}
          className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-500 disabled:opacity-60"
        >
          {submitting ? 'Submitting...' : 'Submit'}
        </button>
      </form>

      <div className="overflow-auto rounded border border-slate-200">
        <table className="min-w-full text-sm">
          <thead className="bg-slate-50 text-left">
            <tr>
              <th className="px-3 py-2">Assignment</th>
              <th className="px-3 py-2">Course</th>
              <th className="px-3 py-2">Status</th>
              <th className="px-3 py-2">File</th>
              <th className="px-3 py-2">Submitted At</th>
            </tr>
          </thead>
          <tbody>
            {!loadingHistory &&
              (history?.content ?? []).map((item) => (
                <tr key={item.submissionId} className="border-t border-slate-100">
                  <td className="px-3 py-2">{item.assignmentTitle}</td>
                  <td className="px-3 py-2">{item.courseCode}</td>
                  <td className="px-3 py-2">{item.status}</td>
                  <td className="px-3 py-2">
                    <a className="text-blue-600 underline" href={item.fileUrl} target="_blank" rel="noreferrer">
                      {item.originalFileName}
                    </a>
                  </td>
                  <td className="px-3 py-2">{new Date(item.submittedAt).toLocaleString()}</td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      <div className="flex items-center gap-2">
        <button
          onClick={() => setPage((p) => Math.max(0, p - 1))}
          className="rounded bg-slate-200 px-3 py-1"
          disabled={page === 0}
        >
          Prev
        </button>
        <span className="text-sm">
          Page {(history?.page ?? 0) + 1} / {Math.max(history?.totalPages ?? 1, 1)}
        </span>
        <button
          onClick={() => setPage((p) => p + 1)}
          className="rounded bg-slate-200 px-3 py-1"
          disabled={history?.last ?? true}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default SubmissionPage;
