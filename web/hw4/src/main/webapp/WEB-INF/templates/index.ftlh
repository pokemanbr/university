<#import "./common.ftlh" as c>

<#-- @ftlvariable name="post" type="ru.itmo.web.hw4.model.Post" -->
<@c.page>
    <#list posts?reverse as post>
        <@c.createPost post 350 true/>
    </#list>
</@c.page>
