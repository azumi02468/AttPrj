/* MUserDao findNoneAttendance.sql */
SELECT mu.*
  FROM m_user mu
 WHERE mu.line_id IS NOT NULL
   AND mu.del_flg = '0'
   AND NOT EXISTS(SELECT '1'
                      FROM t_attendance ta
                     WHERE ta.user_id = mu.user_id
                       AND ta.attendance_cd = :mu.attendanceCd
                       AND ta.attendance_day = :attendanceDay
                       AND ta.del_flg = '0')
 ORDER BY
        mu.user_id