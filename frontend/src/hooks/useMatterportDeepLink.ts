import { useEffect, useRef, useState, useCallback } from "react";

// ── Types ──────────────────────────────────────────────────────
interface DeepLinkParams {
  sweep: string;
  lookat: [number, number, number]; // [rx, ry, rz]
}

interface UseDeepLinkReturn {
  /** Ref to attach to the Matterport iframe */
  iframeRef: React.RefObject<HTMLIFrameElement | null>;
  /** Latest deep-link URL (updated on every sweep change) */
  deepLinkURL: string | null;
  /** Copy the current deep-link to clipboard; returns true on success */
  copyLink: () => Promise<boolean>;
  /** Whether the SDK is connected and ready */
  ready: boolean;
  /** Toast state: briefly true after a successful copy */
  showToast: boolean;
}

// ── URL helpers ────────────────────────────────────────────────

function buildDeepLink(params: DeepLinkParams): string {
  const url = new URL(window.location.origin + window.location.pathname);
  url.searchParams.set("sweep", params.sweep);
  url.searchParams.set(
    "lookat",
    params.lookat.map((v) => v.toFixed(2)).join(",")
  );
  return url.toString();
}

function parseDeepLink(): DeepLinkParams | null {
  const params = new URLSearchParams(window.location.search);
  const sweep = params.get("sweep");
  const lookat = params.get("lookat");
  if (!sweep || !lookat) return null;

  const parts = lookat.split(",").map(Number);
  if (parts.length !== 3 || parts.some(isNaN)) return null;

  return { sweep, lookat: [parts[0], parts[1], parts[2]] };
}

// ── Restore camera from URL params ────────────────────────────

async function restoreFromURL(sdk: any) {
  const saved = parseDeepLink();
  if (!saved) return;

  try {
    await sdk.Sweep.moveTo(saved.sweep, {
      rotation: { x: saved.lookat[0], y: saved.lookat[1] },
      transition: sdk.Sweep.Transition.FLY,
    });
    // Fine-tune rotation after the sweep transition completes
    await sdk.Camera.setRotation(
      { x: saved.lookat[0], y: saved.lookat[1] },
      { speed: 1.5 }
    );
  } catch (err) {
    console.error("[useDeepLink] failed to restore position:", err);
  }
}

// ── Hook ───────────────────────────────────────────────────────

export function useMatterportDeepLink(
  applicationKey: string
): UseDeepLinkReturn {
  const iframeRef = useRef<HTMLIFrameElement | null>(null);
  const sdkRef = useRef<any>(null);
  const [deepLinkURL, setDeepLinkURL] = useState<string | null>(null);
  const [ready, setReady] = useState(false);
  const [showToast, setShowToast] = useState(false);

  // ── Connect to SDK & subscribe to sweep changes ─────────────
  const connectSdk = useCallback(async () => {
    const iframe = iframeRef.current;
    if (!iframe || sdkRef.current) return;

    try {
      // @ts-expect-error – MP_SDK is injected by the Matterport showcase iframe
      const sdk = await window.MP_SDK.connect(iframe, applicationKey, "");
      sdkRef.current = sdk;
      setReady(true);

      // Restore deep-link on first load
      await restoreFromURL(sdk);

      // Listen to every sweep change (user clicks a puck)
      sdk.Sweep.current.subscribe(async (currentSweep: any) => {
        if (!currentSweep.sid && !currentSweep.id) return;

        const pose = await sdk.Camera.getPose();
        const params: DeepLinkParams = {
          sweep: currentSweep.sid ?? currentSweep.id,
          lookat: [pose.rotation.x, pose.rotation.y, pose.rotation.z ?? 0],
        };
        setDeepLinkURL(buildDeepLink(params));
      });
    } catch (err) {
      console.error("[useDeepLink] SDK connect failed:", err);
    }
  }, [applicationKey]);

  useEffect(() => {
    const iframe = iframeRef.current;
    if (!iframe) return;
    iframe.addEventListener("load", connectSdk);
    return () => iframe.removeEventListener("load", connectSdk);
  }, [connectSdk]);

  // ── Copy to clipboard with toast ────────────────────────────
  const copyLink = useCallback(async (): Promise<boolean> => {
    if (!deepLinkURL) return false;
    try {
      await navigator.clipboard.writeText(deepLinkURL);
      setShowToast(true);
      setTimeout(() => setShowToast(false), 2000);
      return true;
    } catch {
      return false;
    }
  }, [deepLinkURL]);

  return { iframeRef, deepLinkURL, copyLink, ready, showToast };
}
