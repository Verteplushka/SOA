import axios from "axios";
import { XMLParser, XMLBuilder } from "fast-xml-parser";

const BASE_URL =
  import.meta.env.VITE_GENOCIDE_API_BASE ||
  "http://localhost:8081/service2/genocide";

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

function toXml(obj, rootName = null) {
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

export async function genocideCount(id1, id2, id3) {
  const res = await axiosInstance.post(`/genocide/count/${id1}/${id2}/${id3}`);
  return parseXml(res.data);
}

export async function genocideMoveToPoorest(id) {
  const res = await axiosInstance.post(`/genocide/move-to-poorest/${id}`);
  return parseXml(res.data);
}
