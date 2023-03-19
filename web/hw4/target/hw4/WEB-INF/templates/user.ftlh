<#-- @ftlvariable name="handle" type="java.lang.String" -->
<#-- @ftlvariable name="user_id" type="java.lang.Long" -->
<#import "./common.ftlh" as c>

<@c.page>
    <#if handle??>
        <#assign u = c.findBy(users, "handle", handle)!/>
    <#elseif user_id??>
        <#assign u = c.findBy(users, "id", user_id)!/>
    </#if>
    <#if u?has_content>
        <div class="user-info">
            <div class="name">
                <@c.userlink u false/>
            </div>
            <div class="list">
                <li>id: ${u.id}</li>
                <li>handle: ${u.handle}</li>
                <li>name: ${u.name}</li>
                <li><a href="/posts?user_id=${u.id}">Posts: ${c.countPosts(u.id)}</a></li>
            </div>
            <div class="pic">
                <a href="/"><img src="img/no-pic.png" alt="Avatar" title="Avatar"/></a>
            </div>
        </div>
        <div class="arrows">
            <#assign up = c.prev(users, u.handle)!/>
            <#if up?has_content>
                <a href="/user?handle=${up.handle}"><-</a>
            <#else>
                <-
            </#if>

            <#assign un = c.next(users, u.handle)!/>
            <#if un?has_content>
                <a href="/user?handle=${un.handle}">-></a>
            <#else>
                ->
            </#if>
        </div>
    <#else>
        No such user
    </#if>
</@c.page>
