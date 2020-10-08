CREATE TABLE Activities(
    id UUID NOT NULL PRIMARY KEY,
    rowNum BIGSERIAL NOT NULL,
    hostId UUID NOT NULL,
    title varchar(100) NOT NULL,
    location varchar(150) NOT NULL,
    price varchar(20) NULL,
    details varchar(500) NULL,
    breakDescription varchar(500) NULL,
    activityDate varchar(100) NOT NULL,
    activityTime varchar(100) NOT NULL,
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_host FOREIGN KEY (hostId) REFERENCES Users(id)
);