import { Container } from '../components/ui/Container'
import {
  DomainSection,
  FeaturedProjectsSection,
  HeroSection,
  NewsSection,
  RecruitmentBanner,
  TeamSection,
  useHomeData,
} from '../features/home'

export default function HomePage() {
  const { data, loading, error } = useHomeData()

  if (loading) {
    return (
      <Container className="flex min-h-[60vh] items-center justify-center">
        <span className="size-8 animate-spin rounded-full border-2 border-line border-t-brand-600" aria-label="Đang tải" />
      </Container>
    )
  }

  if (error || !data) {
    return (
      <Container className="flex min-h-[60vh] items-center justify-center text-ink-600">
        {error ?? 'Không có dữ liệu.'}
      </Container>
    )
  }

  return (
    <>
      <HeroSection stats={data.stats} />
      <DomainSection domains={data.domains} />
      <FeaturedProjectsSection projects={data.featuredProjects} />
      <NewsSection news={data.news} />
      <TeamSection members={data.team} />
      <RecruitmentBanner semester={data.recruitment.semester} posts={data.recruitment.posts} />
    </>
  )
}
