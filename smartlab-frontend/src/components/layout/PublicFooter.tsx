import { Link } from 'react-router-dom'
import { ROUTES } from '../../constants/routes'
import { LAB_CONTACT, LAB_MAP, SITE_NAME } from '../../constants/site'
import { MapPinIcon } from '../icons'
import { Container } from '../ui/Container'

const FOOTER_LINKS = [
  { label: 'Dự án', to: ROUTES.PROJECTS },
  { label: 'Tin tức', to: ROUTES.NEWS },
  { label: 'Tuyển dụng', to: ROUTES.RECRUITMENT },
  { label: 'Liên hệ', to: ROUTES.CONTACT },
]

export function PublicFooter() {
  const contactItems = [
    { label: 'Email', value: LAB_CONTACT.email, href: `mailto:${LAB_CONTACT.email}` },
    { label: 'Điện thoại', value: LAB_CONTACT.phone, href: `tel:${LAB_CONTACT.phone.replace(/\s/g, '')}` },
    { label: 'Địa chỉ', value: LAB_CONTACT.address },
  ]

  return (
    <footer id="contact" className="border-t border-line bg-white pt-12 pb-8 lg:pt-16">
      <Container className="flex flex-col gap-10">
        <div className="grid grid-cols-1 gap-8 md:grid-cols-2 lg:grid-cols-12">
          <div className="flex flex-col gap-3 lg:col-span-4">
            <span className="text-sm font-semibold text-brand-600">Liên hệ</span>
            <h2 className="text-2xl font-bold sm:text-[28px]">Kết nối với {SITE_NAME}</h2>
            <p className="text-[15px] leading-relaxed text-ink-600">
              Cần hợp tác nghiên cứu hoặc hỏi về tuyển thành viên? Hãy liên hệ với chúng tôi.
            </p>
          </div>

          <dl className="flex flex-col gap-4 text-[15px] lg:col-span-3">
            {contactItems.map((item) => (
              <div key={item.label} className="flex flex-col gap-0.5">
                <dt className="text-[13px] text-ink-500">{item.label}</dt>
                <dd className="font-medium break-words">
                  {item.href ? (
                    <a href={item.href} className="text-ink-900 hover:text-brand-600">
                      {item.value}
                    </a>
                  ) : (
                    item.value
                  )}
                </dd>
              </div>
            ))}
          </dl>

          {/* Bản đồ Google Maps: kéo/zoom trực tiếp + nút mở Google Maps ở tab mới */}
          <div className="relative h-64 overflow-hidden rounded-[14px] border border-line bg-[#e7ebe6] md:col-span-2 lg:col-span-5 lg:h-[240px]">
            <iframe
              title="Bản đồ Trường Đại học FPT Hà Nội"
              src={LAB_MAP.embedUrl}
              loading="lazy"
              referrerPolicy="no-referrer-when-downgrade"
              allowFullScreen
              className="h-full w-full border-0"
            />
            <a
              href={LAB_MAP.linkUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="absolute top-3 left-3 inline-flex items-center gap-1.5 rounded-lg bg-white px-3 py-2 text-[13px] font-semibold text-brand-600 shadow-md transition hover:bg-brand-600 hover:text-white"
            >
              <MapPinIcon size={15} />
              Mở Google Maps
            </a>
          </div>
        </div>

        <div className="flex flex-col-reverse gap-4 border-t border-line pt-5 text-sm text-ink-500 sm:flex-row sm:items-center sm:justify-between">
          <span>© {new Date().getFullYear()} {SITE_NAME}. Bảo lưu mọi quyền.</span>
          <nav aria-label="Liên kết chân trang" className="flex flex-wrap gap-x-5 gap-y-2">
            {FOOTER_LINKS.map((link) => (
              <Link key={link.label} to={link.to} className="text-ink-500 hover:text-brand-600">
                {link.label}
              </Link>
            ))}
          </nav>
        </div>
      </Container>
    </footer>
  )
}
