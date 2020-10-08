CREATE TABLE ArticleImages(
    id UUID NOT NULL PRIMARY KEY,
    articleId UUID NOT NULL,
    imageData bytea NOT NULL,
    type VARCHAR(20) NOT NULL,
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_article FOREIGN KEY (articleId) REFERENCES Articles(id)
);