SELECT *
  FROM t_attendance
 WHERE attendance_day LIKE ?
ORDER BY user_id, attendance_day