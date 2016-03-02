package com.chd.smsbackup.entity;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lxp1 on 2015/11/21.
 */
public class SmsItem /*implements Serializable*/ {
    String address;
    String person;
    String date;
    String protocol;
    String read;
    String status;
    String type;
    String reply_path_present;
    String body;
    String locked;
    String error_code;
    String seen;
    public static  final char  dim=(char)127;


    /*public SmsItem()
    {

    }*/
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReply_path_present() {
        return reply_path_present;
    }

    public void setReply_path_present(String reply_path_present) {
        this.reply_path_present = reply_path_present;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }


    public String toString0()
    {

        //address.toString()
        //String head=dim;
        //String body=String.format("%s%s%s%s%s%s%s%s%s%s%s%s",);
        return String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s",address,person, date,protocol,read,status,type,reply_path_present,body,locked, error_code, seen);
    }



    /*public boolean ParseFromStr(String str)  {
        String[] items=str.split(dim);
        if (items.length!=12)
            return false;
        int idx=0;
        this. address=items[idx++];
        person = items[idx++];
         date=items[idx++];
         protocol=items[idx++];
         read=items[idx++];
         status=items[idx++];
         type=items[idx++];
         reply_path_present = items[idx++] ;
         body = items[idx++];
         locked = items[idx++];
         error_code =items[idx++];
         seen = items[idx++];
        return true;
    }*/


    public boolean ParseFromStr(String str,int end)  {
        String[] items=new String[12];

        int idx=0,len=0,pos=0;
        if (end<12)
            return false;
       // while (idx<end)
        //for (int i=0;i<12 && pos<end;i++)
        while (pos<end && idx<12)
        {
                len=str.charAt(pos);

                if (len>256)
                    Log.d("@@@", "dddddddddd");
                if (len==1)
                    len=2;
                items[idx++] = str.substring(pos+1,pos+1+len);

                pos=pos+1+len;
              /* if ( str.charAt(pos)==0  )
               {
                   pos++;
               }*/

        }

        idx=0;
        if (items.length!=12)
            return false;
        //int idx=0;
        address=items[idx++];
        person = items[idx++];
        date=items[idx++];
        protocol=items[idx++];
        read=items[idx++];
        status=items[idx++];
        type=items[idx++];
        reply_path_present = items[idx++] ;
        body = items[idx++];
        locked = items[idx++];
        error_code =items[idx++];
        seen = items[idx++];
        return true;
    }


    public boolean ParseFromStr(byte[] str,int end)  {
        String[] items=new String[12];

        //ArrayList list= (ArrayList) Arrays.asList(str);
        List list=Arrays.asList(str);
        int idx=0,len=0,pos=0;
        if (end<12)
            return false;
        // while (idx<end)
        //for (int i=0;i<12 && pos<end;i++)
        while (pos<end && idx<12)
        {
            len=str[pos];

            if (len>256)
                Log.d("@@@", "dddddddddd");
            if (len==1)
                len=2;
            List  lst=list.subList(pos+1,pos+1+len);


           // items[idx++] =new String( (byte[])());//                 str.substring(pos+1,pos+1+len);

            String[] words = {"ace", "boom", "crew", "dog", "eon"};

            List<String> wordList = Arrays.asList(words);


            pos=pos+1+len;
              /* if ( str.charAt(pos)==0  )
               {
                   pos++;
               }*/

        }

        idx=0;
        if (items.length!=12)
            return false;
        //int idx=0;
        address=items[idx++];
        person = items[idx++];
        date=items[idx++];
        protocol=items[idx++];
        read=items[idx++];
        status=items[idx++];
        type=items[idx++];
        reply_path_present = items[idx++] ;
        body = items[idx++];
        locked = items[idx++];
        error_code =items[idx++];
        seen = items[idx++];
        return true;
    }
}

