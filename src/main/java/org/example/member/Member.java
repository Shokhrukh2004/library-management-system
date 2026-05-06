package org.example.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {
    @Setter
    private int id;
    @Setter
    private String name;
    @Setter
    private String email;
    private LocalDate registerDate;
    @Setter
    private boolean isActive;
}
