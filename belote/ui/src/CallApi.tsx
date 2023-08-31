import { useState } from 'react'

export const CallApi = () => {
    const [message, setMessage] = useState("")

    const callHello = function () {
        fetch("/api/game")
            .then(data => {
                console.log(data)
                data.text().then(text => setMessage(() => text))
            })
    }

    return (
        <>
            <button onClick={callHello}>Call</button>
            <div>{message}</div>
        </>
    )
}