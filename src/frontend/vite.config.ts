import { fileURLToPath, URL } from 'url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import * as fs from 'fs';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig( ({ command, mode }) => {

  // Load .env files
  const env = loadEnv(mode, process.cwd(), '')

  // Paths to HTTPS certificates can be provided by environment variables HTTPS_KEY and HTTPS_CERT
  const httpsConfig = (env.HTTPS_KEY && env.HTTPS_CERT) ? {
    https: {
      key: fs.readFileSync(env.HTTPS_KEY),
      cert: fs.readFileSync(env.HTTPS_CERT)
    }
  } : {}

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        'sockjs-client': path.resolve(__dirname, './node_modules/sockjs-client/dist/sockjs.js'),
      }
    },
    server: {
      ...httpsConfig,
      proxy: {
        '/sock': {
          target: 'ws://127.0.0.1:8888/',
          ws: true,
          changeOrigin: true,
        }
      }
    },
  }
})
