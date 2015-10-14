-- name: create-user!
-- creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- name: update-user!
-- update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- name: get-user
-- retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- name: delete-user!
-- delete a user given the id
DELETE FROM users
WHERE id = :id




-- name: get-authors
-- get a list of authors
SELECT * FROM dmd.authors OFFSET :offset LIMIT :limit

-- name: get-author
-- get a specific author by id
SELECT * FROM dmd.authors WHERE id = :id


-- name: get-author-by-name
SELECT * FROM dmd.authors WHERE keyname = :keyname and forenames = :forenames

-- name: create-author!
-- create an author
INSERT INTO dmd.authors
(keyname, forenames, date_of_birthday, affiliation)
VALUES (:keyname, :forenames, :date_of_birthday, :affiliation)

-- name: update-author!
-- update an author's info
UPDATE dmd.authors
SET keyname = :keyname, forenames = :forenames, date_of_birthday = :date_of_birthday, affiliation = :affiliation
WHERE id = :id

-- name: get-publications
-- get a list of publications
SELECT * FROM dmd.publications WHERE OFFSET :offset LIMIT :limit

-- name: get-publication
-- get a specific publication by id
SELECT * FROM dmd.publications WHERE id = :id


-- name: get-publications-by-title
-- get a list of publications
SELECT * FROM dmd.publications WHERE title like :title limit 10

-- name: get-publications-by-author
-- get a list of author's publications
SELECT * FROM dmd.publications WHERE id in
  (SELECT publication_id FROM dmd.author_of WHERE author_id = :author_id)

-- name: get-publications-by-author-name
SELECT * FROM dmd.publications WHERE id in
  (SELECT publication_id FROM dmd.author_of WHERE author_id in
    (select id from dmd.authors where keyname = :keyname and forenames = :forenames))

-- name: create-publication!
-- create a publication
INSERT INTO dmd.publications
(uid, title, date_created, date_updated, journal_ref, abstract, doi, isbn, subcategories, comments)
VALUES (:uid, :title, :date_created, :date_updated, :journal_ref, :abstract, :doi, :isbn, :subcategories, :comments)

-- name: update-publication!
-- update a publication info
UPDATE dmd.publications
SET uid = :uid, title = :title, date_created = :date_created, date_updated = :date_updated,
  journal_ref = :journal_ref, abstract = :abstract, doi = :doi, isbn = :isbn,
  subcategories = :subcategories, comments = :comments
WHERE id = :id

-- name: bind-publication-to-author!
-- bind a publication to an author
INSERT INTO dmd.author_of
(author_id, publication_id)
VALUES (:author_id, :publication_id)

-- name: get-categories
-- get a list of categories available
SELECT * FROM dmd.categories OFFSET :offset LIMIT :limit

-- name: get-category
-- get a particular category by id
SELECT * FROM dmd.categories WHERE id = :id

-- name: create-category!
-- create a new category
INSERT INTO dmd.categories
(category_name)
VALUES (:category_name)

-- name: update-category!
-- update a specific category
UPDATE dmd.categories
SET category_name = :category_name
WHERE id = :id

