CREATE TABLE Posts(
    id UUID NOT NULL PRIMARY KEY,
    rowNum BIGSERIAL NOT NULL,
    authorId UUID NOT NULL,
    title varchar(200) NOT NULL,
    price varchar(50) NULL,
    message varchar(500) NOT NULL,
    likes int NOT NULL DEFAULT(0),
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_author FOREIGN KEY (authorId) REFERENCES Users(id)
);