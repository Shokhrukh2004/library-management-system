INSERT INTO books (title, author, isbn, total_copies, available_copies, is_active)
VALUES
    ('Clean Code', 'Robert Martin', '9780132350884', 5, 5, true),
    ('Effective Java', 'Joshua Bloch', '9780134685991', 3, 3, true);

INSERT INTO members (name, email, register_date, is_active)
VALUES
    ('John Doe', 'john.doe@mail.com', CURRENT_DATE, true),
    ('Jane Smith', 'jane.smith@mail.com', CURRENT_DATE, true);

INSERT INTO loans (member_id, book_id, borrow_date, due_date, return_date, status)
VALUES
    (1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '10 days', NULL, 'ACTIVE'),
    (2, 2, CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '3 days', 'RETURNED');

UPDATE books SET available_copies = 4 WHERE isbn = '9780132350884';