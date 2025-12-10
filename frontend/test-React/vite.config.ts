// vite.config.ts
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import * as path from 'path'; // pathモジュールを追加

// __dirname を使用してルートディレクトリを解決する
const rootDir = path.resolve(__dirname); 

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    // Reactのパスをプロジェクトルートのnode_modulesに強制的に解決する
    alias: {
      'react': path.resolve(rootDir, 'node_modules/react'),
      'react-dom': path.resolve(rootDir, 'node_modules/react-dom'),
    },
  },
})

