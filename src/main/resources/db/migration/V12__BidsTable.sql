CREATE TABLE Bids(
    id UUID NOT NULL PRIMARY KEY,
    rowNum BIGSERIAL NOT NULL,
    productId UUID NOT NULL,
    bidderId UUID NOT NULL,
    bidAmount varchar(11) NOT NULL,
    processingFee varchar(11) NOT NULL,
    shippingFee varchar(11) NOT NULL,
    totalPrice varchar(11) NOT NULL,
    message varchar(500) NULL,
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_product FOREIGN KEY (productId) REFERENCES Products(id),
    CONSTRAINT fk_bidder FOREIGN KEY (bidderId) REFERENCES Users(id)
);