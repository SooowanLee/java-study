package com.study.java.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void changeTeam() {
        User user = new User("-1", null);
        user.printUser();

        Team newTeam = new Team();
        User changeTeam = user.changeTeam(newTeam);

        changeTeam.printUser();
    }
}