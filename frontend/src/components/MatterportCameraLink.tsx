import { useEffect, useRef, useState, useCallback } from "react";

/**
 * Camera pose stored in / read from URL search params.
 */
interface CameraPose {
  sweepId: string;
  px: number;
  py: number;
  pz: number;
  rx: number;
  ry: number;
}

/** Parse camera params from the current URL (if present). */
function parseCameraFromURL(): CameraPose | null {
  const params = new URLSearchParams(window.location.search);
  const sweepId = params.get("sweepId");
  const px = params.get("px");
  const py = params.get("py");
  const pz = params.get("pz");
  const rx = params.get("rx");
  const ry = params.get("ry");

  if (!sweepId || !px || !py || !pz || !rx || !ry) return null;

  return {
    sweepId,
    px: parseFloat(px),
    py: parseFloat(py),
    pz: parseFloat(pz),
    rx: parseFloat(rx),
    ry: parseFloat(ry),
  };
}

/** Build a shareable URL with camera params. */
function buildCameraURL(pose: CameraPose): string {
  const url = new URL(window.location.href.split("?")[0]);
  url.searchParams.set("sweepId", pose.sweepId);
  url.searchParams.set("px", pose.px.toFixed(3));
  url.searchParams.set("py", pose.py.toFixed(3));
  url.searchParams.set("pz", pose.pz.toFixed(3));
  url.searchParams.set("rx", pose.rx.toFixed(3));
  url.searchParams.set("ry", pose.ry.toFixed(3));
  return url.toString();
}

interface Props {
  modelId: string;       // Matterport model SID e.g. "SxQL3iGyoDo"
  applicationKey: string; // Your Matterport SDK key
}

export function MatterportCameraLink({ modelId, applicationKey }: Props) {
  const iframeRef = useRef<HTMLIFrameElement>(null);
  const sdkRef = useRef<any>(null);
  const [shareURL, setShareURL] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);

  // ── 1. Connect to the SDK once the iframe loads ──────────────
  const connectSdk = useCallback(async () => {
    const iframe = iframeRef.current;
    if (!iframe) return;

    try {
      // @ts-expect-error – global injected by the Matterport embed script
      const sdk = await window.MP_SDK.connect(iframe, applicationKey, "");
      sdkRef.current = sdk;

      // ── 2. If URL has camera params, navigate there ──────────
      const saved = parseCameraFromURL();
      if (saved) {
        await moveCameraTo(sdk, saved);
      }
    } catch (err) {
      console.error("Matterport SDK connect failed:", err);
    }
  }, [applicationKey]);

  useEffect(() => {
    const iframe = iframeRef.current;
    if (!iframe) return;
    iframe.addEventListener("load", connectSdk);
    return () => iframe.removeEventListener("load", connectSdk);
  }, [connectSdk]);

  // ── 3. Capture current pose & build URL ──────────────────────
  const captureAndShare = async () => {
    const sdk = sdkRef.current;
    if (!sdk) return;

    const pose = await sdk.Camera.getPose();
    const sweep = await sdk.Sweep.current;

    const cameraPose: CameraPose = {
      sweepId: sweep.sid ?? sweep.id ?? "",
      px: pose.position.x,
      py: pose.position.y,
      pz: pose.position.z,
      rx: pose.rotation.x,
      ry: pose.rotation.y,
    };

    const url = buildCameraURL(cameraPose);
    setShareURL(url);
    setCopied(false);
  };

  const copyToClipboard = async () => {
    if (!shareURL) return;
    await navigator.clipboard.writeText(shareURL);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  // ── Render ───────────────────────────────────────────────────
  const showcaseURL =
    `https://my.matterport.com/show/?m=${encodeURIComponent(modelId)}` +
    `&play=1&qs=1&applicationKey=${encodeURIComponent(applicationKey)}`;

  return (
    <div style={{ width: "100%", position: "relative" }}>
      <iframe
        ref={iframeRef}
        src={showcaseURL}
        width="100%"
        height="500"
        frameBorder="0"
        allow="xr-spatial-tracking"
        allowFullScreen
      />

      <div style={{ marginTop: 8, display: "flex", gap: 8, alignItems: "center" }}>
        <button onClick={captureAndShare}>📍 Capture Camera Position</button>

        {shareURL && (
          <>
            <input
              readOnly
              value={shareURL}
              style={{ flex: 1, padding: "4px 8px" }}
              onFocus={(e) => e.target.select()}
            />
            <button onClick={copyToClipboard}>
              {copied ? "✓ Copied" : "Copy Link"}
            </button>
          </>
        )}
      </div>
    </div>
  );
}

// ── Helper: Move camera to a saved pose ────────────────────────
async function moveCameraTo(sdk: any, pose: CameraPose) {
  try {
    // First move to the sweep point
    await sdk.Sweep.moveTo(pose.sweepId, {
      rotation: { x: pose.rx, y: pose.ry },
      transition: sdk.Sweep.Transition.FLY,
    });

    // Then fine-tune the camera rotation
    await sdk.Camera.setRotation({ x: pose.rx, y: pose.ry }, { speed: 2 });
  } catch (err) {
    console.error("Failed to restore camera position:", err);
  }
}
