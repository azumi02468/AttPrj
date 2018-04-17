/* MUserDao selectAdminMembers.sql */
SELECT mu.*
  FROM m_user mu
 WHERE mu.del_flg = '0'
   AND NVL(mu.line_id, ' ') <> NVL(:lineId, 'x')
   AND EXISTS(SELECT '1'
                  FROM t_attendance ta
                 WHERE ta.user_id = mu.user_id
                   AND ta.attendance_day LIKE :yyyymm || '%'
                   AND ta.del_flg = '0')
 ORDER BY
        mu.user_id