<#import "commons.ftlh" as c>

<@c.page>
    <div class="posting form-box">
        <div class="header">Create post</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden" name="action" value="posting"/>
                <div class="field">
                    <div class="name">
                        <label for="title">Title</label>
                    </div>
                    <div class="value">
                        <input id="title" name="title"/>
                    </div>
                </div>
                <div class="field">
                    <div class="name">
                        <label for="text">Text</label>
                    </div>
                    <div class="value">
                        <textarea id="text" name="text"></textarea>
                    </div>
                </div>
                <div class="error"></div>
                <div class="button-field">
                    <input type="submit" value="Post">
                </div>
            </form>
        </div>
    </div>

    <script>
        $(function () {
            $(".posting form").submit(function () {
                const title = $(this).find("input[name='title']").val();
                const text = $(this).find("textarea[name='text']").val();
                const $error = $(this).find(".error");

                ajax({action: "posting", title, text},
                    function (response) {

                    }, $error
                );

                return false;
            });
        })
    </script>
</@c.page>