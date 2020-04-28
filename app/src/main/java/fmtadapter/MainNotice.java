package fmtadapter;

public class MainNotice {
    private String title;     //标题
    private String startline; //开头
    private String content;   //内容
    private String signature; //签名
    private String date;      //日期

    /**
     * 带参数的构造方法
     * @param title 标题
     * @param startline 开头
     * @param signature 签名
     * @param date 日期
     */
    public MainNotice(String title,String startline,String signature,String date){
        this.title = title;
        this.startline = startline;
        this.signature = signature;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartline() {
        return startline;
    }

    public void setStartline(String startline) {
        this.startline = startline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
