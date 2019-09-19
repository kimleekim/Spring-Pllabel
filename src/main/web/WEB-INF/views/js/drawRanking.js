function drawRanking(hashtags, id) {
    var rank = 1;

    while (rank <= 10) {
        var newTr=document.createElement('tr');
        newTr.setAttribute("id", "rankingRow"+rank.toString());
        //tr의 자식으로 [td(순위), td(해시태그명)] 들어감
        document.getElementById(id).appendChild(newTr);
        var rankTd=document.createElement('td');
        rankTd.innerText = rank.toString() + "위";
        document.getElementById("rankingRow"+rank.toString()).appendChild(rankTd);
        var tagTd=document.createElement('td');
        tagTd.innerText = hashtags[rank - 1];
        document.getElementById("rankingRow"+rank.toString()).appendChild(tagTd);
        rank++;
    }
}