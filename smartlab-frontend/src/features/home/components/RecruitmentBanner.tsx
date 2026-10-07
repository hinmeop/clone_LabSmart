import { Link } from 'react-router-dom'
import { ChevronRightIcon } from '../../../components/icons'
import { ButtonLink } from '../../../components/ui/Button'
import { Container } from '../../../components/ui/Container'
import { buildPath, ROUTES } from '../../../constants/routes'
import type { RecruitmentSummary } from '../../../types/home.types'
import { formatDate } from '../../../utils/format'

interface RecruitmentBannerProps {
  semester: string
  posts: RecruitmentSummary[]
}

export function RecruitmentBanner({ semester, posts }: RecruitmentBannerProps) {
  return (
    <section className="py-14 sm:py-16 lg:py-20">
      <Container>
        <div className="grid grid-cols-1 gap-8 rounded-[20px] bg-ink-900 p-6 text-canvas sm:p-10 lg:grid-cols-12 lg:gap-10 lg:p-12">
          {/* Giới thiệu */}
          <div className="flex flex-col gap-4 lg:col-span-5">
            <span className="text-sm font-semibold text-brand-300">Tuyển thành viên · {semester}</span>
            <h2 className="text-[26px] leading-tight font-bold sm:text-3xl lg:text-[34px]">
              Tham gia dự án thật cùng SmartLab
            </h2>
            <p className="text-[15px] leading-[1.65] text-ink-200 sm:text-base">
              Chọn bài tuyển của dự án bạn quan tâm, bấm <strong className="text-white">Ứng tuyển ngay</strong> và điền
              form ứng tuyển trực tiếp trên hệ thống. Hồ sơ được lưu lại, Lab xét duyệt và gửi lịch phỏng vấn qua email.
            </p>
            <ButtonLink
              to={ROUTES.RECRUITMENT}
              variant="accent"
              size="lg"
              className="mt-2 self-start"
              rightIcon={<ChevronRightIcon />}
            >
              Xem tất cả bài tuyển
            </ButtonLink>
          </div>

          {/* Danh sách bài tuyển */}
          <ul className="flex flex-col gap-3 lg:col-span-7">
            {posts.map((post) => (
              <li key={post.id}>
                <Link
                  to={buildPath(ROUTES.RECRUITMENT_DETAIL, { postId: post.id })}
                  className="group flex flex-col gap-3 rounded-xl border border-line-dark bg-white/[0.06] p-4 text-canvas transition hover:bg-white/10 sm:flex-row sm:items-center sm:gap-4 sm:px-5 sm:py-[18px]"
                >
                  <span className="flex grow flex-col gap-1">
                    <span className="text-base font-semibold">{post.title}</span>
                    <span className="text-[13px] text-ink-300">
                      {post.projectName} · Tuyển {post.quantity} người · Hạn {formatDate(post.deadline)}
                    </span>
                  </span>
                  <span className="inline-flex h-9 shrink-0 items-center self-start rounded-lg bg-white px-3.5 text-sm font-semibold whitespace-nowrap text-ink-900 group-hover:bg-brand-300 sm:self-auto">
                    Ứng tuyển ngay
                  </span>
                </Link>
              </li>
            ))}
          </ul>
        </div>
      </Container>
    </section>
  )
}
