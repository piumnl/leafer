function upload_img(editor, e, url) {
    var fileList = e.dataTransfer.files;

    if (fileList.length > 1){
        alert('only one image is allowed!');
        return false;
    }

    if(fileList[0].type.indexOf('image') === -1){
        alert("not image！");
        return false;
    }

    var img = new FormData();
    img.append('file', fileList[0]);

    $.ajax({
        type: "post",
        url: url,
        data: img,
        processData : false,
        contentType : false,
        success: function (data) {
            console.log(data)
            // editor.replaceRange("![](" + data['uri'] + ")", {line: editor.getCursor().line, ch: editor.getCursor().ch});
            editor.replaceRange("![](" + data + ")", {line: editor.getCursor().line, ch: editor.getCursor().ch});
        },
        error: function (data) {
            alert("upload failed")
        },
        // add csrf header and token
        beforeSend: function (xhr) {
            let header = $("meta[name='_csrf_header']").attr("content");
            let token = $("meta[name='_csrf']").attr("content");
            xhr.setRequestHeader(header, token);
        }
    });
}
