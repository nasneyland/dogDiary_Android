package com.najin.dogdiary.model;

import java.util.ArrayList;

public class MemberVO {

    private static final MemberVO memberVO = new MemberVO();

    private String id;
    private String phone;
    private String mail;
    private Integer grade;
    private ArrayList<DogVO> dogList = new ArrayList<DogVO>();

    //싱글톤 객체 읽기
    public static MemberVO getInstance() {
        return memberVO;
    }

    //싱글톤 객체 값 설정하기
    public static void setInstance(MemberVO member) {
        memberVO.setId(member.getId());
        memberVO.setPhone(member.getPhone());
        memberVO.setMail(member.getMail());
        memberVO.setGrade(member.getGrade());
        memberVO.setDogList(member.getDogList());
    }

    //싱글톤 객체 파괴하기
    public static void destroyMemberVO() {
        memberVO.setId(null);
        memberVO.setPhone(null);
        memberVO.setMail(null);
        memberVO.setGrade(null);
        memberVO.setDogList(new ArrayList<DogVO>());
    }

    //get,set 메서드
    public static MemberVO getMemberVO() {
        return memberVO;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public ArrayList<DogVO> getDogList() {
        return dogList;
    }

    public void setDogList(ArrayList<DogVO> dogList) {
        this.dogList = dogList;
    }

    //toString
    @Override
    public String toString() {
        return "MemberVO{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", mail='" + mail + '\'' +
                ", grade=" + grade +
                ", dogList=" + dogList +
                '}';
    }
}
