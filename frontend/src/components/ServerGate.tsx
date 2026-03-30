import { useState, type ReactNode } from "react";
import { useServerStatus } from "../context/ServerStatusContext";
import { WakeUpScreen } from "./WakeUpScreen";

/**
 * Gates the app behind the wake-up screen.
 * While the server is cold, shows the branded loading screen.
 * Once awake, fades out the overlay and reveals children.
 */
export function ServerGate({ children }: { children: ReactNode }) {
  const { isServerAwake } = useServerStatus();
  const [showOverlay, setShowOverlay] = useState(true);

  // Once awake, start fade-out; remove overlay after transition ends
  const handleTransitionEnd = () => {
    if (isServerAwake) setShowOverlay(false);
  };

  return (
    <>
      {/* Always render children so they can mount, but hide interactivity */}
      <div
        className={
          isServerAwake ? "opacity-100 transition-opacity duration-700" : "pointer-events-none opacity-0"
        }
      >
        {children}
      </div>

      {/* Overlay */}
      {showOverlay && (
        <div
          onTransitionEnd={handleTransitionEnd}
          className={`transition-opacity duration-700 ${
            isServerAwake ? "pointer-events-none opacity-0" : "opacity-100"
          }`}
        >
          <WakeUpScreen />
        </div>
      )}
    </>
  );
}
