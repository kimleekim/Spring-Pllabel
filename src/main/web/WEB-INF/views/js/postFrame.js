function postFrame(index, post, id, count) {
    var youtubecheck = 3 + count*4;

    if (index != youtubecheck) {
        var newDiv=document.createElement('div');
        newDiv.setAttribute('class', 'insmain');
        newDiv.setAttribute('style', 'float: left;');
        newDiv.innerHTML =
            "<div class = \"inspic\">" +
            "<img class = \"inspic-section\"" +
            "src=\"" + post[0] +
            "\" alt=\"postImage\">\n" +
            "</div>\n" +
            "<div class = \"ins-body\">\n" +
            "<div class = \"ins-text\">\n" +
            post[2] +
            "</div>\n" +
            "<div class = \"text-date\">" +
            post[1] +
            "</div>\n" +
            "</div>\n" +
            "</div>";
        document.getElementById(id).appendChild(newDiv);
    }
    else {
        var newDiv=document.createElement('div');
        newDiv.setAttribute('class', 'youtmain');
        newDiv.setAttribute('style', 'float: left;');
        newDiv.innerHTML =
            "<div class = \"youtpic\">▶" +
            "<a href=\"" + post[6] + "\">" +
            "<img class = \"youtpic-section\"" +
            "src=\"" + post[0] +
            " \" alt=\"postImage\">\n" +
            "</a>\n" + "</div>\n" +
            "<div class = \"yout-body\">\n" +
            "<div class = \"yout-title\">" +
            post[1] +
            "</div>" +
            "<div class = \"yout-text\">\n" +
            post[2] +
            "</div>\n" +
            "<div class = \"text-date\">" +
            "created by&nbsp;" +
            post[4] +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
            post[5] +
            "&nbsp;views" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
            post[3] +
            "</div>\n" +
            "</div>\n" +
            "</div>";
        document.getElementById(id).appendChild(newDiv);
    }
}

function morePost(post) {
    var count = 0;

    for (i = 0; i < 40; i = i + 4) {
        var newDiv = document.createElement('div');
        newDiv.setAttribute('class', 'more-page-section');
        document.getElementsByTagName('body')[0].appendChild(newDiv);

        newDiv=document.createElement('div');
        newDiv.setAttribute('class', 'morePostLine');
        newDiv.setAttribute('id', 'postLine' + i.toString());
        newDiv.setAttribute('style', 'float: left;');
        document.getElementsByClassName('more-page-section')[i / 4].appendChild(newDiv);

        newDiv = document.createElement('div');
        newDiv.setAttribute('id', 'container_morePost' + i.toString());
        newDiv.setAttribute('layout:fragment', 'content');
        document.getElementById('postLine' + i.toString()).appendChild(newDiv);

        for (j = i; j < i + 4; j++) {
            postFrame(j, post[j], 'container_morePost' + i.toString(), count);
        }
        count++;
    }
}