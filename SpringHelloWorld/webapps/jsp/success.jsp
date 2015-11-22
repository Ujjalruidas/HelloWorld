<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<style>
table td{font-family:verdana;font-size: 12px;}
</style>
</head>

<body>
<font face="verdana" size="2">Welcome Mr. <b>${lfobj.user}</b>,<br>
Validations Success..!<br><br>
<u>You Entered</u><br>
</font>

<table>
<tr><td>Email</td><td>${lfobj.email}</td></tr>
<tr><td>Phone</td><td>${lfobj.phone}</td></tr>
<tr><td>Website</td><td>${lfobj.blog}</td></tr>
</table>
</body>
</html>