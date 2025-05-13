function login()
{
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;
    let xhr = new XMLHttpRequest();
    xhr.open("post", "http:localhost:8081", true);
    xhr.onload = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            var userID = xhr.responseText;
            window.location.href = "welcome.html"
        }
    };
    xhr.send("username=" + username + "&password=" + password);
}