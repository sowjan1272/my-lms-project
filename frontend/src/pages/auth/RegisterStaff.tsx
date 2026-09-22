import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, getApiErrorMessage } from '../../api/client'

export default function RegisterStaff() {
  const navigate = useNavigate()
  const [form, setForm] = useState({
    fullName: '', email: '', password: '', confirmPassword: '', phone: '', address: '',
    dateOfJoining: '', designation: '', department: '', qualification: '', salary: '', skillsInput: '',
  })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const [submitting, setSubmitting] = useState(false)

  function update(field: string, value: string) {
    setForm((f) => ({ ...f, [field]: value }))
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match.')
      return
    }
    setSubmitting(true)
    try {
      const skills = form.skillsInput.split(',').map((s) => s.trim()).filter(Boolean)
      await api.post('/auth/register/staff', {
        fullName: form.fullName, email: form.email, password: form.password, confirmPassword: form.confirmPassword,
        phone: form.phone, address: form.address, dateOfJoining: form.dateOfJoining, designation: form.designation,
        department: form.department, qualification: form.qualification, salary: Number(form.salary), skills,
      })
      setSuccess(true)
      setTimeout(() => navigate('/login'), 2200)
    } catch (err) {
      setError(getApiErrorMessage(err, 'Registration failed. Please check your details and try again.'))
    } finally {
      setSubmitting(false)
    }
  }

  if (success) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 px-4">
        <div className="bg-white border border-slate-200 rounded-xl shadow-sm p-8 text-center max-w-sm">
          <p className="text-emerald-600 font-semibold text-lg mb-2">Registration submitted!</p>
          <p className="text-sm text-slate-500">An admin needs to activate your account before you can log in.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 px-4 py-10">
      <div className="w-full max-w-2xl">
        <div className="text-center mb-6">
          <h1 className="text-xl font-semibold text-slate-900">Staff Registration</h1>
          <p className="text-sm text-slate-500 mt-1">Your account will need admin approval before you can log in.</p>
        </div>

        <form onSubmit={handleSubmit} className="bg-white border border-slate-200 rounded-xl shadow-sm p-6 space-y-4">
          {error && <div className="bg-red-50 text-red-700 text-sm rounded-lg px-3 py-2">{error}</div>}

          <div className="grid sm:grid-cols-2 gap-4">
            <Field label="Full Name" required value={form.fullName} onChange={(v) => update('fullName', v)} />
            <Field label="Email" type="email" required value={form.email} onChange={(v) => update('email', v)} />
            <Field label="Password" type="password" required value={form.password} onChange={(v) => update('password', v)} />
            <Field label="Confirm Password" type="password" required value={form.confirmPassword} onChange={(v) => update('confirmPassword', v)} />
            <Field label="Phone" required value={form.phone} onChange={(v) => update('phone', v)} />
            <Field label="Date of Joining" type="date" required value={form.dateOfJoining} onChange={(v) => update('dateOfJoining', v)} />
            <Field label="Designation" required value={form.designation} onChange={(v) => update('designation', v)} />
            <Field label="Department" required value={form.department} onChange={(v) => update('department', v)} />
            <Field label="Qualification" value={form.qualification} onChange={(v) => update('qualification', v)} />
            <Field label="Expected Salary" type="number" required value={form.salary} onChange={(v) => update('salary', v)} />
          </div>
          <Field label="Address" value={form.address} onChange={(v) => update('address', v)} />
          <Field label="Skills (comma-separated)" value={form.skillsInput} onChange={(v) => update('skillsInput', v)} />

          <button
            type="submit"
            disabled={submitting}
            className="w-full bg-brand-600 hover:bg-brand-700 disabled:opacity-60 text-white rounded-lg py-2.5 text-sm font-medium"
          >
            {submitting ? 'Submitting...' : 'Register'}
          </button>
        </form>

        <p className="text-center mt-6 text-sm text-slate-500">
          Already have an account? <Link to="/login" className="text-brand-600 hover:underline">Log in</Link>
        </p>
      </div>
    </div>
  )
}

function Field({ label, value, onChange, type = 'text', required = false }: {
  label: string; value: string; onChange: (v: string) => void; type?: string; required?: boolean
}) {
  return (
    <div>
      <label className="block text-sm font-medium text-slate-700 mb-1">
        {label} {required && <span className="text-red-500">*</span>}
      </label>
      <input
        type={type}
        required={required}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500"
      />
    </div>
  )
}
