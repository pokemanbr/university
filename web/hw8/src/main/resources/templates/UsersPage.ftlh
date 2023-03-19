<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <th>Created</th>
                <#if loggedUser??>
                    <th>Status</th>
                </#if>
            </tr>
            </thead>
            <tbody>
            <#if users?size==0>
                <tr class="noData">
                    <#if loggedUser??>
                        <td colspan="4">
                            No data
                        </td>
                    <#else>
                        <td colspan="3">
                            No data
                        </td>
                    </#if>
                </tr>
            <#else>
                <#list users as user>
                    <tr>
                        <td class="id">${user.id}</td>
                        <td class="login">${user.login}</td>
                        <td class="creationTime">${user.creationTime}</td>
                        <#if loggedUser??>
                            <td>
                                <form method="post" action="">
                                    <input type="hidden" name="id" value="${user.id}"/>
                                    <input type="hidden" name="disabled" value="${user.disabled?then('true' 'false')}"/>
                                    <div class="button-field">
                                        <#if user.disabled>
                                            <input class="button-submit" type="submit" value="Enable">
                                        <#else>
                                            <input class="button-submit" type="submit" value="Disable">
                                        </#if>
                                    </div>
                                </form>
                            </td>
                        </#if>
                    </tr>
                </#list>
            </#if>
            </tbody>
        </table>
    </div>
</@c.page>
