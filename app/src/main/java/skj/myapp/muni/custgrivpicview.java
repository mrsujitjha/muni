package skj.myapp.muni;

public class custgrivpicview {
    private int imginfoid,imgreplyid;
    private String imgattachid,imgattachrid;
    private String Headingtext,replyHeadingtext;
    private String Bodytext,replyBodytext;

    public custgrivpicview(int infoid, String gHeadingtext, String gBodytext,int replyid, String replyhead, String replybody,String gimgattachid,String gimgattachrid) {
        imginfoid = infoid;
        Headingtext = gHeadingtext;
        Bodytext = gBodytext;
        imgreplyid = replyid;
        replyHeadingtext = replyhead;
        replyBodytext = replybody;
        imgattachid=gimgattachid;
        imgattachrid=gimgattachrid;
    }
    public int getImginfoid() { return imginfoid;}
    public String getHeadingtext() {
        return Headingtext;
    }
    public String getBodytext() {
        return Bodytext;
    }

    public int getImgreplyid() {
        return imgreplyid;
    }
    public String getReplyHeadingtext() {
        return replyHeadingtext;
    }
    public String getReplyBodytext() {
        return replyBodytext;
    }

    public String getimgattachid() {
        return imgattachid;
    }
    public String getimgattachrid() {
        return imgattachrid;
    }






}
