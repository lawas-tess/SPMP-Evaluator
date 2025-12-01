/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#7C3AED',
        'primary-dark': '#6D28D9',
        'primary-light': '#A78BFA',
      },
      backgroundImage: {
        'gradient-focus': 'linear-gradient(135deg, #7C3AED 0%, #A78BFA 100%)',
      },
    },
  },
  plugins: [],
}
