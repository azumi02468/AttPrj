  SELECT *
    FROM m_div_details
   WHERE div_id = :divId
    <#if divCd??>
     AND div_cd = :divCd
    </#if>
ORDER BY div_cd