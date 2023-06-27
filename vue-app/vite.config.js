import {fileURLToPath, URL} from 'node:url'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/

const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        allowedHosts: "all"
    }
})
export default defineConfig({
    plugins: [
        vue(),
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        // Add the following line to disable host check
        disableHostCheck: true
    }
})
