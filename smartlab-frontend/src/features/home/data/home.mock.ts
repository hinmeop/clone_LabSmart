import type { HomePageData } from '../../../types/home.types'

/**
 * Dữ liệu mẫu cho Trang chủ (theo mockup).
 * Chỉ dùng trong homeService khi backend chưa có API.
 */
export const HOME_MOCK_DATA: HomePageData = {
  stats: [
    { label: 'Lĩnh vực nghiên cứu', value: 5 },
    { label: 'Dự án đang thực hiện', value: 38 },
    { label: 'Thành viên', value: 184 },
    { label: 'Đợt tuyển đang mở', value: 3 },
  ],

  domains: [
    {
      id: 1,
      code: 'SE',
      name: 'Software Engineering',
      description: 'Phát triển web, mobile và hệ thống thông tin cho nhà trường và doanh nghiệp.',
      managerName: 'Nguyễn Văn Tài',
    },
    {
      id: 2,
      code: 'AI',
      name: 'Trí tuệ nhân tạo (AI)',
      description: 'Học máy, thị giác máy tính và xử lý ngôn ngữ tự nhiên tiếng Việt.',
      managerName: 'Trần Ngọc Lâm',
    },
    {
      id: 3,
      code: 'NCKH',
      name: 'Nghiên cứu khoa học',
      description: 'Đề tài NCKH sinh viên, viết và công bố bài báo hội nghị.',
      managerName: 'Trần Ngọc Lâm',
    },
    {
      id: 4,
      code: 'ĐA',
      name: 'Đồ án tốt nghiệp',
      description: 'Hướng dẫn và quản lý đồ án capstone các khóa SE, AI.',
      managerName: 'Nguyễn Văn Tài',
    },
    {
      id: 5,
      code: 'RB',
      name: 'Robotics',
      description: 'Robot tự hành, hệ thống điều khiển và cơ điện tử.',
      managerName: 'Phạm Minh Hùng',
    },
  ],

  featuredProjects: [
    {
      id: 1,
      name: 'SmartLab Operation Management System',
      description: 'Hệ thống quản lý vận hành phòng Lab: domain, dự án, nhiệm vụ, tài liệu và tuyển dụng.',
      domainName: 'Software Engineering',
      semester: 'Fall 2026',
      status: 'processing',
      startDate: '2026-09-01',
      endDate: '2027-01-15',
      leaderName: 'Hoàng Phương Nam',
      memberCount: 11,
      tone: 'mint',
    },
    {
      id: 2,
      name: 'Chatbot tư vấn tuyển sinh',
      description: 'Trợ lý hỏi đáp tuyển sinh bằng mô hình ngôn ngữ tiếng Việt.',
      domainName: 'AI',
      semester: 'Fall 2026',
      status: 'processing',
      startDate: '2026-09-05',
      endDate: '2026-12-30',
      leaderName: 'Lê Minh',
      memberCount: 5,
      tone: 'sand',
    },
    {
      id: 3,
      name: 'Cổng tài liệu nội bộ lab',
      description: 'Kho tài liệu và quy trình của lab, quản lý theo thư mục.',
      domainName: 'Software Engineering',
      semester: 'Summer 2026',
      status: 'done',
      startDate: '2026-06-01',
      endDate: '2026-09-30',
      leaderName: 'Nguyễn Thảo Vy',
      memberCount: 4,
      tone: 'lavender',
    },
  ],

  news: [
    {
      id: 1,
      title: 'SmartLab đạt giải Nhất Hackathon FPT 2026',
      excerpt: 'Nhóm SmartLab Operation Management System vượt qua 42 đội, giành giải Nhất với sản phẩm quản lý phòng Lab.',
      publishedAt: '2026-09-22',
      authorName: 'Nguyễn Văn Tài',
      tone: 'sand',
    },
    {
      id: 2,
      title: 'Chatbot tư vấn tuyển sinh ra mắt bản thử nghiệm',
      excerpt: 'Chatbot trả lời hơn 300 câu hỏi tuyển sinh, mở thử nghiệm từ 01/10.',
      publishedAt: '2026-09-25',
      authorName: 'Trần Ngọc Lâm',
      tone: 'lavender',
    },
    {
      id: 3,
      title: 'Hệ thống đặt phòng họp triển khai cho toàn khoa',
      excerpt: 'Đặt phòng, thiết bị trực tuyến thay cho sổ giấy.',
      publishedAt: '2026-09-20',
      authorName: 'Nguyễn Văn Tài',
      tone: 'rose',
    },
    {
      id: 4,
      title: 'Robot tự hành của lab vào chung kết Robocon 2026',
      excerpt: 'Đội Robotics vượt qua vòng khu vực với robot dò đường bằng camera.',
      publishedAt: '2026-09-15',
      authorName: 'Phạm Minh Hùng',
      tone: 'leaf',
    },
  ],

  team: [
    { id: 1, fullName: 'Nguyễn Văn Tài', domainName: 'Software Engineering', role: 'manager', tone: 'mint' },
    { id: 2, fullName: 'Trần Ngọc Lâm', domainName: 'AI', role: 'manager', tone: 'sand' },
    { id: 3, fullName: 'Phạm Minh Hùng', domainName: 'Robotics', role: 'manager', tone: 'lavender' },
    { id: 4, fullName: 'Hoàng Phương Nam', domainName: 'Software Engineering', role: 'leader', tone: 'rose' },
    { id: 5, fullName: 'Lê Minh', domainName: 'AI', role: 'leader', tone: 'leaf' },
    { id: 6, fullName: 'Nguyễn Thảo Vy', domainName: 'Software Engineering', role: 'leader', tone: 'stone' },
  ],

  recruitment: {
    semester: 'Fall 2026',
    posts: [
      {
        id: 1,
        title: 'Tuyển Thực tập sinh Frontend (React)',
        projectName: 'SmartLab Operation Management System',
        quantity: 5,
        deadline: '2026-10-15',
      },
      {
        id: 2,
        title: 'Tuyển thành viên NLP cho Chatbot tuyển sinh',
        projectName: 'Chatbot tư vấn tuyển sinh',
        quantity: 3,
        deadline: '2026-10-20',
      },
      {
        id: 3,
        title: 'Tuyển Backend cho nền tảng chấm bài',
        projectName: 'Nền tảng chấm bài lập trình tự động',
        quantity: 4,
        deadline: '2026-10-30',
      },
    ],
  },
}
