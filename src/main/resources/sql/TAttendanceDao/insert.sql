INSERT INTO t_attendance(
       user_id
      ,attendance_day
      ,attendance_time
      ,attendance_cd
      ,edit_flg
      -- 共通カラム
      ,regist_date
      ,regist_user_id
      ,regist_func_cd
)VALUES(
      :userId
     ,:attendanceDay
     ,:attendanceTime
     ,:attendanceCd
     ,:editFlg
     -- 共通カラム
     ,:registDate
     ,:registUserId
     ,:registFuncCd
)