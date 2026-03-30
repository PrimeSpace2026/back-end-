import { useServerStatus } from "../context/ServerStatusContext";

export function WakeUpScreen() {
  const { elapsedSeconds } = useServerStatus();

  const progress = Math.min((elapsedSeconds / 60) * 100, 95);

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-gradient-to-br from-slate-950 via-slate-900 to-indigo-950">
      {/* Ambient glow */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute -top-40 -left-40 h-80 w-80 rounded-full bg-indigo-500/10 blur-3xl" />
        <div className="absolute -bottom-40 -right-40 h-96 w-96 rounded-full bg-purple-500/10 blur-3xl" />
      </div>

      <div className="relative flex w-full max-w-md flex-col items-center gap-8 px-6 text-center">
        {/* Logo / Brand */}
        <div className="flex items-center gap-3">
          <div className="relative flex h-12 w-12 items-center justify-center">
            {/* Spinner ring */}
            <div className="absolute inset-0 animate-spin rounded-full border-2 border-transparent border-t-indigo-400" />
            {/* Inner dot */}
            <div className="h-3 w-3 animate-pulse rounded-full bg-indigo-400" />
          </div>
          <span className="text-2xl font-bold tracking-tight text-white">
            PrimeSpace
          </span>
        </div>

        {/* Status text */}
        <div className="space-y-2">
          <h2 className="text-lg font-semibold text-white/90">
            Waking up the cloud server&hellip;
          </h2>
          <p className="text-sm leading-relaxed text-slate-400">
            Our server sleeps when idle to save resources. The first load
            typically takes <span className="text-indigo-400 font-medium">~60 seconds</span>.
            Subsequent visits will be instant.
          </p>
        </div>

        {/* Progress bar */}
        <div className="w-full space-y-2">
          <div className="h-1.5 w-full overflow-hidden rounded-full bg-slate-800">
            <div
              className="h-full rounded-full bg-gradient-to-r from-indigo-500 to-purple-500 transition-all duration-1000 ease-out"
              style={{ width: `${progress}%` }}
            />
          </div>
          {/* Timer */}
          <div className="flex items-center justify-between text-xs text-slate-500">
            <span>Server warming up</span>
            <span className="tabular-nums font-mono text-indigo-400">
              {elapsedSeconds}s
            </span>
          </div>
        </div>

        {/* Elapsed badge */}
        {elapsedSeconds > 15 && (
          <p className="animate-fade-in text-xs text-slate-500">
            Almost there — hang tight!
          </p>
        )}
      </div>
    </div>
  );
}
