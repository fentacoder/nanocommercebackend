CREATE TABLE Messages(
    id UUID NOT NULL PRIMARY KEY,
    rowNum BIGSERIAL NOT NULL,
    senderId UUID NOT NULL,
    senderName varchar(150) NOT NULL,
    receiverId UUID NOT NULL,
    message varchar(500) NULL,
    readYet int NOT NULL DEFAULT(0),
    type varchar(50) NOT NULL,
    image bytea NULL,
    imageType varchar(30) NULL,
    sentAt TIMESTAMP(3) with time zone
);