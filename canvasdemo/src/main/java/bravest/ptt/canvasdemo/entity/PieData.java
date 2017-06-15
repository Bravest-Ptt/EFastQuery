package bravest.ptt.canvasdemo.entity;

/**
 * Created by pengtian on 2017/5/28.
 */

/**
 *  餅圖實例
 */
public class PieData {
    //用戶關心的數據
    //名字
    private String name;
    //數值
    private float value;
    //百分比
    private float percentage;

    //非用戶關心數據
    //顏色
    private int color = 0;
    //角度
    private float angle = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
