import { useState } from 'react';

type ConnectionProps = {
    callback: (name: string) => void
}

export const Connection = (props: ConnectionProps) => {

    const [inputValue, setInputValue] = useState("Thomas");

    const inputChange = (event: React.ChangeEvent<HTMLInputElement>) => setInputValue(event.target.value)
    const connect = () => props.callback(inputValue)

    return (
        <div>
            <label>Name</label><input type="text" value={inputValue} onChange={inputChange} />
            <button onClick={connect}>Go!</button>
        </div>
    )
}