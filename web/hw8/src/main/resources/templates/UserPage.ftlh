<#import "commons.ftlh" as c>

<@c.page>
    <#if user??>
        <div class="users datatable">
            <table>
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Login</th>
                    <th>Created</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td class="id">${user.id}</td>
                    <td class="login">${user.login}</td>
                    <td class="creationTime">${user.creationTime}</td>
                </tr>
                </tbody>
            </table>
        </div>
    <#else>
        No such user.
    </#if>
</@c.page>
