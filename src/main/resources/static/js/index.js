$(function () {
    $('#s3_form').on('submit',function (e) {
        e.preventDefault();
        // alert("TEST");
        var bucketName = $("#bucketName").val();
        // var fileInput = document.getElementById("exampleInputFile");
        var fileInput = $("#fileInput").val();
        // var isEncrypted = $("#encryptCheckBox").is(":checked");
        var encryptionOption = $('input[name=encryptionRadioOption]:checked', '#s3_form').val();

        console.log("Bucket Name: " + bucketName);
        console.log("File Name: " + fileInput);
        // console.log("Encrypt: " + isEncrypted);
        console.log("Encryption option: " + encryptionOption);
        // var fReader = new FileReader();
        // fReader.readAsDataURL(fileInput.files[0]);
        // fReader.onloadend = function(event){
        //     var file_name = event.target;
        // var data = { "bucket_name" :  bucket_name, "file_name" : file_name};
        var data = $('#s3_form').serialize();
        console.log("Serialized Data: " + data);
        var url = "";
        if(encryptionOption == 'SSE-KMS') {
            url = "/s3/sse-kms-upload"
        } else {
            url = "/s3/cse-kms-upload"
        }
        var form = new FormData($("#s3_form")[0]);
        $.ajax({
            type: "PUT",
            url: url + "?" + data,
            data: form,
            // dataType: "application/json",
            // dataType: "multipart/form-data",
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
    // }
    });
});



function upload(evt) {
    var bucket_name = $("#exampleSelect1").val();
    // var file_name = document.getElementById("exampleInputFile");
    var file_name = $("#exampleInputFile").val();
    var isEncrypted = $("#encryptCheckBox").is(":checked");
    console.log("Bucket Name: " + bucket_name);
    console.log("File Name: " + file_name);
    console.log("Encrypt: " + isEncrypted);
    var url = "/s3/upload";
    var data = { "bucket_name" :  bucket_name, "file_name" : file_name};
    // var fReader = new FileReader();
    // fReader.readAsDataURL(file_name.files[0]);
    // fReader.onloadend = function(event){
    //     console.log(event.target.result);
    // }
    if(isEncrypted) {
        url = "/s3/uploadSecure"
    }
    $.ajax({
        type: "PUT",
        url: url,
        data: data,
        dataType: "application/json",
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
}