<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <th><a class="admin">Loading...</a></th>
            </tr>
            </thead>
            <tbody>
            <template>
                <tr>
                    <td class="user-id"></td>
                    <td class="user_login"></td>
                    <td>
                        <div class="button-field">
                            <input class="button-submit" type="submit" value="Loading...">
                        </div>
                    </td>
                </tr>
            </template>
            <tr class="noData">
                <td colspan="3">
                    No data
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <script>
        $(function () {
            const $noData = $(".users .noData");
            $noData.find("td").text("Loading...");

            ajax({action: "findAll"}, function(response) {
                const loggedUser = response["loggedUser"];
                if (loggedUser === undefined || !loggedUser["admin"]) {
                    $(".users tr th .admin").parent().hide();
                } else {
                    $(".users tr th .admin").text("Admin");
                }
                response["users"].forEach(function(user) {
                    $noData.hide();
                    const $tr = $($(".users template").prop("content")).clone();
                    $tr.find(".user-id").text(user["id"]);
                    $tr.find(".user_login").text(user["login"]);
                    if (loggedUser !== undefined && loggedUser["admin"]) {
                        let value;
                        if (user["admin"]) {
                            value = "disable";
                        } else {
                            value = "enable";
                        }
                        $tr.find(".button-submit").attr({
                            class: "button-submit",
                            type: "submit",
                            value,
                            id: user["id"]
                        });
                    } else {
                        $tr.find(".button-field").parent().hide();
                    }
                    $tr.find("tr").attr("id", user["id"]);
                    $(".users tbody").append($tr);
                });
                $(".users .button-submit").click(function() {
                    const $button = $(this);

                    let value = $button.attr("value");
                    if (value === "enable") {
                        value = "disable";
                    } else {
                        value = "enable";
                    }
                    ajax({action: "changeRights", id: $button.attr("id"), admin: $button.attr("value")},
                        function (response) {
                            $button.attr({
                                class: "button-submit",
                                type: "submit",
                                value
                            });
                        });
                });
            });
        })
    </script>
</@c.page>
