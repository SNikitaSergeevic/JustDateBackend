CREATE TABLE "message"(
    "id" UUID NOT NULL,
    "chat_id" UUID NOT NULL,
    "sender_id" UUID NOT NULL,
    "recipient_id" UUID NOT NULL,
    "text" TEXT NOT NULL,
    "created_at" DATE NOT NULL
);
ALTER TABLE
    "message" ADD PRIMARY KEY("id");
CREATE TABLE "card"(
    "id" UUID NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "description" TEXT NOT NULL,
    "location" VARCHAR(255) NOT NULL,
    "age" INTEGER NOT NULL,
    "sex" VARCHAR(255) NOT NULL,
    "created_at" DATE NOT NULL,
    "last_auth" DATE NOT NULL
);
ALTER TABLE
    "card" ADD PRIMARY KEY("id");
CREATE TABLE "chat"(
    "id" UUID NOT NULL,
    "owner_id" UUID NOT NULL,
    "companion_id" UUID NOT NULL,
    "created_at" DATE NOT NULL
);
ALTER TABLE
    "chat" ADD PRIMARY KEY("id");
CREATE TABLE "image"(
    "id" UUID NOT NULL,
    "path" VARCHAR(255) NOT NULL,
    "card_id" UUID NOT NULL,
    "file_name" VARCHAR(255) NOT NULL,
    "created_at" DATE NOT NULL
);
ALTER TABLE
    "image" ADD PRIMARY KEY("id");
CREATE TABLE "tag"(
    "id" UUID NOT NULL,
    "tag_name" VARCHAR(255) NOT NULL
);
ALTER TABLE
    "tag" ADD PRIMARY KEY("id");
CREATE TABLE "match"(
    "id" UUID NOT NULL,
    "card_id_sen" UUID NOT NULL,
    "card_id_rec" UUID NOT NULL,
    "rec_show" INTEGER NOT NULL,
    "sen_show" INTEGER NOT NULL,
    "match" BOOLEAN NOT NULL,
    "created_at" DATE NOT NULL,
    "id_sen" UUID NOT NULL,
    "id_rec" UUID NOT NULL
);
ALTER TABLE
    "match" ADD PRIMARY KEY("id");
CREATE TABLE "tags"(
    "id" UUID NOT NULL,
    "card_id" UUID NOT NULL,
    "tag_id" UUID NOT NULL
);
ALTER TABLE
    "tags" ADD PRIMARY KEY("id");
CREATE TABLE "owner"(
    "id" UUID NOT NULL,
    "email" VARCHAR(255) NOT NULL,
    "password" VARCHAR(255) NOT NULL,
    "location" VARCHAR(255) NOT NULL,
    "card_id" UUID NOT NULL,
    "created_at" DATE NOT NULL
);
ALTER TABLE
    "owner" ADD PRIMARY KEY("id");
CREATE TABLE "refresh_token"(
    "id" UUID NOT NULL,
    "owner_id" UUID NOT NULL,
    "token" VARCHAR(255) NOT NULL,
    "created_at" DATE NOT NULL
);
ALTER TABLE
    "refresh_token" ADD PRIMARY KEY("id");
ALTER TABLE
    "refresh_token" ADD CONSTRAINT "refresh_token_owner_id_unique" UNIQUE("owner_id");
ALTER TABLE
    "match" ADD CONSTRAINT "match_card_id_sen_foreign" FOREIGN KEY("card_id_sen") REFERENCES "card"("id");
ALTER TABLE
    "image" ADD CONSTRAINT "image_card_id_foreign" FOREIGN KEY("card_id") REFERENCES "card"("id");
ALTER TABLE
    "tags" ADD CONSTRAINT "tags_card_id_foreign" FOREIGN KEY("card_id") REFERENCES "card"("id");
ALTER TABLE
    "match" ADD CONSTRAINT "match_card_id_rec_foreign" FOREIGN KEY("card_id_rec") REFERENCES "card"("id");
ALTER TABLE
    "tags" ADD CONSTRAINT "tags_tag_id_foreign" FOREIGN KEY("tag_id") REFERENCES "tag"("id");
ALTER TABLE
    "owner" ADD CONSTRAINT "owner_card_id_foreign" FOREIGN KEY("card_id") REFERENCES "card"("id");
ALTER TABLE
    "message" ADD CONSTRAINT "message_chat_id_foreign" FOREIGN KEY("chat_id") REFERENCES "chat"("id");
ALTER TABLE
    "refresh_token" ADD CONSTRAINT "refresh_token_owner_id_foreign" FOREIGN KEY("owner_id") REFERENCES "owner"("id");
ALTER TABLE
    "chat" ADD CONSTRAINT "chat_owner_id_foreign" FOREIGN KEY("owner_id") REFERENCES "owner"("id");
ALTER TABLE
    "chat" ADD CONSTRAINT "chat_companion_id_foreign" FOREIGN KEY("companion_id") REFERENCES "owner"("id");