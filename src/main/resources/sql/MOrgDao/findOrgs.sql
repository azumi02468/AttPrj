/**/
  SELECT *
    FROM m_org
   WHERE del_flg = '0'
    <#if likeName??>
     AND org_name like :likeName
    </#if>
ORDER BY disp_seq