package skj.myapp.muni;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class dboperation {
  dbmanager dbHelper = null;
  String dten,tmen,repen;

    public dboperation() {dbHelper = MainActivity.dbmanage;}

    public void getprofile(String usermail){

        String selectQuery = "SELECT * FROM tab_user Where(tab_user.mmail='"+usermail+"')";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
        public_ver.musername =cursor.getString(1);
        public_ver.muserphone =cursor.getString(2);
        public_ver.muserctz =cursor.getString(3);
        public_ver.muserfather =cursor.getString(4);
        public_ver.muserfctz =cursor.getString(5);
        public_ver.musermother =cursor.getString(6);
        public_ver.musermctz =cursor.getString(7);
        public_ver.muserhno =cursor.getString(8);
        public_ver.muid =cursor.getInt(9);
        }
        cursor.close();
        db.close();
    }
    public String changenumbertonepali(String attachmentname){
        String nepalitext="";
       int j=attachmentname.length();
        for (int i = 0; i < j; i++) {
            int en=Integer.parseInt(attachmentname.substring(i,i+1));
            nepalitext=nepalitext+public_ver.wnonep[en];
        }
        return nepalitext;
    }
    public int getattacmentcount(String attachmentname){
    int totali;
        totali=0;
        String selectQuery = "SELECT * FROM tab_pic Where(tab_pic.id LIKE '"+attachmentname+"%')";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                totali=totali+1;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return totali;
    }
    public int getusercount(){
        int totali;
        totali=0;
        String selectQuery = "SELECT * FROM tab_user";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                totali=totali+1;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return totali;
    }
    public String grivid(int grivrow){
        String grividno="";
        int i=0;
        String selectQuery = "SELECT * FROM tab_griv";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
               if(grivrow==i){
                grividno=cursor.getString(0);
                   break;
               }
                i=i+1;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return grividno;
    }


    public void login(){
        String selectQuery = "SELECT * FROM tab_user";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst () ) {
            do {
              if(cursor.getInt(10)==1) {
                  public_ver.loginmail=cursor.getString(0);
                  public_ver.lastsync=cursor.getString(11);
                  public_ver.FEN=cursor.getInt(12);
              }else{ public_ver.lastsync=cursor.getString(11);}
            } while (cursor.moveToNext());
        }else{
            public_ver.loginmail="register";
            public_ver.lastsync="0";
            public_ver.FEN=0;
        }
         cursor.close();
        db.close();
    }
   public void logout(int mvalue,String syncval){
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("login", mvalue);
            if(syncval.length() >0){ cv.put("lastsync", syncval);}
            db.update("tab_user",cv,"mmail= ?", new String [] {String.valueOf(public_ver.loginmail)});
        db.close();
    }
    public void engnep(int mvalue){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("engnep", mvalue);
        db.update("tab_user",cv,"mmail= ?", new String [] {String.valueOf(public_ver.loginmail)});
        db.close();
    }

    public void Delete_info(int rowno){
       String msgid="";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM tab_msg_send";
        Cursor cursor = db.rawQuery(selectQuery, null);
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                if(rowno==i){
                    msgid=cursor.getString(0);
                    break;
                }
                i=i+1;
            } while (cursor.moveToNext());
        }

        db.delete("tab_msg_send","infoid= ?", new String [] {String.valueOf(msgid)});
        db.close();
    }
    public void Delete_griv(int rowno){
        String msgid="";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM tab_griv";
        Cursor cursor = db.rawQuery(selectQuery, null);
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                if(rowno==i){
                    msgid=cursor.getString(0);
                    break;
                }
                i=i+1;
            } while (cursor.moveToNext());
        }

        db.delete("tab_griv","infoid= ?", new String [] {String.valueOf(msgid)});
        db.close();
    }
    public void message_displayed(String tabname){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pview", "2");
       if(tabname.equals("info")){
        db.update("tab_msg_send",cv,"pview= ?", new String [] {"0"});}else{
        db.update("tab_griv",cv,"pview= ?", new String [] {"0"});
       }
        db.close();
    }
    public void save_user(String ummail){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM tab_user WHERE(tab_user.mmail=='"+ummail+"')";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            ContentValues cv = new ContentValues();
            cv.put("name", public_ver.musername);
            cv.put("phone", public_ver.muserphone);
            cv.put("ctz", public_ver.muserctz);
            cv.put("fname", public_ver.muserfather);
            cv.put("fctz", public_ver.muserfctz);
            cv.put("mname", public_ver.musermother);
            cv.put("mctz", public_ver.musermctz);
            cv.put("huno", public_ver.muserhno);
            cv.put("uid", public_ver.muid);
            db.update("tab_user",cv,"mmail= ?", new String [] {String.valueOf(ummail)});
        }else{
            ContentValues cv = new ContentValues();
            cv.put("mmail", ummail);
            cv.put("name", public_ver.musername);
            cv.put("phone", public_ver.muserphone);
            cv.put("ctz", public_ver.muserctz);
            cv.put("fname", public_ver.muserfather);
            cv.put("fctz", public_ver.muserfctz);
            cv.put("mname", public_ver.musermother);
            cv.put("mctz", public_ver.musermctz);
            cv.put("huno", public_ver.muserhno);
            cv.put("uid", public_ver.muid);
            cv.put("login", 1);
            cv.put("lastsync", "0");
            cv.put("engnep", public_ver.FEN);
            db.insert("tab_user", null,cv );
        }

        db.close();
    }
    public void SAVE_INFO(String infoid,String infotext,String isender,String ireceiver){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM tab_msg_send WHERE(tab_msg_send.infoid=='"+infoid+"')";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            ContentValues cv = new ContentValues();
            cv.put("infotext", infotext);
            cv.put("infosender", isender);
            cv.put("inforeceiver",ireceiver);
            db.update("tab_msg_send",cv,"infoid= ?", new String [] {String.valueOf(infoid)});
        }else{
            ContentValues cv = new ContentValues();
            cv.put("infoid", infoid);
            cv.put("infotext", infotext);
            cv.put("infosender", isender);
            cv.put("inforeceiver",ireceiver);
            cv.put("pview", "1");
            db.insert("tab_msg_send", null,cv );
        }

        db.close();
    }
    public void Welcome_message(String infotext){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String infoid=public_ver.muid+"-"+getdatetime()+"-"+public_ver.loginmail;
            ContentValues cv = new ContentValues();
            cv.put("infoid", infoid);
            cv.put("infotext", infotext);
            cv.put("infosender", "Admin");
            cv.put("inforeceiver","User");
            cv.put("pview", "0");
            db.insert("tab_msg_send", null,cv );
        db.close();
    }
    public void SAVE_GRIV(String infoid,String infotext,String isender,String ireceiver){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM tab_griv WHERE(tab_griv.infoid=='"+infoid+"')";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            ContentValues cv = new ContentValues();
            cv.put("infotext", infotext);
            cv.put("infosender", isender);
            cv.put("inforeceiver",ireceiver);
            cv.put("gpic",public_ver.gpic);
            db.update("tab_griv",cv,"infoid= ?", new String [] {String.valueOf(infoid)});
        }else{
            ContentValues cv = new ContentValues();
            cv.put("infoid", infoid);
            cv.put("infotext", infotext);
            cv.put("infosender", isender);
            cv.put("inforeceiver",ireceiver);
            cv.put("pview", "1");//griv posted by public
            cv.put("gpic",public_ver.gpic);
            cv.put("rpic","-");
            db.insert("tab_griv", null,cv );
        }
        db.close();
    }
    public void reply_griv(String infoid,String infotext,String replyid){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("replyid", replyid);
            cv.put("replytext", infotext);
            cv.put("pview", "2");//griv reply posted by ward/mun/admin
            cv.put("rpic",public_ver.gpic);
            db.update("tab_griv",cv,"infoid= ?", new String [] {String.valueOf(infoid)});
           db.close();
    }
    public void sync_uploaddoc(String allinfo){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String infoval[]=allinfo.split("/::/");
        for (int i = 0; i < infoval.length; i++) {
            String colval[] = infoval[i].split("/ww/");
            ContentValues cv = new ContentValues();
            String selectQuery = "SELECT * FROM tab_pic WHERE(tab_pic.id=='" + colval[0] + "')";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                //message exist
            } else{
                cv.put("id", colval[0]);
                if(colval.length>1){ cv.put("description", colval[1]);}
                db.insert("tab_pic", null, cv);}
        }
        db.close();
    }
    public void delete_uploaddoc_info(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
     //   db.delete("tab_pic","tab_pic.id<>?", new String [] {"-"});
        db.delete("tab_pic",null, null);
        db.close();
    }

    public void deletedoc(String docname){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("tab_pic","id=?", new String [] {String.valueOf(docname)});
        db.close();
    }
    public void delete_munlink(String linkid){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("tab_link","linkid=?", new String [] {String.valueOf(linkid)});
        db.close();
    }
    public void sync_munlink(String allinfo){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String infoval[]=allinfo.split("/::/");
        for (int i = 0; i < infoval.length; i++) {
            String colval[] = infoval[i].split("/ww/");
            ContentValues cv = new ContentValues();
            String selectQuery = "SELECT * FROM tab_link WHERE(tab_link.linkid=='" + colval[0] + "')";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                cv.put("lneng", colval[1]);
                cv.put("lnnep", colval[2]);
                cv.put("ldetail", colval[3]);
                db.update("tab_link",cv,"linkid= ?", new String [] {String.valueOf( colval[0])});
            } else{
                cv.put("linkid", colval[0]);
                cv.put("lneng", colval[1]);
                cv.put("lnnep", colval[2]);
                cv.put("ldetail", colval[3]);
                db.insert("tab_link", null, cv);}
        }
        db.close();
    }
    public void delete_mun_link(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("tab_link","linkid<> ?", new String [] {"-"});
        db.close();
    }
    public void sync_info(String allinfo){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String infoval[]=allinfo.split("/::/");
        for (int i = 0; i < infoval.length; i++) {
            String colval[] = infoval[i].split("/ww/");
            ContentValues cv = new ContentValues();
            String selectQuery = "SELECT * FROM tab_msg_send WHERE(tab_msg_send.infoid=='" + colval[0] + "')";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                //message exist
            } else{
            cv.put("infoid", colval[0]);
            cv.put("infotext", colval[1]);
            cv.put("infosender", colval[2]);
            cv.put("inforeceiver", colval[3]);
            cv.put("pview", public_ver.pview);
            db.insert("tab_msg_send", null, cv);}
        }
        db.close();
    }
    public void sync_griv(String allinfo){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String infoval[]=allinfo.split("/::/");
        boolean replydone;
        for (int i = 0; i < infoval.length; i++) {
            String colval[] = infoval[i].split("/ww/");
            ContentValues cv = new ContentValues();
            String selectQuery = "SELECT * FROM tab_griv WHERE(tab_griv.infoid=='" + colval[0] + "')";
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                if (cursor.getString(4)==null){replydone=false;}else{
                    if(cursor.getString(4).equals("")){replydone=false;}else{replydone=true;}
                }
              if(colval.length>4 && replydone==false) {
                cv.put("replyid", colval[4]);
                cv.put("replytext", colval[5]);
                cv.put("pview", 0);
                cv.put("rpic", colval[7]);
                db.update("tab_griv",cv,"infoid= ?", new String [] {String.valueOf( colval[0])});
                }
            } else{
                cv.put("infoid", colval[0]);
                cv.put("infotext", colval[1]);
                cv.put("infosender", colval[2]);
                cv.put("inforeceiver", colval[3]);
                cv.put("gpic", colval[6]);
               if(colval.length>4) {
                cv.put("replyid", colval[4]);
                cv.put("replytext", colval[5]);
                cv.put("rpic", colval[7]);
              }
                cv.put("pview", public_ver.pview);//new entry 0 old entry 1
                db.insert("tab_griv", null, cv);}
        }
        db.close();
    }
    public String get_newmessage(){
     String Messagetext="";
        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_msg_send WHERE(tab_msg_send.pview=0)";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst () ) {
            do {
                if(Messagetext.length()>0){ Messagetext=Messagetext+ "/::/" +cursor.getString(1) + "/ww/" + cursor.getString(2);}else{
                    Messagetext=cursor.getString(1) + "/ww/" + cursor.getString(2);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return Messagetext;
    }
    public String get_grivnewmessage(){
        String Messagetext="";
        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_griv WHERE(tab_griv.pview=0)";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst () ) {
            do {
                if(Messagetext.length()>0){ Messagetext=Messagetext+ "/::/" +cursor.getString(1) + "/ww/" + cursor.getString(5);}else{
                    Messagetext=cursor.getString(1) + "/ww/" + cursor.getString(5);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return Messagetext;
    }
public String getdatetime(){
    String curdatetime="";
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month= calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int hr = calendar.get(Calendar.HOUR_OF_DAY);
    int min = calendar.get(Calendar.MINUTE);
    long datetime=(((year*100+month+1)*100+day)*100+hr);
    curdatetime=datetime*100+min+"";
    return curdatetime;
}
    public String getdate_timeoooo(){
        String curdatetime="";
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month= calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        long datetime=((year*100+month+1)*100+day);
        curdatetime=datetime+"";
        return curdatetime;
    }
    public void get_mun_linklist_withoutimage(ArrayAdapter <String> adapter){
        adapter.clear();
        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_link";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if ( cursor.moveToFirst () ) {
            do {
                if(public_ver.FEN==0) {
                adapter.add(cursor.getString(1));}else{
                adapter.add(cursor.getString(2));
                }
            } while (cursor.moveToNext());
        }    cursor.close();
        db.close();

    }
    public void get_mun_linklist(){

        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_link";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int i=0;
        if ( cursor.moveToFirst () ) {
            do {
                    if (public_ver.weblink.length()==0) {
                        if (public_ver.FEN == 0) {
                            public_ver.weblink = cursor.getString(1);
                           public_ver.wimageid = R.drawable.info + "";
                        } else {
                            public_ver.weblink = cursor.getString(2);
                            public_ver.wimageid = R.drawable.replygriv + "";
                        }
                    }else{
                        if (public_ver.FEN == 0) {
                            public_ver.weblink =public_ver.weblink+ ","+ cursor.getString(1);
                            public_ver.wimageid =public_ver.wimageid+ ","+ R.drawable.info + "";
                        } else {
                            public_ver.weblink = public_ver.weblink+ ","+cursor.getString(2);
                            public_ver.wimageid =public_ver.wimageid + ","+R.drawable.replygriv + "";
                        }
                    }
            } while (cursor.moveToNext());
        }    cursor.close();
        db.close();

    }
    public void get_mun_selectedlink(String linkid){

        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_link WHERE tab_link.linkid LIKE'"+linkid+"%'";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst () ) {
            do {
                public_ver. mlinkid=cursor.getInt(0);
                public_ver. mlneng=cursor.getString(1);
                public_ver. mlnnep=cursor.getString(2);
                public_ver. mlindes=cursor.getString(3);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

    }
    public void get_selected_doc(int rowno){
int i=0;
        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_pic WHERE tab_pic.id LIKE '"+public_ver.loginmail.replace("@gmail.com","_gm")+"%'";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst () ) {
            do {
                if (i==rowno) {
                    public_ver.gpic = cursor.getString(0);
                    break;
                }
                i=i+1;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

    }
    public  String get_mun_name(String munid){
        String munname ="";
        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_mun WHERE(tab_mun.id LIKE '"+ munid +"')";
        SQLiteDatabase db2 = dbHelper.getReadableDatabase();
        Cursor cursor = db2.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst () ) {
            if (public_ver.FEN == 1) {
                munname = cursor.getString(2);
            } else {
                munname = cursor.getString(1);
            }
        }
        cursor.close();
        db2.close();
    return munname;
    }

    public void get_msg_send_arraylist(String msgid, ArrayList<infocustomview> arrayList){
        String sendertype="";
        arrayList.clear();
        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_msg_send WHERE(tab_msg_send.infoid LIKE '%"+msgid +"%')";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if ( cursor.moveToFirst () ) {
            do {

                String dt =cursor.getString(0).substring(6,18);
                if(public_ver.FEN==0){dten="Date:";tmen=" Time:";}else{dten="मिति:";tmen=" समय:";dt=changenumbertonepali(dt);}
                String finaldt=dten + dt.substring(0,4)+"-"+dt.substring(4,6)+"-"+dt.substring(6,8)+tmen+dt.substring(8,10)+":"+dt.substring(10,12);
                int i=R.drawable.info;
                String wn= cursor.getString(0).substring(3,5);
                if(public_ver.FEN==0){ sendertype=cursor.getString(2);}else{
                    if(cursor.getString(2).equals("Admin")){sendertype="व्यवस्थापक";}
                    if(cursor.getString(2).equals("Municipality")){sendertype="नगरपालिका";}
                    if(cursor.getString(2).equals("Ward")){sendertype="वा्ड";  wn =public_ver.wnonep[Integer.parseInt(wn)];}
                }
                if(cursor.getString(2).equals("Ward")) {sendertype=sendertype+"-"+wn;}
                arrayList.add(new infocustomview(i,finaldt +"(" +sendertype+")",cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
    }
    public void get_grv_send_arraylist(String msgid, ArrayList<custgrivpicview> garrayList){

        String sendertype="";
        String postedtype="";
        String replygrivmsg;
        garrayList.clear();
        String selectQuery = "";
        String k="";
        String l="";
        Boolean replydone;
        selectQuery = "SELECT * FROM tab_griv WHERE(tab_griv.infoid LIKE '%"+msgid +"%')";
        SQLiteDatabase db2 = dbHelper.getReadableDatabase();
        Cursor gcursor = db2.rawQuery(selectQuery, null);

        if ( gcursor.moveToFirst () ) {
            do {
                int i=R.drawable.complain;
                int j=R.drawable.replygriv;
                k=gcursor.getString(7);
                l=gcursor.getString(8);

                String dt =gcursor.getString(0).substring(6,18);
                if(public_ver.FEN==0){dten="Date:";tmen=" Time:";}else{dten="मिति:";tmen=" समय:";dt=changenumbertonepali(dt);}
                String finaldt=dten + dt.substring(0,4)+"-"+dt.substring(4,6)+"-"+dt.substring(6,8)+tmen+dt.substring(8,10)+":"+dt.substring(10,12);
                String wn= gcursor.getString(0).substring(3,5);
                if(public_ver.FEN==0){ postedtype=gcursor.getString(2);}else{
                    if(gcursor.getString(2).equals("Admin")){postedtype="व्यवस्थापक";}
                    if(gcursor.getString(2).equals("Municipality")){postedtype="नगरपालिका";}
                    if(gcursor.getString(2).equals("Ward")){postedtype="वा्ड";  wn =public_ver.wnonep[Integer.parseInt(wn)];}
                    if(gcursor.getString(2).equals("Public")){postedtype="नागरिक";  wn =public_ver.wnonep[Integer.parseInt(wn)];}
                }
                if(gcursor.getString(2).equals("Ward")||gcursor.getString(2).equals("Public")) {postedtype=postedtype+"-"+wn;}
                if (gcursor.getString(4)==null){replydone=false;}else{
                    if(gcursor.getString(4).equals("")){replydone=false;}else{replydone=true;}
                }

                if (replydone==false){
                    garrayList.add(new custgrivpicview(i,finaldt +"(" +postedtype+")",gcursor.getString(1),0,"","",k,l));
                }else{
                    if(public_ver.FEN==0){ sendertype=gcursor.getString(3);}else{
                        if(gcursor.getString(3).equals("Admin")){sendertype="व्यवस्थापक";}
                        if(gcursor.getString(3).equals("Municipality")){sendertype="नगरपालिका";}
                        if(gcursor.getString(3).equals("Ward")){sendertype="वाड";}
                    }
                    String dt1 =gcursor.getString(4).substring(6,18);
                    if(public_ver.FEN==0){dten="Date:";tmen=" Time:";repen="Reply on:";}else{dten="मिति:";tmen=" समय:";repen=" जवाफ:";dt1=changenumbertonepali(dt1);}
                    String finaldt1=dten + dt1.substring(0,4)+"-"+dt1.substring(4,6)+"-"+dt1.substring(6,8)+tmen+dt1.substring(8,10)+":"+dt1.substring(10,12);
                    replygrivmsg=repen+finaldt1+"("+ sendertype +")";
                  garrayList.add(new custgrivpicview(i,finaldt +"(" +postedtype+")",gcursor.getString(1),j,replygrivmsg,gcursor.getString(5),k,l));

                }

            } while (gcursor.moveToNext());
        }

        gcursor.close();
        db2.close();
    }

    public void Get_mun_list(int province,ArrayAdapter <CharSequence> adapter){

        adapter.clear();
        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_mun WHERE(tab_mun.id LIKE '"+ province +"%')";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int x=0;
        // looping through all rows and adding to list
        if ( cursor.moveToFirst () ) {
            do {
                x=x+1;
              if(public_ver.FEN==1){
                  adapter.add(cursor.getString(2));
              }else{
                  adapter.add(cursor.getString(1));
              }

            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();

    }

    public void ward_list(int PlID,ArrayAdapter <CharSequence> adapter){
     int wno=10;

        String selectQuery = "SELECT * FROM tab_mun WHERE(tab_mun.id="+ PlID +")";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst () ) {
            do {
                wno = cursor.getInt(3);
            } while (cursor.moveToNext());
        }

        adapter.clear();
        for(int i=1;i<=wno;i++){
            adapter.add(i+"");
        }
        cursor.close();
        db.close();
    }
    public void ward_list_all(int PlID,ArrayAdapter <CharSequence> adapter){
        int wno=10;

        String selectQuery = "SELECT * FROM tab_mun WHERE(tab_mun.id="+ PlID +")";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst () ) {
            do {
                wno = cursor.getInt(3);
            } while (cursor.moveToNext());
        }

        adapter.clear();
        if(public_ver.FEN==0){
        adapter.add("All");}else{adapter.add("सबै");}
        for(int i=1;i<=wno;i++){
            if(public_ver.FEN==0){adapter.add(i+"");}else{ adapter.add(public_ver.wnonep[i]);}
        }
        cursor.close();
        db.close();
    }
    public void Savepicinforecord(String id,String des){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery = "SELECT * FROM tab_pic WHERE(tab_pic.id='"+ id +"')";
        Cursor cursor = db.rawQuery(selectQuery, null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {
            cv.put("description", des);
            db.update("tab_pic",cv,"id= ?", new String [] {String.valueOf(id)});
        }else {

            cv.put("id", id);
            cv.put("description", des);
            cv.put("lat", public_ver.lat);
            cv.put("lng", public_ver.lng);
            db.insert("tab_pic", null, cv);
        }
        db.close();
    }
    public void get_all_attachments(ArrayAdapter <CharSequence> adapter){

        adapter.clear();
        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_pic";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if ( cursor.moveToFirst () ) {
            do {
                adapter.add(cursor.getString(0) + ":" + cursor.getString(1));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();

    }
    public void get_all_attachments_list(){
        String selectQuery = "";
        selectQuery = "SELECT * FROM tab_pic WHERE tab_pic.id LIKE '"+public_ver.loginmail.replace("@gmail.com","_gm")+"%'";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst () ) {
            do {
                if (public_ver.weblink.length()==0) {
                    public_ver.weblink = cursor.getString(0) + ":" +cursor.getString(1);
                    public_ver.wimageid = R.drawable.replygriv + "";

                }else{
                    public_ver.weblink =public_ver.weblink+ ","+cursor.getString(0) + ":" + cursor.getString(1);
                    public_ver.wimageid =public_ver.wimageid+ ","+ R.drawable.replygriv + "";
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

    }
}