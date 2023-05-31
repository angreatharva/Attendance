package com.example.attendance;

public class ReadwriteUserDetails {
    public String FullName,Email,DOB,Sid,ClassDiv,Gender,Pass;


    public ReadwriteUserDetails(){};


    public ReadwriteUserDetails(String textFullName,String textEmail,String textDOB,String textSid,String textClassDiv,String textGender,String textPass) {
        this.FullName=textFullName;
        this.Email=textEmail;
        this.DOB=textDOB;
        this.Sid=textSid;
        this.ClassDiv=textClassDiv;
        this.Gender=textGender;
        this.Pass=textPass;
    }
}
