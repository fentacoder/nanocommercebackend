CREATE TABLE Comments(
    id UUID NOT NULL PRIMARY KEY,
    authorId UUID NOT NULL,
    postId UUID NOT NULL,
    message varchar(500) NOT NULL,
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_author FOREIGN KEY (authorId) REFERENCES Users(id),
    CONSTRAINT fk_post FOREIGN KEY (postId) REFERENCES Posts(id)
);