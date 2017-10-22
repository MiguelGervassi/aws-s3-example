$(document).ready(function() {
    $("#bucketName_dl").change(function() {
        sendAjaxRequest();
    });

    function sendAjaxRequest() {
        var bucketName = $("#bucketName_dl").val();
        $.get( "/s3/objects?bucketName=" + bucketName, function( data ) {
            $("#objectName_dl").empty();
            data.forEach(function(item, i) {
                var option = "<option value = " + item.key + ">" + item.key +  "</option>";
                $("#objectName_dl").append(option);
            });
        });
    };

    $('#s3UploadForm').on('submit',function (e) {
        e.preventDefault();
        var bucketName = $("#bucketName").val();
        var fileInput = $("#fileInput").val();
        var encryptionOption = $('input[name=encryptionRadioOption]:checked', '#s3UploadForm').val();

        console.log("Bucket Name: " + bucketName);
        console.log("File Name: " + fileInput);
        // console.log("Encrypt: " + isEncrypted);
        console.log("Encryption option: " + encryptionOption);
        var data = $('#s3UploadForm').serialize();
        console.log("Serialized Data: " + data);
        var url = "/s3/upload";
        var form = new FormData($("#s3UploadForm")[0]);
        $.ajax({
            type: "PUT",
            url: url + "?" + data,
            data: form,
            processData: false,
            contentType: false,
            success : function(response) {
                console.log(response)
                return "success";
            },
            error : function ( response ) {
                console.log("URL: " + url)
                console.log("D: " + JSON.stringify(data))
                console.log("R: " + JSON.stringify(response));
            }
        });
        return false;
    });
});