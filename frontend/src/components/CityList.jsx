import React, { useEffect, useState } from "react";
import {
  searchCities,
  byNamePrefix,
  byGovernorAge,
  deleteCity,
} from "../api/citiesApi";
import ErrorBox from "./ErrorBox";

function toArray(maybe) {
  if (!maybe) return [];
  if (Array.isArray(maybe)) return maybe;
  return [maybe];
}

export default function CityList() {
  const [cities, setCities] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortField, setSortField] = useState("ID");
  const [sortDir, setSortDir] = useState("ASC");
  const [filterName, setFilterName] = useState("");
  const [filterGovernorAge, setFilterGovernorAge] = useState("");
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchPage();
  }, [page, size, sortField, sortDir]);

  async function fetchPage() {
    setLoading(true);
    setError(null);
    try {
      const request = {
        pagination: { page, size },
        sort: [{ field: sortField, direction: sortDir }],
      };
      const resp = await searchCities(request);
      // resp.cityPageResponse.pagination and .cities.city
      const pageObj = resp.cityPageResponse || resp.cityPageResponse || {};
      const citiesRaw = pageObj.cities ? pageObj.cities.city : [];
      setCities(toArray(citiesRaw));
    } catch (e) {
      console.error(e);
      setError({ message: e?.response?.data || e.message });
    } finally {
      setLoading(false);
    }
  }

  async function handlePrefixSearch() {
    setError(null);
    try {
      const resp = await byNamePrefix(filterName);
      const arr = resp.cities ? resp.cities.city : [];
      setCities(toArray(arr));
    } catch (e) {
      setError({ message: e?.response?.data || e.message });
    }
  }

  async function handleGovernorSearch() {
    setError(null);
    try {
      const resp = await byGovernorAge(filterGovernorAge);
      const arr = resp.cities ? resp.cities.city : [];
      setCities(toArray(arr));
    } catch (e) {
      setError({ message: e?.response?.data || e.message });
    }
  }

  async function handleDelete(id) {
    if (!confirm("Удалить город с id=" + id + "?")) return;
    try {
      await deleteCity(id);
      fetchPage();
    } catch (e) {
      setError({ message: e?.response?.data || e.message });
    }
  }
}
