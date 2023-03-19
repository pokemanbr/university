<#-- @ftlvariable name="user_id" type="java.lang.Long" -->
<#import "./common.ftlh" as c>

<#-- @ftlvariable name="post" type="ru.itmo.web.hw4.model.Post" -->
<@c.page>
    <#assign have = false/>
    <#if user_id??>
        <#assign u = c.findBy(users, "id", user_id)!/>
    </#if>
    <#if u?has_content>
        <#list posts?reverse as post>
            <#if user_id == post.user_id>
                <@c.createPost post 350 true/>
                <#assign have = true/>
            </#if>
        </#list>
    <#else>
        The user doesn't exist
        <#assign have = true/>
    </#if>
    <#if have == false>
        This user doesn't have posts
    </#if>
</@c.page>
