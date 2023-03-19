<#import "commons.ftlh" as c>

<@c.page>
    <div class="articles">
        <template>
        <article>
            <div class="title"></div>
            <div>By <a class="author"></a></div>
            <div class="body"></div>
            <div class="footer">
                <div class="left">
                    <img src="img/voteup.png" title="Vote Up" alt="Vote Up"/>
                    <span class="positive-score">+173</span>
                    <img src="img/votedown.png" title="Vote Down" alt="Vote Down"/>
                </div>
                <div class="right">
                    <img src="img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                    <a class="time"></a>
                    <img src="img/comments_16x16.png" title="Comments" alt="Comments"/>
                    <a href="#">68</a>
                </div>
            </div>
        </article>
        </template>
    </div>

    <script>
        $(function () {
            ajax({action: "findArticles"},
                function (response) {
                    response["articles"].forEach(function (article) {
                        const $tr = $($(".articles template").prop("content")).clone();
                        $tr.find(".title").text(article["title"]);
                        $tr.find(".body").text(article["text"]);
                        $tr.find(".author").text(response["logins"][article["userId"]]);
                        $tr.find(".footer .right .time").text(article["creationTime"]);
                        $(".articles").append($tr);
                    });
                }
            )
        })
    </script>
</@c.page>
