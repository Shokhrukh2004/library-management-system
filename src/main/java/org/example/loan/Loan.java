package org.example.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.loan.enums.Status;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Loan {
    @Setter
    private int id;
    private int memberId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    @Setter
    private LocalDate returnDate;
    @Setter
    private Status status;
}
