/// <reference types="vite/client" />

/** Khai báo kiểu cho biến môi trường trong file .env */
interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
