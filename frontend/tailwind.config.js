/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#38bdf8",
        "primary-dark": "#3b82f6",
        "primary-light": "#bae6fd",
      },
      backgroundImage: {
        "gradient-focus": "linear-gradient(135deg, #38bdf8 0%, #bae6fd 100%)",
      },
    },
  },
  plugins: [],
};
