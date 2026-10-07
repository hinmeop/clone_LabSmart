import { Link } from 'react-router-dom'
import { Section } from '../../../components/ui/Section'
import { SectionHeading } from '../../../components/ui/SectionHeading'
import { buildPath, ROUTES } from '../../../constants/routes'
import type { NewsSummary } from '../../../types/home.types'
import { cn, formatDate } from '../../../utils/format'
import { TONE_BG, TONE_GRADIENT } from '../../../utils/tone'

const newsPath = (id: number) => buildPath(ROUTES.NEWS_DETAIL, { newsId: id })

function FeaturedNewsCard({ news }: { news: NewsSummary }) {
  return (
    <Link
      to={newsPath(news.id)}
      className="group flex flex-col overflow-hidden rounded-[14px] border border-line bg-white text-ink-900 transition hover:shadow-[0_8px_24px_-12px_rgba(21,32,29,0.25)]"
    >
      {news.coverImageUrl ? (
        <img
          src={news.coverImageUrl}
          alt=""
          className="h-48 w-full object-cover sm:h-[260px]"
          loading="lazy"
        />
      ) : (
        <div className={cn('h-48 sm:h-[260px]', TONE_GRADIENT[news.tone])} aria-hidden="true" />
      )}
      <div className="flex flex-col gap-2.5 p-5 sm:p-6">
        <span className="text-sm text-ink-500">
          {formatDate(news.publishedAt)} · {news.authorName}
        </span>
        <h3 className="text-[22px] leading-[1.3] font-bold group-hover:text-brand-600 sm:text-[26px]">{news.title}</h3>
        <p className="text-[15px] leading-relaxed text-ink-600 sm:text-base">{news.excerpt}</p>
      </div>
    </Link>
  )
}

function CompactNewsCard({ news }: { news: NewsSummary }) {
  return (
    <Link
      to={newsPath(news.id)}
      className="group flex gap-4 rounded-[14px] border border-line bg-white p-4 text-ink-900 transition hover:border-brand-600/40"
    >
      {news.coverImageUrl ? (
        <img src={news.coverImageUrl} alt="" className="h-[84px] w-24 shrink-0 rounded-[10px] object-cover" loading="lazy" />
      ) : (
        <span className={cn('h-[84px] w-20 shrink-0 rounded-[10px] sm:w-24', TONE_BG[news.tone])} aria-hidden="true" />
      )}
      <span className="flex min-w-0 flex-col gap-1.5">
        <span className="text-[13px] text-ink-500">{formatDate(news.publishedAt)}</span>
        <span className="text-[15px] leading-snug font-semibold group-hover:text-brand-600 sm:text-base">{news.title}</span>
        <span className="line-clamp-2 text-sm leading-normal text-ink-600">{news.excerpt}</span>
      </span>
    </Link>
  )
}

interface NewsSectionProps {
  news: NewsSummary[]
}

export function NewsSection({ news }: NewsSectionProps) {
  const [featured, ...others] = news
  if (!featured) return null

  return (
    <Section>
      <SectionHeading eyebrow="Tin tức" title="Tin tức dự án nổi bật" action={{ label: 'Tất cả tin tức', to: ROUTES.NEWS }} />

      <div className="grid grid-cols-1 gap-4 sm:gap-5 lg:grid-cols-12">
        <div className="lg:col-span-7">
          <FeaturedNewsCard news={featured} />
        </div>
        <div className="flex flex-col gap-4 lg:col-span-5">
          {others.slice(0, 3).map((item) => (
            <CompactNewsCard key={item.id} news={item} />
          ))}
        </div>
      </div>
    </Section>
  )
}
