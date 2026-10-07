import { useState } from 'react'

const roles = ['IT Admin', 'Business Admin', 'Manager', 'Leader', 'Member', 'Guest']

export default function TestPage() {
  const [count, setCount] = useState(0)

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-100 to-blue-100 p-10">
      <div className="mx-auto max-w-4xl rounded-2xl bg-white p-8 shadow-xl">
        <h1 className="text-4xl font-bold text-blue-600">SmartLab Frontend</h1>
        <p className="mt-2 text-slate-500">
          Nếu bạn thấy nền gradient, card bo góc có bóng đổ và chữ xanh in đậm thì Tailwind đã hoạt động.
        </p>

        <div className="mt-8 grid grid-cols-3 gap-4">
          {roles.map((role) => (
            <div
              key={role}
              className="rounded-lg border border-slate-200 p-4 text-center font-medium text-slate-700 transition hover:-translate-y-1 hover:border-blue-500 hover:bg-blue-50 hover:shadow-md"
            >
              {role}
            </div>
          ))}
        </div>

        <div className="mt-8 flex items-center gap-4">
          <button
            onClick={() => setCount(count + 1)}
            className="rounded-lg bg-blue-600 px-5 py-2.5 font-semibold text-white hover:bg-blue-700 active:scale-95"
          >
            Click: {count}
          </button>
          <span className="rounded-full bg-green-100 px-3 py-1 text-sm font-medium text-green-700">
            Tailwind OK
          </span>
          <span className="rounded-full bg-amber-100 px-3 py-1 text-sm font-medium text-amber-700">
            React Router OK
          </span>
        </div>
      </div>
    </div>
  )
}