function drawRanking(hashtags, id) {
    var rank = 1;
    while (rank <= 10) {
        var newTr=document.createElement('tr');
        newTr.setAttribute("id", "rankingRow"+rank.toString());
        newTr.setAttribute("style", "border: 0.2px solid gray;");
        document.getElementById(id).appendChild(newTr);

        var rankTd=document.createElement('td');
        rankTd.innerText = rank.toString() + "위";
        rankTd.setAttribute("style", "border: 0.2px solid gray;");
        document.getElementById("rankingRow"+rank.toString()).appendChild(rankTd);

        var tagTd=document.createElement('td');
        tagTd.innerText = hashtags[rank - 1];
        tagTd.setAttribute("style", "border: 0.2px solid gray;");
        document.getElementById("rankingRow"+rank.toString()).appendChild(tagTd);

        rank++;
    }
}