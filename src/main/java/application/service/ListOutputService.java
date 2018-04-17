package application.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.TAttendanceDao;
import application.dto.DayAttendanceDto;
import application.emuns.AttenanceCd;
import application.entity.TAttendance;

/**
 * リスト出力サービス
 *
 * @author 作成者名
 *
 */
@Service
@Transactional
public class ListOutputService {

    /** 勤怠情報DAO。 */
    @Autowired
    private TAttendanceDao tAttendancedDao;

    /**
     * 1日ごとの勤怠情報リストを返します。
     * @param yyyymm 出勤日(年月)
     */
    public List<DayAttendanceDto> getDayAttendanceList(String yyyymm) {
        // TAttendanceエンティティの情報を、同じ日の出勤、退勤をセットにした形に変換する
        List<TAttendance> attendanceList = tAttendancedDao.selectByMonth(yyyymm);
        Map<String, DayAttendanceDto> daysAttendanceMap = new LinkedHashMap<String, DayAttendanceDto>();
        attendanceList.stream().forEach(tAttendance -> this.setDaysAttendanceMap(daysAttendanceMap, tAttendance));

        // Mapの値部分のみをList化してリターンする
        return new ArrayList<DayAttendanceDto>(daysAttendanceMap.values());
    }

    /**
     * 勤怠情報エンティティを日付ごとの勤怠情報マップにセットします。
     * @param daysAttendanceMap 日付ごとの勤怠情報マップ
     * @param tAttendance 勤怠情報エンティティ
     */
    private void setDaysAttendanceMap(Map<String, DayAttendanceDto> daysAttendanceMap, TAttendance tAttendance) {
        DayAttendanceDto dayAttendance;
        String daysAttendanceKey = getDaysAttendanceMapKey(tAttendance);
        if (daysAttendanceMap.containsKey(daysAttendanceKey)) {
            // すでに同じユーザと日付の組み合わせのdayAttendanceが存在する場合はMapから取得
            dayAttendance = daysAttendanceMap.get(daysAttendanceKey);
        } else {
            // 同じユーザと日付の組み合わせのdayAttendanceが存在しない場合は生成して情報をセット
            dayAttendance = new DayAttendanceDto();
            daysAttendanceMap.put(daysAttendanceKey, dayAttendance);
            dayAttendance.setUserId(tAttendance.getUserId());
            dayAttendance.setAttendanceDay(tAttendance.getAttendanceDay());
        }
        ZoneId zone = ZoneId.systemDefault();
        // 出勤か退勤で時刻データをセットするフィールドを変える
        if (AttenanceCd.ARRIVAL == AttenanceCd.getByCode(tAttendance.getAttendanceCd())) {
            dayAttendance.setArrivalTime(
                    LocalDateTime.ofInstant(tAttendance.getAttendanceTime().toInstant(), zone));
        } else {
            dayAttendance.setClockOutTime(
                    LocalDateTime.ofInstant(tAttendance.getAttendanceTime().toInstant(), zone));
        }
    }

    /**
     * 日付ごとの勤怠情報マップ用キーを返します。
     * @param tAttendance 勤怠情報エンティティ
     * @return 日付ごとの勤怠情報マップ用キー
     */
    private String getDaysAttendanceMapKey(TAttendance tAttendance) {
        return tAttendance.getUserId() + "_" + tAttendance.getAttendanceDay();
    }
}
