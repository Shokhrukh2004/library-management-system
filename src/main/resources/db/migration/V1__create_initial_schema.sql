CREATE TABLE books (
    id               SERIAL PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    author           VARCHAR(255) NOT NULL,
    isbn             VARCHAR(50)  NOT NULL UNIQUE,
    total_copies     INT          NOT NULL DEFAULT 0,
    available_copies INT          NOT NULL DEFAULT 0,
    is_active        BOOLEAN      NOT NULL DEFAULT true
);

CREATE TABLE members (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    register_date DATE         NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT true
);

CREATE TABLE loans (
    id          SERIAL PRIMARY KEY,
    member_id   INT  NOT NULL REFERENCES members(id),
    book_id     INT  NOT NULL REFERENCES books(id),
    borrow_date DATE NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE,
    status      VARCHAR(50) NOT NULL
);