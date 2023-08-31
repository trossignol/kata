import { useState } from 'react'
import './App.css'
import { CallApi } from './CallApi'
import { Connection } from './Connection'
import { Game } from './game/Game'

function App() {
  const [name, setName] = useState('')

  return (
    <>
      {!name?.length && <Connection callback={setName} />}
      {name && <Game name={name} />}
      <CallApi />
    </>
  )
}

export default App
