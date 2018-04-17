UPDATE t_line_status
   SET line_id = :lineId
      ,user_id = :userId
      ,menu_cd = :menuCd
      ,action_name = :actionName
      ,contents = :contents
      ,request_time = :requestTime
      -- 共通カラム
      ,update_date = SYSDATE
      ,update_user_id = :updateUserId
      ,update_func_cd = :updateFuncCd
 WHERE
      line_id = :lineId
