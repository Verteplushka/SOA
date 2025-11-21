export const governmentMap = {
  ALL: "ЛЮБАЯ",
  DIARCHY: "ДИАРХИЯ",
  KRITARCHY: "КРИТАРХИЯ",
  REPUBLIC: "РЕСПУБЛИКА",
};

export const reverseGovernmentMap = Object.fromEntries(
  Object.entries(governmentMap).map(([key, value]) => [value, key])
);

export const localizeGovernment = (value) => governmentMap[value] || value;

export const getGovernmentKey = (label) => reverseGovernmentMap[label] || label;

export const governmentOptions = Object.values(governmentMap).slice(1);
