/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#38bdf8",
        "primary-dark": "#3b82f6",
        "primary-light": "#bae6fd",

        secondary: "#f2f2f2",
        "secondary-dark": "#cacacaff",
        "secondary-light": "#ffffff",

        accent: "#3b82f6",
        "accent-two": "#2563eb",
        "accent-three": "#1d4ed8",

        background: "#e0f7fa",
        "background-dark": "#b2ebf2",
        "background-light": "#e0f2fe",
      },
      backgroundImage: {
        "gradient-focus": "linear-gradient(135deg, #38bdf8 0%, #bae6fd 100%)",
      },
    },
  },
  plugins: [],
};
