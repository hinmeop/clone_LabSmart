/** Ghép className, bỏ qua giá trị falsy. */
export function cn(...classes: Array<string | false | null | undefined>): string {
  return classes.filter(Boolean).join(' ')
}

/** ISO date (yyyy-mm-dd) → dd/mm/yyyy. */
export function formatDate(iso: string): string {
  const [year, month, day] = iso.slice(0, 10).split('-')
  if (!year || !month || !day) return iso
  return `${day}/${month}/${year}`
}

/** "Nguyễn Văn Tài" → "NT" (chữ cái đầu của họ + tên). */
export function getInitials(fullName: string): string {
  const parts = fullName.trim().split(/\s+/)
  if (parts.length === 0 || !parts[0]) return ''
  const first = parts[0].charAt(0)
  const last = parts.length > 1 ? parts[parts.length - 1].charAt(0) : ''
  return (first + last).toUpperCase()
}
