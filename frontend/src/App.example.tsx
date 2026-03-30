/**
 * Example App.tsx showing how to integrate the wake-up system.
 *
 * Wrap your entire app in <ServerStatusProvider> + <ServerGate>.
 * All child components can call useServerStatus() to check if the
 * server is awake before making API requests.
 *
 * Copy the pieces you need into your existing App.tsx / Layout.tsx.
 */

import { ServerStatusProvider } from "./context/ServerStatusContext";
import { ServerGate } from "./components/ServerGate";

function App() {
  return (
    <ServerStatusProvider>
      <ServerGate>
        {/* -------- Your existing app goes here -------- */}
        <div className="min-h-screen bg-slate-950 text-white">
          <h1 className="p-8 text-3xl font-bold">PrimeSpace</h1>
          <p className="px-8 text-slate-400">
            Your app content loads here after the server wakes up.
          </p>
        </div>
        {/* --------------------------------------------- */}
      </ServerGate>
    </ServerStatusProvider>
  );
}

export default App;
