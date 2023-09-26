import { useState } from "react"
import Web3 from "web3"

export default function Sign () {
  const [isVerified, setIsVerified] = useState(false)

  const onClick = async () => {
    const provider = window.ethereum || window.web3?.provider || null

    if (!provider) {
      console.error('!provider')
      return
    }

    const web3 = new Web3(provider)
    const [address] = await web3.eth.requestAccounts()
    var date = new Date();
	var res = date.getFullYear() + '/' + ('0' + (date.getMonth() + 1)).slice(-2) + '/' +('0' + date.getDate()).slice(-2) + ' ' +  ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2) + ':' + ('0' + date.getSeconds()).slice(-2) ;

    const message = 'テストメッセージ \nEOA:' + address +  '\n' + res
    const password = ''
    const signature = await web3.eth.personal.sign(message, address, password)
    const response = await fetch('http://localhost:8080/check', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify({message, address, signature}),
    })

    const actual = web3.eth.accounts.recover(message, signature)
    console.log(actual)

    const body = await response.json()
    setIsVerified(body.isVerified)
  }

  return (
    <>
      <button onClick={onClick}>Sign</button>
      {isVerified && <p>Verified!</p>}
    </>
  )
}