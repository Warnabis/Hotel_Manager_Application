import { useEffect, useRef, useState } from 'react';

interface Option {
  id: number;
  label: string;
}

interface Props {
  label: string;
  options: Option[];
  selected: number[];
  onChange: (ids: number[]) => void;
  loading?: boolean;
}

export function MultiSelect({ label, options, selected, onChange, loading }: Props) {
  const [open, setOpen] = useState(false);
  const rootRef = useRef<HTMLDivElement>(null);

  const toggle = (id: number) => {
    onChange(selected.includes(id) ? selected.filter((x) => x !== id) : [...selected, id]);
  };

  const selectedLabels = options.filter((o) => selected.includes(o.id)).map((o) => o.label);

  useEffect(() => {
    if (!open) return;

    const handlePointerDown = (event: MouseEvent) => {
      if (!rootRef.current?.contains(event.target as Node)) {
        setOpen(false);
      }
    };

    document.addEventListener('mousedown', handlePointerDown);
    return () => document.removeEventListener('mousedown', handlePointerDown);
  }, [open]);

  return (
    <div ref={rootRef} className={`form-group multi-select${open ? ' multi-select-open' : ''}`}>
      <label>{label}</label>
      <button
        type="button"
        className="multi-select-trigger"
        onClick={() => setOpen((prev) => !prev)}
        disabled={loading}
        aria-expanded={open}
      >
        {loading ? 'Загрузка...' : selectedLabels.length ? selectedLabels.join(', ') : 'Выберите...'}
      </button>
      {open && (
        <div className="multi-select-dropdown">
          <div className="multi-select-dropdown-header">
            <span>{label}</span>
            <button type="button" className="multi-select-close" onClick={() => setOpen(false)} aria-label="Закрыть">
              ×
            </button>
          </div>
          <div className="multi-select-options">
            {options.map((o) => (
              <label key={o.id} className="multi-select-option">
                <input
                  type="checkbox"
                  checked={selected.includes(o.id)}
                  onChange={() => toggle(o.id)}
                />
                {o.label}
              </label>
            ))}
            {options.length === 0 && <div className="text-muted multi-select-empty">Нет данных</div>}
          </div>
        </div>
      )}
    </div>
  );
}

interface LoadProps {
  label: string;
  loadOptions: () => Promise<Option[]>;
  selected: number[];
  onChange: (ids: number[]) => void;
}

export function AsyncMultiSelect({ label, loadOptions, selected, onChange }: LoadProps) {
  const [options, setOptions] = useState<Option[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadOptions().then(setOptions).finally(() => setLoading(false));
  }, [loadOptions]);

  return <MultiSelect label={label} options={options} selected={selected} onChange={onChange} loading={loading} />;
}
