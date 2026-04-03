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
import { AddTourForm } from "./components/AddTourForm";

function App() {
  return (
    <ServerStatusProvider>
      <ServerGate>
        {/* -------- Your existing app goes here -------- */}
        <AddTourForm />
        {/* --------------------------------------------- */}
      </ServerGate>
    </ServerStatusProvider>
  );
}

export default App;
