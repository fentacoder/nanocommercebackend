CREATE TABLE ActivityMembers(
    id UUID NOT NULL PRIMARY KEY,
    memberId UUID NOT NULL,
    activityId UUID NOT NULL,
    teamNumber int NOT NULL,
    createdAt DATE DEFAULT(CURRENT_TIMESTAMP),
    CONSTRAINT fk_member FOREIGN KEY (memberId) REFERENCES Users(id),
    CONSTRAINT fk_activity FOREIGN KEY (activityId) REFERENCES Activities(id)
);