/**/
  SELECT *
    FROM m_org
   WHERE del_flg = '0'
     AND org_cd = ?
ORDER BY disp_seq