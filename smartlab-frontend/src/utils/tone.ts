import type { Tone } from '../types/common.types'

/** Map tông màu → class nền Tailwind (khai báo tĩnh để Tailwind quét được). */
export const TONE_BG: Record<Tone, string> = {
  mint: 'bg-tone-mint',
  sand: 'bg-tone-sand',
  lavender: 'bg-tone-lavender',
  rose: 'bg-tone-rose',
  leaf: 'bg-tone-leaf',
  stone: 'bg-tone-stone',
}

/** Gradient dùng làm ảnh bìa tạm khi tin tức chưa có ảnh. */
export const TONE_GRADIENT: Record<Tone, string> = {
  mint: 'bg-gradient-to-br from-brand-600 via-brand-300 to-tone-mint',
  sand: 'bg-[linear-gradient(120deg,#8a4b00_0%,#d9a441_60%,#f1e7d6_100%)]',
  lavender: 'bg-gradient-to-br from-[#3b4a8a] via-[#8f9bd1] to-tone-lavender',
  rose: 'bg-gradient-to-br from-[#8a3b52] via-[#d18fa3] to-tone-rose',
  leaf: 'bg-gradient-to-br from-[#3f6b1e] via-[#9fc47f] to-tone-leaf',
  stone: 'bg-gradient-to-br from-[#5c5340] via-[#b8ab8f] to-tone-stone',
}
