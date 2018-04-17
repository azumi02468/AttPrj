UPDATE m_setting
   SET open_time = :openTime
      ,open_minutes = :openMinutes
      ,close_time = :closeTime
      ,close_minutes = :closeMinutes
      ,alert_close_time = :alertCloseTime
      ,alert_close_minutes = :alertCloseMinutes
      ,business_flag_mon = :businessFlagMon
      ,business_flag_tue = :businessFlagTue
      ,business_flag_wed = :businessFlagWed
      ,business_flag_thu = :businessFlagThu
      ,business_flag_fri = :businessFlagFri
      ,business_flag_sat = :businessFlagSat
      ,business_flag_sun = :businessFlagSun
      ,alert_flag = :alertFlag
      ,update_date = SYSDATE
      ,update_user_id = :updateUserId
      ,update_func_cd = :updateFuncCd