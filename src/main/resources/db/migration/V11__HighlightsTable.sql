CREATE TABLE Highlights(
    id UUID NOT NULL PRIMARY KEY,
    productId UUID NOT NULL,
    message varchar(500) NOT NULL,
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_product FOREIGN KEY (productId) REFERENCES Products(id)
);