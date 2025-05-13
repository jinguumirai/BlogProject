function loadNaviBar(userID)
{
    if (userID == "")
    {
        fetch('htmlFiles/title.html') // use fetch load navi bar
            .then(response => response.text())
            .then(data => {
                document.getElementById('navbar-placeholder').innerHTML = data; // 将获取的数据插入到占位符中
        })
    }
    else
    {
        document.getElementById('navbar-placeholder').innerHTML = userID
    }
}