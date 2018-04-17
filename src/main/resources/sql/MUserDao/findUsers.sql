   SELECT u.*
         ,o.org_name
         ,dd.div_cd_content as authName
     FROM m_user u
LEFT JOIN m_org o
       ON u.org_cd = o.org_cd
LEFT JOIN m_div_details dd
       ON u.auth_cd = dd.div_cd
      AND dd.div_id = 1
    WHERE u.del_flg = '0'
    <#if orgCd??>
      AND u.org_cd = :orgCd
    </#if>
    <#if likeName??>
      AND u.name like :likeName
    </#if>
 ORDER BY u.user_id