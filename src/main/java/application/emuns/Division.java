package application.emuns;

import lombok.Getter;

/**
 * 汎用区分
 *
 * @author 作成者氏名
 *
 */
public enum Division {
    AUTH(1),
    ATTENDANCE(2),
    MENU(3);

    @Getter
    private int id;

    Division(int id) {
        this.id = id;
    }
}
