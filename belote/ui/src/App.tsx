import { useState } from 'react'
import './App.css'
import { Connection } from './Connection'
import { Game } from './game/Game'

function App() {
  const [name, setName] = useState('')

  return (
    <>
      {!name?.length && <Connection callback={setName} />}
      {name && <Game name={name} />}
    </>
  )
}

export default App
