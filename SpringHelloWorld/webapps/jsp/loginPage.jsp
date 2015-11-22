<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Spring3Example</title>
<style>
.error {
color: #EF1313;
font-style: italic;
}
</style>
</head>
<body>

<form:form action="login.html" commandName="userDetails">
<table>
<tr>
	<td><font face="verdana" size="2px">User</font></td>
	<td>:</td>
	<td>
	<font face="verdana" size="2">
	<form:input path="user" /> <form:errors path="user" class="error"></form:errors>
	</font>
	</td>
</tr>
<tr>
	<td><font face="verdana" size="2px">Email</font></td>
	<td>:</td>
	<td>
	<font face="verdana" size="2">
	<form:input path="email" /> <form:errors path="email" class="error"></form:errors>
	</font>
	</td>
</tr>
<tr>
	<td><font face="verdana" size="2px">Phone</font></td>
	<td>:</td>
	<td>
	<font face="verdana" size="2">
	<form:input path="phone" /> <form:errors path="phone" class="error"></form:errors>
	</font>
	</td>
</tr>
<tr>
	<td><font face="verdana" size="2px">Blog</font></td>
	<td>:</td>
	<td>
	<font face="verdana" size="2">
	<form:input path="blog" /> <form:errors path="blog" class="error"></form:errors>
	</font>
	</td>
</tr>
<tr>
	<td>
	<input type="submit" value="Submit" />
	</td>
</tr>
</table>
</form:form>
</body>
</html>