package app.yungfan.com.autobooks.model;

import com.orm.SugarRecord;

/**
 * Created by yangfan on 2016/4/27.
 */

public class Books extends SugarRecord {


    private String xmmc;//项目名称
    private String fkfs;//付款方式
    private String xfje;//消费金额
    private String gls;//公里数
    private String bz;//备注

    int year;//消费日期 年
    int month;//消费日期 月
    int day;//消费日期 日


    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    /**
     * 保留无参构造函数
     */
    public Books() {
    }

    public String getFkfs() {
        return fkfs;
    }

    public void setFkfs(String fkfs) {
        this.fkfs = fkfs;
    }

    public String getXfje() {
        return xfje;
    }

    public Books(String xmmc, String fkfs, String xfje, String gls, String bz, int year, int month, int day) {
        this.day = day;
        this.xmmc = xmmc;
        this.fkfs = fkfs;
        this.xfje = xfje;
        this.gls = gls;
        this.bz = bz;
        this.year = year;
        this.month = month;
    }

    public void setXfje(String xfje) {
        this.xfje = xfje;
    }

    public String getGls() {
        return gls;
    }

    public void setGls(String gls) {
        this.gls = gls;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getXmmc() {

        return xmmc;
    }

    public void setXmmc(String xmmc) {
        this.xmmc = xmmc;
    }

}
