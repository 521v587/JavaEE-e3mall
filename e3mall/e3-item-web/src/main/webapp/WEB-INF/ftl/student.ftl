<html>
<head>
<title>测试FreeMarker</title>
</head>
<body>
学生信息:<br/>
学生姓名：${student.name}&nbsp;&nbsp;&nbsp;&nbsp;
学生年龄：${student.age}&nbsp;&nbsp;&nbsp;&nbsp;
地址：${student.address}&nbsp;&nbsp;&nbsp;&nbsp;

<br/>
<br/>

<table border="1">
	<tr>
		<th>下标</th>
		<th>序号</th>
		<th>姓名</th>
		<th>年龄</th>
		<th>地址</th>
	</tr>
	<#list studentList as student>
		<#if student_index % 2 == 0>
			<tr bgcolor="red">
		<#else>
			<tr bgcolor="blue">
		</#if>
				<th>${student_index}</th>
				<th>序号</th>
				<th>${student.name}</th>
				<th>${student.age}</th>
				<th>${student.address}</th>
			</tr>
	</#list>
</table>

当前时间为：${date?string("yyyy/MM/dd HH:mm:ss")}<br/>
取null值(如果为null的话，!后为默认值)：${val!"hell"}<br/>
引入模板hello.ftl：<#include "hello.ftl">

</body>
</html>