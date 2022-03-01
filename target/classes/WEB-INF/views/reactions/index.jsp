<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="constants.ForwardConst" %>

<c:set var="actRep" value="${ForwardConst.ACT_REA.getValue()}" />
<c:set var="commIdx" value="${ForwardConst.CMD_INDEX.getValue()}" />
<c:set var="commShow" value="${ForwardConst.CMD_SHOW.getValue()}" />
<c:set var="commNew" value="${ForwardConst.CMD_NEW.getValue()}" />

<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
        <c:if test="${flush != null}">
            <div id="flush_success">
                <c:out value="${flush}"></c:out>
            </div>
        </c:if>
        <h2>リアクション　一覧</h2>
        <table id="reaction_list">
            <tbody>
                <tr>
                    <th class="reaction_name">氏名</th>
                    <th class="reaction_date">日付</th>
                    <th class="reaction_title">タイトル</th>
                    <th class="reaction_action">操作</th>
                </tr>
                <c:forEach var="reaction" items="${reactions}" varStatus="status">
                    <fmt:parseDate value="${reaction.reactionDate}" pattern="yyyy-MM-dd" var="reactionDay" type="date" />

                    <tr class="row${status.count % 2}">
                        <td class="reaction_name"><c:out value="${reaction.employee.name}" /></td>
                        <td class="reaction_date"><fmt:formatDate value='${reactionDay}' pattern='yyyy-MM-dd' /></td>
                        <td class="reaction_title">${reaction.title}</td>
                        <td class="reaction_action"><a href="<c:url value='?action=${actRep}&command=${commShow}&id=${reaction.id}' />">詳細を見る</a></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div id="pagination">
            （全 ${reactions_count} 件）<br />
            <c:forEach var="i" begin="1" end="${((reactions_count - 1) / maxRow) + 1}" step="1">
                <c:choose>
                    <c:when test="${i == page}">
                        <c:out value="${i}" />&nbsp;
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value='?action=${actRep}&command=${commIdx}&page=${i}' />"><c:out value="${i}" /></a>&nbsp;
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
        <p><a href="<c:url value='?action=${actRep}&command=${commNew}' />">新規リアクションの登録</a></p>

    </c:param>
</c:import>