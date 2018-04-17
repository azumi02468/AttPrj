SELECT *
  FROM t_attendance
 WHERE user_id = ?
   AND attendance_day LIKE ? || '%'
   AND del_flg = '0'