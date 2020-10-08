CREATE TABLE ActivitiesImages(
    id UUID NOT NULL PRIMARY KEY,
    activityId UUID NOT NULL,
    imageData bytea NOT NULL,
    type VARCHAR(10) NOT NULL,
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_activity FOREIGN KEY (activityId) REFERENCES Activities(id)
);