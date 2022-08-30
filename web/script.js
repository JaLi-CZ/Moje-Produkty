var passwordText = document.getElementById("password")

let cookie = document.cookie
let cookies = cookie.split(";")

for(let i=0; i<cookies.length; i++) {
    let cookie = cookies[i]
    let parts = cookie.split("=")
    let key = parts[0]
    let value = parts[1]
    if(key === "password") {
        passwordText.innerHTML = value
        break
    }
}