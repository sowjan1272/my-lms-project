import { ReactNode, useState } from 'react'
import EmptyState from './EmptyState'

export interface Column<T> {
  header: string
  accessor: (row: T) => ReactNode
  className?: string
}

interface Props<T> {
  columns: Column<T>[]
  data: T[]
  emptyMessage?: string
  searchable?: boolean
  onSearch?: (query: string) => void
  rowKey: (row: T) => string | number
  onRowClick?: (row: T) => void
}

export default function DataTable<T>({
  columns, data, emptyMessage = 'No records found.', searchable, onSearch, rowKey, onRowClick,
}: Props<T>) {
  const [query, setQuery] = useState('')

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
      {searchable && (
        <div className="p-4 border-b border-slate-100">
          <input
            type="text"
            placeholder="Search..."
            value={query}
            onChange={(e) => {
              setQuery(e.target.value)
              onSearch?.(e.target.value)
            }}
            className="w-full sm:w-72 rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500"
          />
        </div>
      )}

      {data.length === 0 ? (
        <EmptyState message={emptyMessage} />
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-slate-100 bg-slate-50/60">
                {columns.map((col) => (
                  <th key={col.header} className="text-left px-4 py-3 font-medium text-slate-500 whitespace-nowrap">
                    {col.header}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {data.map((row) => (
                <tr
                  key={rowKey(row)}
                  onClick={() => onRowClick?.(row)}
                  className={`border-b border-slate-50 last:border-0 ${onRowClick ? 'cursor-pointer hover:bg-slate-50' : ''}`}
                >
                  {columns.map((col) => (
                    <td key={col.header} className={`px-4 py-3 text-slate-700 ${col.className || ''}`}>
                      {col.accessor(row)}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
