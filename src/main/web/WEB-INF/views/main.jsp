<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% request.setCharacterEncoding("utf-8"); %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>HOME</title>
</head>
<body>
<h2>main 페이지!</h2>
<form action="/" method="POST" id="stationSearch">
  <div>지하철역 : <input type="text" name="station" value=""></div>
  <input type="submit" value="검색"/>
</form>
<h4>맛집 랭킹 3위</h4>
<table>
  <tr>
    <td> 1위 </td>
    <td> ${top3_restaurant[0]} </td>
  </tr>
  <tr>
    <td> 2위 </td>
    <td> ${top3_restaurant[1]} </td>
  </tr>
  <tr>
    <td> 3위 </td>
    <td> ${top3_restaurant[2]} </td>
  </tr>
</table>
<h4>핫플 랭킹 3위</h4>
<table>
  <tr>
    <td> 1위 </td>
    <td> ${top3_place[0]} </td>
  </tr>
  <tr>
    <td> 2위 </td>
    <td> ${top3_place[1]} </td>
  </tr>
  <tr>
    <td> 3위 </td>
    <td> ${top3_place[2]} </td>
  </tr>
</table>
<h4>실시간 랭킹 3위</h4>
<table>
  <tr>
    <td> 1위 </td>
    <td> ${top3_station[0]} </td>
  </tr>
  <tr>
    <td> 2위 </td>
    <td> ${top3_station[1]} </td>
  </tr>
  <tr>
    <td> 3위 </td>
    <td> ${top3_station[2]} </td>
  </tr>
</table>
</body>

</html>




