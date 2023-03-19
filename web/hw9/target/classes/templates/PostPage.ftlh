<#import "commons.ftlh" as c>

<@c.page>
    <#if post??>
        <@c.post post false/>
        <#if commentForm??>
            <div class="form">
                <div class="header">Write Comment</div>
                <div class="body">
                    <form method="post" action="">
                        <input type="hidden" name="action" value="writeComment"/>
                        <div class="field">
                            <div class="name">
                                <label for="text">Text</label>
                            </div>
                            <div class="value">
                                <textarea id="text" name="text">${commentForm.text!}</textarea>
                            </div>
                            <@c.error "commentForm.text"/>
                        </div>
                        <div class="button-field">
                            <input type="submit" value="Write">
                        </div>
                    </form>
                </div>
            </div>
        </#if>
        <#list post.comments as comment>
            <@c.comment comment=comment/>
        </#list>
    <#else>
        No such post.
    </#if>
</@c.page>
