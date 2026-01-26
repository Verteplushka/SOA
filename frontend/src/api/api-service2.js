import axios from "axios";
import { XMLParser, XMLBuilder } from "fast-xml-parser";

const BASE_URL =
  import.meta.env.VITE_GENOCIDE_API_BASE ||
  "https://158.160.138.235:8766/REST_layer/genocide";

const axiosInstance = axios.create({
  baseURL: BASE_URL,
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
  const res = await axiosInstance.post(`/count/${id1}/${id2}/${id3}`);
  return parseXml(res.data);
}

export async function genocideMoveToPoorest(id) {
  const res = await axiosInstance.post(`/move-to-poorest/${id}`);
  return parseXml(res.data);
}
