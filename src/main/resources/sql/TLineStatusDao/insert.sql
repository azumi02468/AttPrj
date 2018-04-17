INSERT INTO t_line_status(
       line_id
      ,user_id
      ,menu_cd
      ,action_name
      ,contents
      ,request_time
      -- 共通カラム
      ,regist_date
      ,regist_user_id
      ,regist_func_cd
)VALUES(
      :lineId
     ,:userId
     ,:menuCd
     ,:actionName
     ,:contents
     ,:requestTime
     -- 共通カラム
     ,:registDate
     ,:registUserId
     ,:registFuncCd
)