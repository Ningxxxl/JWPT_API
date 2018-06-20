package cn.ningxy.bean;

/**
 * @Author: ningxy
 * @Description: 问卷实体类
 * @Date: 2018-06-18 15:08
 **/
public class Questionnaire {

    private String name;
    private String character;
    private String content;
    private boolean isEvaluated;
    private String wjbm;
    private String bpr;
    private String pgnr;

    public Questionnaire() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isEvaluated() {
        return isEvaluated;
    }

    public void setEvaluated(boolean evaluated) {
        isEvaluated = evaluated;
    }

    public void setEvaluated(String str) {
        isEvaluated = !"否".equals(str);
    }

    public String getWjbm() {
        return wjbm;
    }

    public void setWjbm(String wjbm) {
        this.wjbm = wjbm;
    }

    public String getBpr() {
        return bpr;
    }

    public void setBpr(String bpr) {
        this.bpr = bpr;
    }

    public String getPgnr() {
        return pgnr;
    }

    public void setPgnr(String pgnr) {
        this.pgnr = pgnr;
    }

    public String getString() {
        return wjbm + "#@" + bpr + "#@" + character + "#@" + name + "#@" + content + "#@" + pgnr;
    }

    @Override
    public String toString() {
        return "Questionnaire{" +
                "name='" + name + '\'' +
                ", character='" + character + '\'' +
                ", content='" + content + '\'' +
                ", isEvaluated=" + isEvaluated +
                ", wjbm='" + wjbm + '\'' +
                ", bpr='" + bpr + '\'' +
                ", pgnr='" + pgnr + '\'' +
                '}';
    }
}
