UPDATE m_org
   SET
       org_name = :orgName
      ,location = :location
      ,disp_seq = :dispSeq
      ,update_date = :updateDate
      ,update_user_id = :updateUserId
      ,update_func_cd = :updateFuncCd
      ,del_flg = :delFlg
WHERE org_cd = :orgCd
