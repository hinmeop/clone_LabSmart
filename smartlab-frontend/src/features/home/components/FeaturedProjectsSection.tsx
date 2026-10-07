import { Section } from '../../../components/ui/Section'
import { SectionHeading } from '../../../components/ui/SectionHeading'
import { ROUTES } from '../../../constants/routes'
import type { ProjectSummary } from '../../../types/home.types'
import { ProjectCard } from './ProjectCard'

interface FeaturedProjectsSectionProps {
  projects: ProjectSummary[]
}

export function FeaturedProjectsSection({ projects }: FeaturedProjectsSectionProps) {
  return (
    <Section background="white">
      <SectionHeading eyebrow="Dự án" title="Dự án tiêu biểu" action={{ label: 'Tất cả dự án', to: ROUTES.PROJECTS }} />

      <div className="grid grid-cols-1 gap-4 sm:gap-5 md:grid-cols-2 lg:grid-cols-3">
        {projects.map((project) => (
          <ProjectCard key={project.id} project={project} />
        ))}
      </div>
    </Section>
  )
}
