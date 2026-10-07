/**
 * Chữ viết tắt hiển thị trong ô vuông của domain (backend chưa có trường "mã viết tắt").
 * "Software Engineering" → "SE", "Nghiên cứu khoa học" → "NCKH", "Robotics" → "RO"
 */
export function getDomainCode(name: string): string {
  const words = name
    .replace(/[&()\-–,./]/g, ' ')
    .split(/\s+/)
    .filter(Boolean)
  if (words.length === 0) return '?'
  if (words.length === 1) return words[0].slice(0, 2).toUpperCase()
  return words
    .slice(0, 4)
    .map((w) => w.charAt(0))
    .join('')
    .toUpperCase()
}
