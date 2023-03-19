<#-- @ftlvariable name="articles" type="ru.itmo.wp.model.domain.Article[]" -->
<#-- @ftlvariable name="article" type="ru.itmo.wp.model.domain.Article" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="articles datatable">
        <div class="caption">Articles</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Title</th>
                <th>Privacy</th>
            </tr>
            </thead>
            <tbody>
            <#list articles as article>
                <tr>
                    <td class="id">${article.id}</td>
                    <td class="title">${article.title}</td>
                    <td>
                        <div class="button-field">
                            <@button article/>
                        </div>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>

    <script>
        $(".articles tr .button-field .button-submit").click(function() {
            const $button = $(this);
            ajax({action: "changePrivacy", postId: $button.attr("id"), hidden: $button.attr("value")},
                function (response) {
                    let value = $button.attr("value");
                    if (value === "Show") {
                        value = "Hide";
                    } else {
                        value = "Show";
                    }
                    $button.attr({
                        class: "button-submit",
                        type: "submit",
                        value
                    });
                });
        });
    </script>
</@c.page>

<#macro button article>
    <#if article.hidden>
        <input class="button-submit" type="submit" value="Show" id="${article.id}">
    <#else>
        <input class="button-submit" type="submit" value="Hide" id="${article.id}">
    </#if>
</#macro>