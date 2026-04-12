import { useMatterportDeepLink } from "../hooks/useMatterportDeepLink";

interface Props {
  modelId: string;
  applicationKey: string;
}

export function MatterportCameraLink({ modelId, applicationKey }: Props) {
  const { iframeRef, deepLinkURL, copyLink, ready, showToast } =
    useMatterportDeepLink(applicationKey);

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

      {ready && deepLinkURL && (
        <div
          style={{
            marginTop: 8,
            display: "flex",
            gap: 8,
            alignItems: "center",
            position: "relative",
          }}
        >
          <input
            readOnly
            value={deepLinkURL}
            style={{
              flex: 1,
              padding: "6px 10px",
              borderRadius: 4,
              border: "1px solid #ccc",
              fontSize: 13,
            }}
            onFocus={(e) => e.target.select()}
          />
          <button
            onClick={copyLink}
            style={{
              padding: "6px 14px",
              borderRadius: 4,
              border: "none",
              background: "#4f46e5",
              color: "#fff",
              cursor: "pointer",
              fontSize: 13,
            }}
          >
            Copy Link
          </button>

          {showToast && (
            <span
              style={{
                position: "absolute",
                right: 0,
                top: -32,
                background: "#10b981",
                color: "#fff",
                padding: "4px 12px",
                borderRadius: 4,
                fontSize: 12,
                pointerEvents: "none",
              }}
            >
              Copied!
            </span>
          )}
        </div>
      )}
    </div>
  );
}
