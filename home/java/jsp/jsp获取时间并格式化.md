jsp页面获取时间和格式化 : 

<c:set var="nowDate" value="<%=System.currentTimeMillis()%>"></c:set>
<fmt:parseDate value="${rfqQuotationComplexVo.rfqRequestComplexVo.requestVo.quotationEndDate}" var="parsedEmpDate"  pattern="yyyy-MM-dd HH:mm" />
parsedEmpDate.getTime() > nowDate  
js获取指定时间的毫秒数 new Date('2017-06-19 18:24').getTime();