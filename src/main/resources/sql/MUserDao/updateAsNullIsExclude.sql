update m_user
   set update_date = :updateDate
    <#if name??>
      ,name = :name
    </#if>
    <#if password??>
      ,password = :password
    </#if>
    <#if mail??>
      ,mail = :mail
    </#if>
    <#if lineId??>
      ,line_id = :lineId
    </#if>
    <#if auth_cd??>
      ,auth_cd = :authCd
    </#if>
    <#if org_cd??>
      ,org_cd = :orgCd
    </#if>
    <#if managerId??>
      ,manager_id = :managerId
    </#if>
    <#if delFlg??>
      ,del_flg = :delFlg
    </#if>
    <#if updateUserId??>
      ,update_user_id = :updateUserId
    </#if>
    <#if updateFuncCd??>
      ,update_func_cd = :updateFuncCd
    </#if>
where user_id = :userId