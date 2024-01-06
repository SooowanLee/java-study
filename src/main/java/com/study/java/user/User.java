package com.study.java.user;

import java.util.Objects;
import java.util.regex.Pattern;

public class User {

    private  String name;
    private final Team team;

    public User(String name, Team team) {
        validate(name, team);
        this.name = name;
        this.team = team;
    }

    private void validate(String name, Team team) {
        if (isNumeric(name)) {
            throw new IllegalArgumentException("이름은 숫자일 수 없습니다.");
        }

        if (Objects.isNull(team)) {
            throw new NullPointerException("팀을 지정해주세요.");
        }
    }

    private boolean isNumeric(String name) {
        return Pattern.matches("^(0|[-]?[1-9]\\d*)$", name);
    }

    public User changeTeam(final Team newTeam) {
        validate(this.name, newTeam);
        return new User(this.name, newTeam);
    }

//    private void changeName(final String name) {
//        name = "test1"; //컴파일 오류
//        //...
//        name = "test2"; //컴파일 오류
//    }

    public void printUser() {
        System.out.println(this.name + " " + this.team);
    }
}

