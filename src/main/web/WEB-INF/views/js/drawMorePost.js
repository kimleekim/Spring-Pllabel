function morePost(post) {
    var count = 1;

    for (i = 0; i < 40; i = i + 4) {
        var newDiv=document.createElement('div');
        newDiv.setAttribute('id', 'postLine' + i.toString());
        document.getElementsByTagName('body')[0].appendChild(newDiv);

        newDiv = document.createElement('div');
        newDiv.setAttribute('id', 'container_morePost' + i.toString());
        newDiv.setAttribute('layout:fragment', 'content');
        document.getElementById('postLine' + i.toString()).appendChild(newDiv);

        for (j = i; j < i + 4; j++) {
            postFrame(j, post, 'container_morePost' + i.toString(), count);
            count++;
        }
    }
}