CREATE TABLE ProductsImages(
    id UUID NOT NULL PRIMARY KEY,
    productId UUID NOT NULL,
    imageData bytea NOT NULL,
    type VARCHAR(10) NOT NULL,
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_product FOREIGN KEY (productId) REFERENCES Products(id)
);