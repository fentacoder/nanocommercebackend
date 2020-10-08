CREATE TABLE Users(
    id UUID NOT NULL PRIMARY KEY,
    rowNum BIGSERIAL NOT NULL,
    firstName VARCHAR(100) NULL,
    lastName VARCHAR(100) NULL,
    email VARCHAR(200) NULL,
    password VARCHAR(500) NULL,
    phoneNumber VARCHAR(11) NULL,
    image bytea NULL,
    imageType VARCHAR(10) NULL,
    twitter VARCHAR(100) NULL,
    bio VARCHAR(500) NULL,
    city VARCHAR(200) NULL,
    state VARCHAR(200) NULL,
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP)
);