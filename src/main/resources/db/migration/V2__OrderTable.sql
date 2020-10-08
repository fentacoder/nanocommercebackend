CREATE TABLE Orders(
    id UUID NOT NULL PRIMARY KEY,
    rowNum BIGSERIAL NOT NULL,
    userId UUID NOT NULL,
    stripeOrderId VARCHAR(500) NULL,
    paypalOrderId VARCHAR(500) NULL,
    productId UUID NULL,
    activityId UUID NULL,
    bidAmount varchar(50) NULL,
    processingFee varchar(50) NULL,
    shippingFee varchar(50) NULL,
    totalPrice varchar(50) NOT NULL,
    confirmed INT DEFAULT(0),
    confirmedId VARCHAR(500) NULL,
    orderedAt DATE DEFAULT(CURRENT_TIMESTAMP)
);