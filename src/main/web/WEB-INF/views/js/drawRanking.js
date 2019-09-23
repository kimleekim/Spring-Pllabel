function drawRanking(hashtags, id) {
    var rank = 1;
    while (rank <= 10) {
        var newTr=document.createElement('tr');
        newTr.setAttribute("id", "rankedRow"+rank.toString());
        newTr.setAttribute("style", "height: 40px;");
        document.getElementById(id).appendChild(newTr);

        var rankTd=document.createElement('td');
        rankTd.setAttribute("id", "ranking"+rank.toString());
        rankTd.setAttribute("style", "height: 40px; text-align: center;");
        rankTd.innerHTML = rank.toString();
        document.getElementById("rankedRow"+rank.toString()).appendChild(rankTd);

        var tagTd=document.createElement('td');
        tagTd.setAttribute("style", "height: 40px;");
        tagTd.innerText = hashtags[rank - 1];
        document.getElementById("rankedRow"+rank.toString()).appendChild(tagTd);

        rank++;
    }
}