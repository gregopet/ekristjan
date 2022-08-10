import { fileURLToPath, URL } from 'url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import * as fs from 'fs';
import path from 'path';
import { VitePWA } from "vite-plugin-pwa"

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
    plugins: [vue(), VitePWA({
      strategies: "injectManifest", // https://vite-plugin-pwa.netlify.app/guide/inject-manifest.html#custom-service-worker,
      registerType: "autoUpdate",
      srcDir: 'src',
      disable: false,
      filename: 'serviceworker.ts',
      devOptions: {
        enabled: true,
      },
      manifest: {
        name: "e-kristijan",
        short_name: 'e-kristijan',
        icons: [
          { sizes: 'any', src: "/assets/francetabevka.58da755a.jpg", type: "image/jpeg" }
        ],
        background_color: 'white',
        display: 'standalone',
        start_url: '/',
      }
    })],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        'sockjs-client': path.resolve(__dirname, './node_modules/sockjs-client/dist/sockjs.js'),
      }
    },
    server: {
      ...httpsConfig,
      proxy: {
        '/departures': { target: 'ws://127.0.0.1:8888/' },
        '/security': { target: 'ws://127.0.0.1:8888/' },
        '/sock': { target: 'ws://127.0.0.1:8888/', ws: true }
      }
    },
  }
})
