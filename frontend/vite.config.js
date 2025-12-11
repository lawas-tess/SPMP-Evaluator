import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    strictPort: false,
    open: true,
  },
  test: {
    environment: "jsdom",
    globals: true,
    setupFiles: "./src/Test/setupTests.js",
  },

  build: {
    outDir: "dist",
    sourcemap: false,
  },
});
