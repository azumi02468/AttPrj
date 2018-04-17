INSERT INTO m_user (
   user_id
  ,password
  ,name
  ,mail
  ,auth_cd
  ,org_cd
  ,manager_id
  ,regist_date
  ,regist_user_id
  ,regist_func_cd
) VALUES (
  :userId
 ,:password
 ,:name
 ,:mail
 ,:authCd
 ,:orgCd
 ,:managerId
 ,:registDate
 ,:registUserId
 ,:registFuncCd
)