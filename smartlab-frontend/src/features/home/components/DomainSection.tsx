import { ChevronRightIcon } from '../../../components/icons'
import { ButtonLink } from '../../../components/ui/Button'
import { Section } from '../../../components/ui/Section'
import { SectionHeading } from '../../../components/ui/SectionHeading'
import { ROUTES } from '../../../constants/routes'
import type { DomainSummary } from '../../../types/home.types'
import { DomainCard } from './DomainCard'

interface DomainSectionProps {
  domains: DomainSummary[]
}

export function DomainSection({ domains }: DomainSectionProps) {
  return (
    <Section>
      <SectionHeading
        eyebrow="Lĩnh vực"
        title="Các domain của SmartLab"
        description="Mỗi dự án thuộc một domain và được tổ chức theo năm học → kỳ học. Mỗi domain do một Manager phụ trách."
        action={{ label: 'Tất cả lĩnh vực', to: ROUTES.DOMAINS }}
      />

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 sm:gap-5 lg:grid-cols-3">
        {domains.map((domain) => (
          <DomainCard key={domain.id} domain={domain} />
        ))}

        {/* Thẻ kêu gọi ứng tuyển */}
        <div className="flex flex-col justify-between gap-4 rounded-[14px] bg-brand-600 p-5 text-white sm:p-6">
          <div className="flex flex-col gap-2">
            <span className="text-[13px] font-semibold text-brand-300">Tuyển thành viên</span>
            <h3 className="text-xl leading-snug font-bold sm:text-[22px]">Chưa biết bắt đầu từ đâu?</h3>
            <p className="text-[15px] leading-relaxed text-brand-200">
              Xem các bài tuyển đang mở để tìm dự án phù hợp với kỹ năng của bạn.
            </p>
          </div>
          <ButtonLink to={ROUTES.RECRUITMENT} variant="white" className="self-start" rightIcon={<ChevronRightIcon />}>
            Xem bài tuyển
          </ButtonLink>
        </div>
      </div>
    </Section>
  )
}
