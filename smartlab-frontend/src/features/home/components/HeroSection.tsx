import { FolderIcon, SendIcon } from '../../../components/icons'
import { ButtonLink } from '../../../components/ui/Button'
import { Container } from '../../../components/ui/Container'
import { ROUTES } from '../../../constants/routes'
import type { LabStat } from '../../../types/home.types'
import { cn } from '../../../utils/format'

interface HeroSectionProps {
  stats: LabStat[]
}

export function HeroSection({ stats }: HeroSectionProps) {
  return (
    <section className="border-b border-line bg-white">
      <Container className="grid grid-cols-1 items-center gap-10 py-12 sm:py-16 lg:grid-cols-12 lg:gap-14 lg:pt-20 lg:pb-[72px]">
        {/* Giới thiệu */}
        <div className="flex flex-col gap-5 sm:gap-6 lg:col-span-7">
          <span className="inline-flex items-center gap-2 self-start rounded-full bg-brand-50 px-3 py-1.5 text-[13px] font-semibold text-brand-600 sm:text-sm">
            <span className="size-2 shrink-0 rounded-full bg-brand-600" />
            Phòng Lab nghiên cứu · Khoa Công nghệ thông tin
          </span>

          <h1 className="text-[32px] leading-[1.2] font-bold tracking-[-0.02em] sm:text-[42px] lg:text-[52px] lg:leading-[1.15]">
            Nơi sinh viên biến ý tưởng nghiên cứu thành sản phẩm thật
          </h1>

          <p className="max-w-[620px] text-base leading-[1.65] text-ink-600 sm:text-lg">
            SmartLab quản lý toàn bộ hoạt động của phòng Lab: lĩnh vực, dự án theo năm – kỳ, nhiệm vụ, tài liệu, tin
            tức và tuyển thành viên — tất cả ở một nơi.
          </p>

          <div className="flex flex-col gap-3 sm:flex-row">
            <ButtonLink to={ROUTES.PROJECTS} size="lg" leftIcon={<FolderIcon />}>
              Xem dự án
            </ButtonLink>
            <ButtonLink to={ROUTES.RECRUITMENT} size="lg" variant="outline" leftIcon={<SendIcon />}>
              Ứng tuyển thành viên
            </ButtonLink>
          </div>
        </div>

        {/* Thống kê nhanh — luôn là lưới 2x2 */}
        <dl className="grid grid-cols-2 overflow-hidden rounded-2xl border border-line bg-canvas lg:col-span-5">
          {stats.map((stat, index) => (
            <div
              key={stat.label}
              className={cn(
                'flex flex-col-reverse gap-1.5 px-4 py-5 sm:px-6 sm:py-7',
                index % 2 === 1 && 'border-l border-line',
                index >= 2 && 'border-t border-line',
              )}
            >
              <dt className="text-sm text-ink-600 sm:text-[15px]">{stat.label}</dt>
              <dd className="text-[32px] leading-none font-bold sm:text-[40px]">{stat.value}</dd>
            </div>
          ))}
        </dl>
      </Container>
    </section>
  )
}
