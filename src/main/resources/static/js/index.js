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

    // $('#s3DownloadForm').on('submit',function (e) {
    //     e.preventDefault();
    //     var encryptCheckBox = $('input[id=encryptCheckBox]:checked', '#s3DownloadForm').val();
    //     if(encryptCheckBox) {
    //         this.action = "/s3/download-client-side-encrypted-object";
    //     } else {
    //         this.action = "/s3/download";
    //     }
    //     this.submit();
        // var bucketName = $("#bucketName_dl").val();
        // var objectName = $("#objectName_dl").val();
        // var encryptionOption = $('input[name=encryptionRadioOption]:checked', '#s3DownloadForm').val();
        // console.log("Bucket Name: " + bucketName);
        // console.log("Object Name: " + objectName);
        // console.log("Encryption option: " + encryptionOption);
        // var data = $('#s3DownloadForm').serialize();
        // console.log("Serialized Data: " + data);
        // var url = "/s3/download";
        // $.ajax({
        //     type: "GET",
        //     url: url,
        //     data: data,
        //     dataType: "application/json",
        //     success : function(response) {
        //        console.log(response);
        //        // ajax_download(response);
        //     },
        //     error : function ( response ) {
        //         console.log("URL: " + url)
        //         console.log("D: " + JSON.stringify(data))
        //         console.log("R: " + JSON.stringify(response));
        //     }
        // });
        // return false;
    // });
});