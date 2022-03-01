<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="constants.AttributeConst" %>

<c:if test="${errors != null}">
    <div id="flush_error">
        入力内容にエラーがあります。<br />
        <c:forEach var="error" items="${errors}">
            ・<c:out value="${error}" /><br />
        </c:forEach>

    </div>
</c:if>
<fmt:parseDate value="${reaction.reactionDate}" pattern="yyyy-MM-dd" var="reactionDay" type="date" />
<label for="${AttributeConst.REA_DATE.getValue()}">日付</label><br />
<input type="date" name="${AttributeConst.REA_DATE.getValue()}" value="<fmt:formatDate value='${reactionDay}' pattern='yyyy-MM-dd' />" />
<br /><br />

<label for="name">氏名</label><br />
<c:out value="${sessionScope.login_employee.name}" />
<br /><br />

<label for="${AttributeConst.REA_TITLE.getValue()}">タイトル</label><br />
<input type="text" name="${AttributeConst.REA_TITLE.getValue()}" value="${reaction.title}" />
<br /><br />

<label for="${AttributeConst.REA_CONTENT.getValue()}">内容</label><br />
<textarea name="${AttributeConst.REA_CONTENT.getValue()}" rows="10" cols="50">${reaction.content}</textarea>
<br /><br />
<input type="hidden" name="${AttributeConst.REA_ID.getValue()}" value="${reaction.id}" />
<input type="hidden" name="${AttributeConst.TOKEN.getValue()}" value="${_token}" />
<button type="submit">投稿</button>