package skj.myapp.muni;

public class infocustomview {
    private int imginfoid;
    private String Headingtext;
    private String Bodytext;
    public infocustomview(int infoid, String gHeadingtext, String gBodytext) {
        imginfoid = infoid;
        Headingtext = gHeadingtext;
        Bodytext = gBodytext;
    }
    public int getImginfoid() {
        return imginfoid;
    }
    public String getHeadingtext() {
        return Headingtext;
    }
    public String getBodytext() {
        return Bodytext;
    }

}
