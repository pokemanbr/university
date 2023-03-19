<#-- @ftlvariable name="post_id" type="java.lang.Long" -->
<#import "./common.ftlh" as c>

<@c.page>
    <#if post_id??>
        <#assign post = c.findBy(posts, "id", post_id)!/>
    </#if>
    <#if post?has_content>
        <@c.createPost post 0 false/>
    <#else>
        The post doesn't exist
    </#if>
</@c.page>
