import axios from "axios";
import { XMLParser, XMLBuilder } from "fast-xml-parser";

const BASE_URL =
  import.meta.env.VITE_API_BASE || "https://158.160.140.50:8545/Service1";

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/xml" },
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

export function toXml(obj, rootName = null) {
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
