import axios from "axios";
import { XMLParser, XMLBuilder } from "fast-xml-parser";

const BASE_URL =
  import.meta.env.VITE_API_BASE || "http://localhost:8080/Service1"; // change if needed

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/xml" },
  // NOTE: in browsers you cannot bypass TLS checks programmatically. Accept self-signed cert in browser or use valid cert.
});

const parser = new XMLParser({
  ignoreAttributes: false,
  attributeNamePrefix: "@_",
});
const builder = new XMLBuilder({
  ignoreAttributes: false,
  attributeNamePrefix: "@_",
  format: true,
});

function toXml(obj, rootName = null) {
  // builder expects an object root; if rootName provided, wrap
  if (rootName) {
    const w = {};
    w[rootName] = obj;
    return builder.build(w);
  }
  return builder.build(obj);
}

function parseXml(xmlText) {
  try {
    return parser.parse(xmlText);
  } catch (e) {
    console.error("XML parse error", e);
    throw e;
  }
}

export async function searchCities(requestBody) {
  const xml = toXml(requestBody, "CitySearchRequest");
  const res = await axiosInstance.post("/cities/search", xml);
  return parseXml(res.data);
}

export async function addCity(cityInput) {
  const xml = toXml(cityInput, "CityInput");
  const res = await axiosInstance.post("/cities", xml);
  return parseXml(res.data);
}

export async function getCity(id) {
  const res = await axiosInstance.get(`/cities/${id}`);
  return parseXml(res.data);
}

export async function updateCity(id, cityInput) {
  const xml = toXml(cityInput, "CityInput");
  const res = await axiosInstance.put(`/cities/${id}`, xml);
  return parseXml(res.data);
}

export async function deleteCity(id) {
  const res = await axiosInstance.delete(`/cities/${id}`);
  return res.status;
}

export async function deleteByMeters(meters) {
  const res = await axiosInstance.delete(
    `/cities/by-meters-above-sea-level?meters=${encodeURIComponent(meters)}`
  );
  return res.status;
}

export async function byNamePrefix(prefix) {
  const res = await axiosInstance.get(
    `/cities/by-name-prefix?prefix=${encodeURIComponent(prefix)}`
  );
  return parseXml(res.data);
}

export async function byGovernorAge(age) {
  const res = await axiosInstance.get(
    `/cities/by-governor-age?age=${encodeURIComponent(age)}`
  );
  return parseXml(res.data);
}

export async function genocideCount(id1, id2, id3) {
  const res = await axiosInstance.post(`/genocide/count/${id1}/${id2}/${id3}`);
  return parseXml(res.data);
}

export async function genocideMoveToPoorest(id) {
  const res = await axiosInstance.post(`/genocide/move-to-poorest/${id}`);
  return parseXml(res.data);
}
