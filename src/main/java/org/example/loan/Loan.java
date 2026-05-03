package org.example.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.loan.enums.Status;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Loan {

    private int id;
    private final int memberId;
    private final int bookId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private Status status;
}
