<%--
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>

<%-- 
    This is a custom short view render for educational background.
    The variable individual is the OBJECT of the property statement to be rendered. -- 
    In this JSP that is the  Educational Background, not the Person, Organization or DegreeType
 --%>

<c:choose>
    <c:when test="${!empty individual}">                
        <c:choose>
            <%-- SUBJECT is a Person  --%>
            <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#educationalBackground'}">         
            
                <%-- Degree type and major --%>
                <c:set var="degreeStr" value="" />
                <c:set var="degreeType" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#degreeTypeAwarded'].objectPropertyStatements[0].object}"/>
                <c:set var="degreeAbbreviation" value="${degreeType.dataPropertyMap['http://vivoweb.org/ontology/core#degreeAbbreviation'].dataPropertyStatements[0].data}"/>
                <c:set var="degreeStr" value="${!empty degreeAbbreviation ? degreeAbbreviation : degreeType.name }" />
                <c:set var="degreeMajor" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#majorField'].dataPropertyStatements[0].data}"/>
                <c:if test="${ ! empty degreeMajor }">
                    <c:set var="degreeStr" value="${degreeStr} in ${degreeMajor}" />
                </c:if>
                <c:if test="${ ! empty degreeStr }">
                    <c:set var="degreeStr"><p:process>${degreeStr}</p:process></c:set>
                </c:if>
                
                <%-- Organization granting degree --%>
                <c:set var="selectedOrganization" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#organizationGrantingDegree'].objectPropertyStatements[0].object}"/>              
                <c:if test="${ ! empty selectedOrganization }">                        
                    <c:url var="selectedOrganizationURL" value="/individual">
                        <c:param name="uri" value="${selectedOrganization.URI}"/>
                    </c:url>
                    <c:set var="selectedOrganizationStr" ><a href='${selectedOrganizationURL}'><p:process>${selectedOrganization.name}</p:process></a></c:set>
                </c:if>
                
                <%-- Optional department/school to organization --%>
                <c:set var="degreeDeptOrSchool" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#departmentOrSchool'].dataPropertyStatements[0].data}"/>
                <c:if test="${ ! empty degreeDeptOrSchool }"> 
                </c:if>
                        
                <%-- Year of degree --%>                             
                <c:set var="year" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}"/>
                <c:if test="${ ! empty year }">
                    <c:set var="year"><p:process>${year}</p:process></c:set>
                </c:if>
                
                <%-- Supplemental information --%>
                <c:set var="degreeSupplementalInfo" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#supplementalInformation'].dataPropertyStatements[0].data}"/>
                <c:if test="${ ! empty degreeSupplementalInfo }">
                    <c:set var="degreeSupplementalInfo"><p:process>${degreeSupplementalInfo}</p:process></c:set>
                </c:if>           
                
                <%-- Build the output string --%>
                <c:choose>
                    <c:when test="${ ! empty degreeStr }">
                        ${degreeStr}
                        <c:if test="${ ! empty selectedOrganizationStr}">, ${selectedOrganizationStr}</c:if>
                        <c:if test="${ ! empty degreeDeptOrSchool}">, ${degreeDeptOrSchool}</c:if>
                        <c:if test="${ ! empty year }">, ${year}</c:if>
                        <c:if test="${ ! empty degreeSupplementalInfo }">, ${degreeSupplementalInfo}</c:if>             
                    </c:when>
                    <c:otherwise>
                        <a href="${objLink}"><p:process>educational background ${individual.name}</p:process></a>
                    </c:otherwise>
                </c:choose>
            </c:when>
              
            <%-- SUBJECT is a Degree Type  --%>                     
            <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#awardedTo'}">             
                <c:set var="year" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}"/>
                <c:set var="degreeMajor" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#majorField'].dataPropertyStatements[0].data}"/>
                
                <c:set var="selectedOrganization" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#organizationGrantingDegree'].objectPropertyStatements[0].object}"/>
                <c:set var="selectedOrganizationName" value="${selectedOrganization.name}"/>                                
    
                <c:set var="person" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#educationalBackgroundOf'].objectPropertyStatements[0].object}"/>
                <c:set var="personName" value="${person.name}"/>
                <c:url var="personURL" value="/individual"><c:param name="uri" value="${person.URI}"/></c:url>
                <c:set var="personLink" ><a href='${personURL}'><p:process>${personName}</p:process></a></c:set>
                
                <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
                
                <c:choose>
                    <c:when test="${! empty personName && ! empty year && ! empty degreeMajor && ! empty selectedOrganizationName  }">                      
                        ${personLink} <p:process> in ${degreeMajor}, ${selectedOrganizationName}, ${year}</p:process>
                    </c:when>
                    <c:when test="${! empty personName && empty year && ! empty degreeMajor && ! empty selectedOrganizationName  }">
                        ${personLink} <p:process> in ${degreeMajor}, ${selectedOrganizationName}</p:process>                                                
                    </c:when>
                    <c:when test="${! empty personName && empty year && ! empty degreeMajor && empty selectedOrganizationName  }">
                        ${personLink} <p:process> in ${degreeMajor}</p:process>                                             
                    </c:when>
                    <c:when test="${! empty personName && ! empty year && empty degreeMajor && ! empty selectedOrganizationName  }">
                        ${personLink} <p:process> ${selectedOrganizationName}, ${year}</p:process>                                              
                    </c:when>
                    <c:otherwise>                       
                        <a href="${objLink}"><p:process>educational background ${individual.name}</p:process></a>                        
                    </c:otherwise>
                </c:choose>
            </c:when>                           
    
        <%-- The predicate was not one of the predicted ones, so create a normal link --%>  
        <c:otherwise>           
            <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
            <a href="${objLink}"><p:process>${individual.name}</p:process></a>
        </c:otherwise>
    </c:choose>         

            
    </c:when>
    
    <%-- This clause is when there is no object individual defined, it should never be reached. --%>
    <c:otherwise>
        <c:out value="Nothing to draw in edBackgroundShortView"/>
    </c:otherwise>
</c:choose>
