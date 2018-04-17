UPDATE t_attendance
   SET attendance_time = :attendanceTime
      ,edit_flg = :editFlg
      -- 共通カラム
      ,update_date = SYSDATE
      ,update_user_id = :updateUserId
      ,update_func_cd = :updateFuncCd
 WHERE user_id = :userId
   AND attendance_cd = :attendanceCd
   AND attendance_day = :attendanceDay