window.notify = function (message) {
    $.notify(message, {
        position: "right bottom",
        className: "success"
    });
}

ajax = function (data, success, error = null) {
    $.ajax({
        type: "POST",
        dataType: "json",
        data: data,
        success: function (response) {
            success(response);
            if (response.error) {
                error.text(response.error);
            }
            if (response.redirect) {
                location.href = response.redirect;
            }
        }
    });
}
