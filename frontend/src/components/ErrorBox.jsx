import React from "react";

export default function ErrorBox({ error }) {
  if (!error) return null;
  const message = error.message || JSON.stringify(error);
  return <div className="error">Ошибка: {message}</div>;
}
