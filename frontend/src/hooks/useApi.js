import { useEffect, useState } from "react";

export function useApi(requestFn, dependencies = []) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    setLoading(true);
    requestFn()
      .then((response) => active && setData(response?.data?.data ?? response?.data))
      .catch((err) => active && setError(err?.response?.data?.message || err.message))
      .finally(() => active && setLoading(false));

    return () => {
      active = false;
    };
  }, dependencies);

  return { data, loading, error };
}
