<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%
    request.setCharacterEncoding("utf-8");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
</head>
<body>
<h1>
    2번째 페이지
</h1>
<p> overall에 해당 지하철역이 있다!</p>
<p> 검색 결과 : ${station} </p>
<p> 누구랑 많이 가지? : ${withwho}</p>
<input type="button" onclick="location.href='/'" value="초기 화면으로">
<p> 연관 해시태그 TOP5</p>
<p>1위 : ${top5_hashtag[0]}</p>
<p>2위 : ${top5_hashtag[1]}</p>
<p>3위 : ${top5_hashtag[2]}</p>
<p>4위 : ${top5_hashtag[3]}</p>
<p>5위 : ${top5_hashtag[4]}</p>
</form>
</body>
</html>
