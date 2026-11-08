export type WorkoutSetFormData = {
  reps: string;
  weights: string;
};

export type WorkoutFormData = {
  name: string;
  sets: WorkoutSetFormData[];
};

type WorkoutFormProps = {
  formData: WorkoutFormData;
  onNameChange: (value: string) => void;
  onSetChange: (
    index: number,
    field: keyof WorkoutSetFormData,
    value: string,
  ) => void;
  onAddSet: () => void;
  onRemoveSet: (index: number) => void;
  onRegister: () => void;
  onGetAll: () => void;
  isLoading: boolean;
};

export function WorkoutForm({
  formData,
  onNameChange,
  onSetChange,
  onAddSet,
  onRemoveSet,
  onRegister,
  onGetAll,
  isLoading,
}: WorkoutFormProps) {
  return (
    <div>
      <input
        placeholder="種目名"
        type="text"
        value={formData.name}
        onChange={(e) => onNameChange(e.target.value)}
      />

      {formData.sets.map((set, index) => (
        <div key={index}>
          <span>{index + 1}セット目</span>
          <input
            placeholder="重さ"
            type="number"
            value={set.weights}
            onChange={(e) => onSetChange(index, "weights", e.target.value)}
          />
          <input
            placeholder="回数"
            type="number"
            value={set.reps}
            onChange={(e) => onSetChange(index, "reps", e.target.value)}
          />
          <button
            onClick={() => onRemoveSet(index)}
            disabled={isLoading || formData.sets.length <= 1}
          >
            削除
          </button>
        </div>
      ))}

      <div className="button-group">
        <button onClick={onAddSet} disabled={isLoading}>
          セット追加
        </button>
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
