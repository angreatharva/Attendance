package com.example.attendance;

public class Detailsdata {
    public String Faculty_Name1,Subject1,Faculty_Name2,Subject2,Faculty_Name3,Subject3,Faculty_Name4,Subject4,Faculty_Name5,Subject5,Faculty_Name6,Subject6;
    public Long Absent1,Student_ID,Present1,Absent2,Present2,Absent3,Present3,Absent4,Present4,Absent5,Present5,Absent6,Present6;
    public Detailsdata(){};

    public Detailsdata(Long sid,String facname1,Long abs1,Long pr1,String sub1,String facname2,Long abs2,Long pr2,String sub2,String facname3,Long abs3,Long pr3,String sub3,String facname4,Long abs4,Long pr4,String sub4,String facname5,Long abs5,Long pr5,String sub5,String facname6,Long abs6,Long pr6,String sub6){
        this.Student_ID=sid;
        this.Faculty_Name1=facname1;
        this.Absent1=abs1;
        this.Subject1=sub1;
        this.Present1=pr1;
        this.Faculty_Name2=facname2;
        this.Absent2=abs2;
        this.Subject2=sub2;
        this.Present2=pr2;
        this.Faculty_Name3=facname3;
        this.Absent3=abs3;
        this.Subject3=sub3;
        this.Present3=pr3;
        this.Faculty_Name4=facname4;
        this.Absent4=abs4;
        this.Subject4=sub4;
        this.Present4=pr4;
        this.Faculty_Name5=facname5;
        this.Absent5=abs5;
        this.Subject5=sub5;
        this.Present5=pr5;
        this.Faculty_Name6=facname6;
        this.Absent6=abs6;
        this.Subject6=sub6;
        this.Present6=pr6;
    }
}
