
/**
 * ローディング画像をオーバーレイ表示する。
 */
function loading(overlayBox) {
  if($("#" + overlayBox + " div.overlay").length == 0) {
    var loading = "<div class='overlay'><i class='fa fa-refresh fa-spin'></i></div>";
    $("#" + overlayBox).append(loading);
  }
}

/**
 * ローディング画像を非表示にする。
 */
function removeLoading(overlayBox) {
  $("#" + overlayBox + " div.overlay").remove();
}

$(function() {
  $('.fade-out').fadeOut(3000);
});

/**
 * Submit前に実行確認ダイアログを表示する。
 */
function confirmBeforeSubmit(formId, message, action) {
  var form = $("#" + formId);

  if(action == undefined) {
    action = function(){
                loading('content');
                form.submit();
             };
  }

  form.validator({
    custom: {
      contenttype: function($el) {
        if($el.prop('type') == 'file' && $el[0].files[0] != null) {
           var validContentTypes = $el.data("contenttype").split(/[ ,]+/);
           var fileContentType = $el[0].files[0].type;
           if(validContentTypes.indexOf(fileContentType) == -1) {
             return "ファイル形式が正しくありません。" + validContentTypes.join(" ,") + " のみ有効です";
           }
        }
      },
    }
  });

  if (form.validator('validate').has('.has-error').length === 0) {
    $.confirm({
              title: '',
              content: message,
              icon: 'fa fa-info-circle',
              animation: 'scale',
              closeAnimation: 'scale',
              opacity: 0.5,
              buttons: {
                'confirm': {
                  text: 'OK',
                  btnClass: 'btn-blue',
                  action: action
                },
                'cancel': {
                  text: 'キャンセル'
                }
              }
            });
  }
}

/**
 * 情報メッセージを表示する。
 */
function showInfoMessage(message) {
    $.confirm({
        title: '',
        content: message,
        icon: 'fa fa-info-circle',
        animation: 'scale',
        closeAnimation: 'scale',
        opacity: 0.5,
        buttons: {
          'confirm': {
            text: 'OK',
            btnClass: 'btn-blue',
          }
        }
      });
}