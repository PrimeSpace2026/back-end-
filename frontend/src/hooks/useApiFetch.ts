import { useServerStatus } from "../context/ServerStatusContext";

const API_BASE =
  import.meta.env.VITE_API_URL ?? "https://your-render-backend.onrender.com";

/**
 * Wrapper around fetch that blocks until the server is confirmed awake.
 * Use this instead of raw fetch() for ALL your API calls.
 *
 *   const { apiFetch } = useApiFetch();
 *   const tours = await apiFetch("/api/tours").then(r => r.json());
 */
export function useApiFetch() {
  const { isServerAwake } = useServerStatus();

  async function apiFetch(
    path: string,
    init?: RequestInit
  ): Promise<Response> {
    if (!isServerAwake) {
      throw new Error(
        "Server is not awake yet. Wrap your component tree in <ServerGate> to prevent premature API calls."
      );
    }
    return fetch(`${API_BASE}${path}`, init);
  }

  return { apiFetch, isServerAwake };
}
