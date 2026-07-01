type WorkoutFormProps = {
  formData: {
    name: string;
    reps: string;
    sets: string;
    weights: string;
  };
  onChange: (field: string, value: string) => void;
  onRegister: () => void;
  onGetAll: () => void;
  isLoading: boolean;
};

export function WorkoutForm({ formData, onChange, onRegister, onGetAll, isLoading }: WorkoutFormProps) {
  return (
    <div>
      <input
        placeholder="種目名"
        type="text"
        value={formData.name}
        onChange={(e) => onChange("name", e.target.value)}
      />
      <input
        placeholder="回数"
        type="number"
        value={formData.reps}
        onChange={(e) => onChange("reps", e.target.value)}
      />
      <input
        placeholder="セット数"
        type="number"
        value={formData.sets}
        onChange={(e) => onChange("sets", e.target.value)}
      />
      <input
        placeholder="重さ"
        type="number"
        value={formData.weights}
        onChange={(e) => onChange("weights", e.target.value)}
      />
      <div className="button-group">
        <button onClick={onRegister} disabled={isLoading}>
          {isLoading ? "処理中..." : "登録"}
        </button>
        <button onClick={onGetAll} disabled={isLoading}>
          {isLoading ? "処理中..." : "全件取得"}
        </button>
      </div>
    </div>
  );
}