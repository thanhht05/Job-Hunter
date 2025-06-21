import { useEffect, useState } from 'react'
import './App.css'

function App() {
  const [message, setMessage] = useState('Đang tải...')
  
  useEffect(() => {
    const backendUrl = import.meta.env.VITE_BACKEND_URL

    fetch(`${backendUrl}/`)
      .then((res) => res.json())
      .then((log) => setMessage(log.data))
      .catch((err) => {
        console.error(err)
        setMessage('Không kết nối được đến backend!')
      })
  }, [])

  return (
    <>
      <h1>Giao tiếp Backend</h1>
      <pre>{JSON.stringify(message, null, 2)}</pre> // ✅ Hiển thị đẹp JSON

    </>
  )
}

export default App
