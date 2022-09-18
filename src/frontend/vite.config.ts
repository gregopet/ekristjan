import { fileURLToPath, URL } from 'url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import * as fs from 'fs';
import path from 'path';
import { VitePWA } from "vite-plugin-pwa"
import tailwindcss from 'tailwindcss'

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
    plugins: [vue(), tailwindcss() as any, VitePWA({
      strategies: "injectManifest", // https://vite-plugin-pwa.netlify.app/guide/inject-manifest.html#custom-service-worker,
      injectRegister: null, //https://vite-plugin-pwa.netlify.app/guide/register-service-worker.html
      srcDir: 'src',
      disable: false, // DISABLE & RUN 'yarn preview', THEN DO A HARD RESET TO RELOAD THE SERVICE WORKER TO ENABLE DEV TIME RELOADING!
      filename: 'serviceworker.ts',
      devOptions: {
        enabled: true,
      },
      mode: "development",
      manifest: {
        name: "e-kristijan",
        short_name: 'e-kristijan',
        icons: [
          { sizes: '582x582', src: "/francetabevka.png", type: "image/png" }
        ],
        background_color: 'white',
        display: 'standalone',
        start_url: '/',
      },
      includeAssets: ['francetabevka.png'],
    })],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        'sockjs-client': path.resolve(__dirname, './node_modules/sockjs-client/dist/sockjs.js'),
      }
    },
    build: { },
    server: {
      ...httpsConfig,
      proxy: {
        '/departures': { target: 'http://127.0.0.1:8888' },
        '/security': { target: 'http://127.0.0.1:8888' },
        '/log': { target: 'http://127.0.0.1:8888' },
        '/backoffice': { target: 'http://127.0.0.1:8888' },
        '/sock': { target: 'ws://127.0.0.1:8888', ws: true }
      }
    },
  }
})
