CREATE TABLE Products(
    id UUID NOT NULL PRIMARY KEY,
    rowNum BIGSERIAL NOT NULL,
    ownerId UUID NOT NULL,
    title varchar(200) NOT NULL,
    price varchar(50) NOT NULL,
    details varchar(500) NOT NULL,
    isSold int DEFAULT(0),
    shippingFee VARCHAR(20) NOT NULL,
    preferredPay VARCHAR(50) NOT NULL DEFAULT('paypal'),
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_owner FOREIGN KEY (ownerId) REFERENCES Users(id)
);