<#-- @ftlvariable name="talks" type="ru.itmo.wp.model.domain.Talk[]" -->
<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#-- @ftlvariable name="error" type="java.lang.String" -->
<#import "commons.ftlh" as c>

<@c.page>
    <form method="post" action="">
        <input type="hidden" name="action" value="send"/>
        <div class="form-box">
            <label>
                <select name="targetUser" class="select">
                    <#list users as u>
                        <option>${u.login}</option>
                    </#list>
                </select>
            </label>
            <div class="name">
            <label>
                <textarea name="text" class="text"></textarea>
            </label>
            </div>
            <#if error??>
                <div class="error">
                    ${error!}
                </div>
            </#if>
            <div class="button-field">
                <input type="submit" value="Send">
            </div>
        </div>
    </form>
    <div class="datatable">
        <div class="caption">Talks</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>От кого</th>
                <th>Кому</th>
                <th>Текст</th>
                <th>Когда</th>
            </tr>
            </thead>
            <tbody>
            <#list talks as talk>
                <tr>
                    <#assign sourceUser = findBy(users, "id", talk.sourceUserId)>
                    <#assign targetUser = findBy(users, "id", talk.targetUserId)>
                    <td>${talk.id}</td>
                    <td>${sourceUser.login}</td>
                    <td>${targetUser.login}</td>
                    <td>${talk.text}</td>
                    <td>${talk.creationTime}</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</@c.page>

<#function findBy items key value>
    <#list items as item>
        <#if item[key] == value>
            <#return item/>
        </#if>
    </#list>
</#function>