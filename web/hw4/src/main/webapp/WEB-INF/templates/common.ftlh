<#macro page>
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>Codeforces</title>
        <link rel="stylesheet" href="/css/normalize.css">
        <link rel="stylesheet" href="/css/style.css">
        <link rel="stylesheet" href="/css/user.css">
        <link rel="stylesheet" href="/css/users.css">
        <link rel="stylesheet" href="/css/posts.css">
        <link rel="icon" href="/favicon.ico">
    </head>
    <body>
    <@header/>
    <div class="middle">
        <@sidebar/>
        <main>
            <#nested/>
        </main>
    </div>
    <@footer/>
    </body>
    </html>
</#macro>

<#-- @ftlvariable name="uri" type="java.lang.String" -->
<#-- @ftlvariable name="user" type="ru.itmo.web.hw4.model.User" -->
<#macro header>
    <header>
        <a href="#" class="logo"><img alt="Codeforces" title="Codeforces"
                                      src="/img/codeforces-logo-with-telegram.png"></a>
        <div class="languages">
            <a href="#"><img alt="In English" title="In English"
                             src="/img/gb.png"></a>
            <a href="#"><img alt="In Russian" title="In Russian"
                             src="/img/ru.png"></a>
        </div>
        <div class="enter-or-register-box">
            <#if user??>
                <@userlink user true/>
                |
                <a href="/logout">Logout</a>
            <#else>
                <a href="/enter">Enter</a>
                |
                <a href="/register">Register</a>
            </#if>
        </div>
        <nav>
            <#assign items=[
            {"href": "/index", "name": "Home"},
            {"href": "/misc/help", "name": "Help"},
            {"href": "/contests", "name": "Contests"},
            {"href": "/users", "name": "Users"}
            ]/>
            <ul>
                <#list items as item>
                    <li>
                        <#if item.href == uri>
                            <a href="${item.href}" class="current_page_tab">${item.name}</a>
                        <#else>
                            <a href="${item.href}">${item.name}</a>
                        </#if>
                    </li>
                </#list>
            </ul>
        </nav>
    </header>
</#macro>

<#-- @ftlvariable name="post" type="ru.itmo.web.hw4.model.Post" -->
<#macro sidebar>
    <aside>
        <#list posts as post>
            <section>
                <div class="header">Post #${post.id}</div>
                <div class="body">
                    <@editPost post.text 350 true/>
                </div>
                <div class="footer">
                    <a href="/post?post_id=${post.id}">View all</a>
                </div>
            </section>
        </#list>
    </aside>
</#macro>

<#macro footer>
    <footer>
        2010-2021 &copy; by Mike Mirzayanov
    </footer>
</#macro>

<#macro editPost text lim crop>
    <#assign lines = text?split("\n")>
    <#assign count = lim>
    <#list lines as line>
        <#if crop && line?length gt count>
            ${line?substring(0, count - 1)}...
            <#break>
        <#else>
            <p>${line}</p>
            <#if crop>
                <#assign count-=line?length>
            </#if>
        </#if>
    </#list>
</#macro>

<#macro createPost post lim crop>
    <article>
        <div class="title">${post.title}</div>
        <div class="author">By <@handlelink findBy(users, "id", post.user_id)/></div>
        <div class="body">
            <@editPost post.text lim crop/>
        </div>
        <div class="footer">
            <div class="left">
                <img src="img/voteup.png" title="Vote Up" alt="Vote Up"/>
                <span class="positive-score">+173</span>
                <img src="img/votedown.png" title="Vote Down" alt="Vote Down"/>
            </div>
            <div class="right">
                <img src="img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                2 days ago
                <img src="img/comments_16x16.png" title="Comments" alt="Comments"/>
                <a href="#">68</a>
            </div>
        </div>
    </article>
</#macro>

<#macro userlink user nameOnly>
<#-- @ftlvariable name="user" type="ru.itmo.web.hw4.model.User" -->
    <#if nameOnly>
        <a href="/user?handle=${user.handle}">${user.name}</a>
    <#else>
        <a href="/user?handle=${user.handle}" style="font-weight: bold" class="${user.color}-color">${user.name}</a>
    </#if>
</#macro>

<#macro handlelink user>
<#-- @ftlvariable name="user" type="ru.itmo.web.hw4.model.User" -->
    <a href="/user?handle=${user.handle}" class="${user.color}-color">${user.handle}</a>
</#macro>

<#function findBy items key value>
    <#list items as item>
        <#if item[key] == value>
            <#return item/>
        </#if>
    </#list>
</#function>

<#function next items value>
    <#list items as item>
        <#if item["handle"] == value>
            <#if item_has_next>
                <#return items[item_index + 1]/>
            </#if>
        </#if>
    </#list>
</#function>

<#function prev items value>
    <#list items as item>
        <#if item["handle"] == value>
            <#if item_index gt 0>
                <#return items[item_index - 1]/>
            </#if>
        </#if>
    </#list>
</#function>

<#function countPosts user_id>
    <#assign count = 0/>
    <#list posts as post>
        <#if post.user_id == user_id>
            <#assign count++/>
        </#if>
    </#list>
    <#return count/>
</#function>