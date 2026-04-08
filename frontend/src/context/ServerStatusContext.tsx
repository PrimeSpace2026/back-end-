import {
  createContext,
  useContext,
  useEffect,
  useState,
  useCallback,
  useRef,
  type ReactNode,
} from "react";

interface ServerStatusContextType {
  isServerAwake: boolean;
  elapsedSeconds: number;
}

const ServerStatusContext = createContext<ServerStatusContextType>({
  isServerAwake: false,
  elapsedSeconds: 0,
});

export const useServerStatus = () => useContext(ServerStatusContext);

const API_BASE =
  import.meta.env.VITE_API_URL ?? "https://your-render-backend.onrender.com";

const WAKE_URL = `${API_BASE}/api/wake`;
const PING_INTERVAL_MS = 4000; // ping every 4 seconds
const PING_TIMEOUT_MS = 5000; // abort individual ping after 5s
const KEEPALIVE_INTERVAL_MS = 600_000; // background ping every 10 min once awake

export function ServerStatusProvider({ children }: { children: ReactNode }) {
  const [isServerAwake, setIsServerAwake] = useState(false);
  const [elapsedSeconds, setElapsedSeconds] = useState(0);
  const startTime = useRef(Date.now());
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const ping = useCallback(async (): Promise<boolean> => {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), PING_TIMEOUT_MS);
    try {
      const res = await fetch(WAKE_URL, {
        method: "GET",
        signal: controller.signal,
        cache: "no-store",
      });
      clearTimeout(timeout);
      return res.ok;
    } catch {
      clearTimeout(timeout);
      return false;
    }
  }, []);

  useEffect(() => {
    let cancelled = false;

    // Elapsed timer — ticks every second while waking
    timerRef.current = setInterval(() => {
      setElapsedSeconds(Math.floor((Date.now() - startTime.current) / 1000));
    }, 1000);

    // Initial ping
    (async () => {
      const ok = await ping();
      if (!cancelled && ok) {
        setIsServerAwake(true);
        return;
      }

      // Start interval pinging
      intervalRef.current = setInterval(async () => {
        const ok = await ping();
        if (ok && !cancelled) {
          setIsServerAwake(true);
        }
      }, PING_INTERVAL_MS);
    })();

    return () => {
      cancelled = true;
      if (intervalRef.current) clearInterval(intervalRef.current);
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [ping]);

  const keepAliveRef = useRef<ReturnType<typeof setInterval> | null>(null);

  // Once awake, clear wake-up intervals and start background keep-alive
  useEffect(() => {
    if (isServerAwake) {
      if (intervalRef.current) clearInterval(intervalRef.current);
      if (timerRef.current) clearInterval(timerRef.current);

      // Background keep-alive: ping every 10 min to prevent Render shutdown
      keepAliveRef.current = setInterval(() => {
        fetch(WAKE_URL, { method: "GET", cache: "no-store" }).catch(() => {});
      }, KEEPALIVE_INTERVAL_MS);
    }
    return () => {
      if (keepAliveRef.current) clearInterval(keepAliveRef.current);
    };
  }, [isServerAwake]);

  return (
    <ServerStatusContext.Provider value={{ isServerAwake, elapsedSeconds }}>
      {children}
    </ServerStatusContext.Provider>
  );
}
