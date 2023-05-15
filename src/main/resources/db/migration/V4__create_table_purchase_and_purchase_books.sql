CREATE TABLE purchase(
    id INT auto_increment PRIMARY KEY,
    customer_id  INT NOT NULL,
    nfe VARCHAR(255),
    price DECIMAL(15,2) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE purchase_book(
    purchase_id int not null,
    book_id int not null,
    FOREIGN KEY (purchase_id) REFERENCES purchase(id),
    FOREIGN KEY (book_id) REFERENCES book(id),
    PRIMARY KEY(purchase_id, book_id)
);
