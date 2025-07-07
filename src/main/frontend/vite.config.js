import { defineConfig } from 'vite';
import path from 'path';

export default defineConfig({
    base: '/', // root path when served
    build: {
        outDir: path.resolve(__dirname, '../backend/src/main/resources/static'), // Spring Boot serves from here
        emptyOutDir: true
    },
    server: {
        port: 5173,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            }
        }
    }
});
