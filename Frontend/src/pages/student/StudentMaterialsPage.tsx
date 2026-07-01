import React, { useCallback, useEffect, useState } from 'react';
import { AxiosError } from 'axios';
import { learningApi } from '../../api/learningApi';
import type { ApiErrorResponse, LectureMaterialItem, PageResponse } from '../../types/auth';

const StudentMaterialsPage: React.FC = () => {
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<LectureMaterialItem> | null>(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const extractError = (err: unknown): string => {
    const axiosErr = err as AxiosError<ApiErrorResponse>;
    return axiosErr.response?.data?.message ?? 'Request failed';
  };

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await learningApi.listMyMaterials(page, 10);
      setData(res);
    } catch (err) {
      setMessage(extractError(err));
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    queueMicrotask(() => {
      void load();
    });
  }, [load]);

  return (
    <div className="space-y-4 rounded-xl border border-slate-200 p-4">
      <h2 className="text-lg font-semibold">Lecture Materials</h2>

      {message && <div className="rounded bg-red-50 px-3 py-2 text-sm text-red-700">{message}</div>}

      <div className="overflow-auto rounded border border-slate-200">
        <table className="min-w-full text-sm">
          <thead className="bg-slate-50 text-left">
            <tr>
              <th className="px-3 py-2">Title</th>
              <th className="px-3 py-2">Course</th>
              <th className="px-3 py-2">Lecturer</th>
              <th className="px-3 py-2">File</th>
            </tr>
          </thead>
          <tbody>
            {!loading &&
              (data?.content ?? []).map((item) => (
                <tr key={item.id} className="border-t border-slate-100">
                  <td className="px-3 py-2">{item.title}</td>
                  <td className="px-3 py-2">{item.courseCode}</td>
                  <td className="px-3 py-2">{item.lecturerName}</td>
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

      <div className="flex items-center gap-2">
        <button
          onClick={() => setPage((p) => Math.max(0, p - 1))}
          className="rounded bg-slate-200 px-3 py-1"
          disabled={page === 0}
        >
          Prev
        </button>
        <span className="text-sm">
          Page {(data?.page ?? 0) + 1} / {Math.max(data?.totalPages ?? 1, 1)}
        </span>
        <button
          onClick={() => setPage((p) => p + 1)}
          className="rounded bg-slate-200 px-3 py-1"
          disabled={data?.last ?? true}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default StudentMaterialsPage;
