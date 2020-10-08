CREATE TABLE PostsImages(
    id UUID NOT NULL PRIMARY KEY,
    postId UUID NOT NULL,
    imageData bytea NOT NULL,
    type VARCHAR(10) NOT NULL,
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_post FOREIGN KEY (postId) REFERENCES Posts(id)
);