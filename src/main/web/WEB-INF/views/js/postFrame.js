function postFrame(index, post, id) {
    var checkYoutube = 1;

    if (checkYoutube != 4) {
        var newDiv=document.createElement('div');
        newDiv.innerHTML =
            "<div class=\"container_hostPost\" style=\"border:0.5px solid black\">\n" +
            "    <div class=\"post-img\" style=\"border:0.5px solid black\">\n" +
            "        <img src=\"" +
            post[0] +
            "\" alt=\"postImage\">\n" +
            "    </div>\n" +
            "    <div class=\"post-etc\" style=\"border:0.5px solid black\">\n" +
            post[1] +
            "    </div>\n" +
            "    <div class=\"post-text\" style=\"border:0.5px solid black\">\n" +
            post[2] +
            "    </div>\n" +
            "</div>";
        document.getElementById(id).appendChild(newDiv);
        checkYoutube++;
    }
    else {
        var newDiv=document.createElement('div');
        newDiv.innerHTML = 
            "<div class=\"container_hostPost\" style=\"border:0.5px solid black\">\n" +
            "    <div class=\"post-img\" style=\"border:0.5px solid black\">\n" +
            "        <img src=\"" +
            post[0] +
            "\" alt=\"postImage\">\n" +
            "    </div>\n" +
            "    <div class=\"post-title\" style=\"border:0.5px solid black\">\n" +
            post[1] +
            "    </div>\n" +
            "    <div class=\"post-text\" style=\"border:0.5px solid black\">\n" +
            post[2] +
            "    </div>\n" +
            "    <div class=\"post-date\" style=\"border:0.5px solid black\">\n" +
            post[3] +
            "    </div>\n" +
            "    <div class=\"post-creator\" style=\"border:0.5px solid black\">\n" +
            post[4] +
            "    </div>\n" +
            "    <div class=\"post-totalview\" style=\"border:0.5px solid black\">\n" +
            post[5] +
            "    </div>\n" +
            "    <div class=\"post-videolink\" style=\"border:0.5px solid black\">\n" +
            post[6] +
            "    </div>\n" +
            "</div>";
        document.getElementById(id).appendChild(newDiv);
        checkYoutube = 1;
     }
}