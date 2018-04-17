/** 選択中の組織コード */
var selectedOrgCd = '';

/**
 * 組織を検索する。
 */
function findOrg() {
    var formId = 'org-list';

    loading(formId);

    $.ajax( {
          url: '/admin/find-orgs',
          method: 'GET'
          }).done(function(res) {

              removeLoading(formId);

              $('#org-list').empty();

              if(res.results.length > 0) {
                  var tmpl = $.templates('#org-record-tmpl');

                  $.each(res.results, function(index, record){
                     $('#org-list').append(tmpl.render(record))
                  });

              } else {
                  $('#org-list').append("<tr><td><h4>データがありません。</h4></td></tr>");
              }

          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
              removeLoading(formId);
          });
}

/**
 * ユーザを検索する。
 * @param targetOrgCd 対象組織コード
 */
function findUser(targetOrgCd) {
    var formId = 'user-list';

    loading(formId);

    $.ajax( {
          url: '/admin/find-users',
          method: 'GET',
          data: {
              orgCd: targetOrgCd
          }
          }).done(function(res) {
        	  selectedOrgCd = targetOrgCd;
              removeLoading(formId);

              $('#user-list').empty();
              $('#userAllCheck').prop('selected', false);

             if(res.results.length > 0) {
                  var tmpl = $.templates('#user-record-tmpl');

                  $.each(res.results, function(index, record){
                     $('#user-list').append(tmpl.render(record))
                  });

              } else {
                  $('#user-list').append("<tr><td><h4>データがありません。</h4></td></tr>");
              }

          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
              removeLoading(formId);
          });
}

/**
 * 組織登録ダイアログを表示する。
 */
function openRegisterOrg() {
    var $form = $('#modal-org-form');
    $form.modal('show');
    $('#org-register-button').show();
    $('#org-update-button').hide();
    $form.find('#org-cd').attr('readonly', false);
}

/**
 * 組織の登録ボタン押下時の動作。
 */
function handleRegisterOrg() {
    confirmBeforeSubmit("org-register-form", "登録しますか？", registerOrg);
}

/**
 * 組織を登録する。
 */
function registerOrg() {
   var formId = "org-register-form";

   loading(formId);

   $form = $("#" + formId);

   $.ajax( {
          url: '/admin/orgs',
          method: 'POST',
          data: $form.serialize()
          }).done(function(res) {
              removeLoading(formId);
        	  if (res.status == 'NG') {
        		  showInfoMessage(res.message);
        	  } else {
                  showInfoMessage("登録しました");
    	          findOrg();
                  $('#modal-org-form').modal('hide');
        	  }
          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
              removeLoading(formId);
              showInfoMessage("登録に失敗しました");
          });
}

/**
 * 組織更新ダイアログを表示する。
 */
function openUpdateOrg(orgCd) {
    var $form = $('#modal-org-form');
    $form.find('#org-cd').attr('readonly', true);
    $.ajax({
          url: '/admin/find-org',
          method: 'GET',
          data: {
	          orgCd: orgCd
	      }
	      }).done(function(res) {
	          $form.find('#org-cd').val(res.results.orgCd);
	          $form.find('#org-name').val(res.results.orgName);
	          $form.find('#disp-seq').val(res.results.dispSeq);
	          $form.modal('show');
	          $('#org-register-button').hide();
	          $('#org-update-button').show();
	      }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
	          removeLoading(formId);
	          showInfoMessage("通信エラーが発生しました");
	          $form.modal('show');
          });
}

/**
 * 組織の更新ボタン押下時の動作。
 */
function handleUpdateOrg() {
    confirmBeforeSubmit("org-register-form", "更新しますか？", updateOrg);
}

/**
 * 組織を更新する。
 */
function updateOrg() {
	   var formId = "org-register-form";

	   loading(formId);

	   $form = $("#" + formId);

	   $.ajax( {
	          url: '/admin/org-update',
	          method: 'POST',
	          data: $form.serialize()
	          }).done(function(res) {
	             removeLoading(formId);
	             showInfoMessage("更新しました");
	             $('#org-register-button').show();
	             $('#org-update-button').hide();
	             findOrg();
	             $('#modal-org-form').modal('hide');
	          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
	             removeLoading(formId);
	             showInfoMessage("更新に失敗しました");
	          });
}

/**
 * 組織の削除処理ボタン押下時の動作。
 */
function handleDeleteOrg(orgCd) {
	selectedOrgCd = orgCd;
	confirmBeforeSubmit("dummy", "削除しますか？", deleteOrg);
}

/**
 * 組織を削除する。
 * @param targetOrgCd 対象の組織コード
 */
function deleteOrg() {
    $.ajax( {
        url: '/admin/org-delete',
        method: 'POST',
        data: {
            orgCd: selectedOrgCd
        }
        }).done(function(res) {
            showInfoMessage("削除しました");
            findOrg();
        }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
            showInfoMessage("削除に失敗しました");
        });
}

/**
 * ユーザ登録ダイアログを開く。
 */
function openRegisterUser() {
    var $form = $('#modal-user-form');
    $form.modal('show');
    $('#user-register-button').show();
    $('#user-update-button').hide();
    $form.find('#user-id').attr('readonly', false);
}

/**
 * ユーザ登録ボタン押下時の動作。
 * @returns
 */
function handleRegisterUser() {
   confirmBeforeSubmit("user-register-form", "登録しますか？", registerUser);
}

/**
 * ユーザを登録する。
 */
function registerUser() {
   var formId = "user-register-form";

   loading(formId);

   $form = $("#" + formId);

   $.ajax( {
          url: '/admin/users',
          method: 'POST',
          data: $form.serialize()
          }).done(function(res) {
    		  removeLoading(formId);
        	  if (res.status == 'NG') {
        		  showInfoMessage(res.message);
        	  } else {
        		  showInfoMessage("登録しました");
        		  findUser(selectedOrgCd);
        		  $('#modal-user-form').modal('hide');
        	  }
          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
              removeLoading(formId);
              showInfoMessage("登録に失敗しました");
          });
}

/**
 * ユーザ更新ダイアログを開く。
 * @param targetUserId 対象ユーザID
 */
function openUpdateUser(targetUserId) {
    var $form = $('#modal-user-form');
    $form.find('#user-id').attr('readonly', true);
    $.ajax({
          url: '/admin/find-user',
          method: 'GET',
          data: {
	          userId: targetUserId
	      }
	      }).done(function(res) {
	          $form.find('#user-id').val(res.results.userId);
	          $form.find('#name').val(res.results.name);
	          $form.find('#mail').val(res.results.mail);
	          $form.find('#line-id').val(res.results.lineId);
	          // optionタグを挿入しているのは、外部からSelect2に値を設定する際に必要なため
	          $form.find('#user-org-cd').append('<option value="' + res.results.orgCd + '" selected="selected">' + res.orgName + '</option>').change();
	          $form.find('#auth-cd').append('<option value="' + res.results.authCd + '" selected="selected">' + res.authName + '</option>').change();
	          if (res.results.managerId != null) {
	        	  $form.find('#manager-id').append('<option value="' + res.results.managerId + '" selected="selected">' + res.managerName + '</option>').change();
	          }
	          $form.modal('show');
	          $('#user-register-button').hide();
	          $('#user-update-button').show();
	      }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
	          showInfoMessage("通信エラーが発生しました");
	          $form.modal('show');
          });
}

/**
 * ユーザ更新ボタン押下時の動作。
 */
function handleUpdateUser() {
    confirmBeforeSubmit("user-register-form", "更新しますか？", updateUser);
}

/**
 * ユーザを更新する。
 */
function updateUser() {
	   var formId = "user-register-form";

	   loading(formId);

	   $form = $("#" + formId);

	   $.ajax( {
	          url: '/admin/user-update',
	          method: 'POST',
	          data: $form.serialize()
	          }).done(function(res) {
	             removeLoading(formId);
	             showInfoMessage("更新しました");
	             $('#user-register-button').show();
	             $('#user-update-button').hide();
	             findUser(selectedOrgCd);
	             $('#modal-user-form').modal('hide');
	          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
	             removeLoading(formId);
	             showInfoMessage("更新に失敗しました");
	          });
}

/**
 * ユーザ削除ボタンを押下した時の動作。
 */
function handleDeleteUsers() {
	if ($('.checkUser:checked').length > 0) {
	    confirmBeforeSubmit("user-delete-form", "選択したユーザを削除しますか？", deleteUsers);
	} else {
		showInfoMessage("ユーザを選択してください");
	}
}

/**
 * ユーザを削除する。
 */
function deleteUsers() {
   var formId = "user-delete-form";
   loading(formId);
   $form = $("#" + formId);
   $.ajax( {
        url: '/admin/user-delete',
        method: 'POST',
        data: $('.checkUser:checked').serialize()
        }).done(function(res) {
            showInfoMessage("削除しました");
            removeLoading(formId);
            findUser(selectedOrgCd);
        }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
            showInfoMessage("削除に失敗しました");
        });
}

/**
 * ユーザ全選択ボタン押下時の動作。
 */
function onUserAllSelecting() {
	if ($('#userAllCheck:checked').length > 0) {
		$('.checkUser').prop('checked', true);
	} else {
		$('.checkUser').prop('checked', false);
	}
}

/**
 * 初期化処理
 */
$(function() {
	function format(item) { return item.text; };

    findOrg();

    $('#org-list > tr').click(function() {
        findUser($(this).data("org-cd"));
    });

    $('.modal').on('hidden.bs.modal', function(event) {
        $(event.target).find('form')[0].reset();
        $(event.target).find('select').val('').trigger('change');
    });

    // selectを高機能化するjQueryのプラグイン"Select2"の適用
    $('#user-org-cd').select2({
        ajax: {
            url: '/admin/orgs/select2',
            dataType: "json",
            delay: 250
        }});

    $('#manager-id').select2({
        ajax: {
            url: '/admin/users/select2',
            dataType: "json",
            delay: 250,
            data: function (params) {
              var query = {
                orgCd: $('#user-org-cd').val(),
                name: params.term
              }
              return query;
            }
        },
        allowClear: true, // 未選択可能
        placeholder: ""   // allowClearによる未選択動作時に必要
    });

    $('#auth-cd').select2({
        ajax: {
            url: '/admin/auths/select2',
            dataType: "json"
        },
        minimumResultsForSearch: 100 // 検索枠を表示させないようにするために設定
    });

});
