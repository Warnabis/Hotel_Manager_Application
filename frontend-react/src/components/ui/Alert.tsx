interface Props {
  message: string;
  validationErrors?: Record<string, string>;
  onClose?: () => void;
}

export function ErrorAlert({ message, validationErrors, onClose }: Props) {
  return (
    <div className="alert alert-error">
      <div className="alert-header">
        <strong>{message}</strong>
        {onClose && (
          <button type="button" className="alert-close" onClick={onClose}>×</button>
        )}
      </div>
      {validationErrors && (
        <ul>
          {Object.entries(validationErrors).map(([k, v]) => (
            <li key={k}>{v}</li>
          ))}
        </ul>
      )}
    </div>
  );
}

export function SuccessAlert({ message }: { message: string }) {
  return <div className="alert alert-success">{message}</div>;
}
