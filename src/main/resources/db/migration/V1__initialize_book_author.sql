-- create author table
CREATE TABLE author(
  id VARCHAR(20) PRIMARY KEY,
  first_name VARCHAR(50),
  last_name VARCHAR(50)
);

-- create book table
CREATE TABLE book(
  id VARCHAR(20) PRIMARY KEY,
  name VARCHAR(50),
  page_count SMALLINT,
  author_id VARCHAR(20)
);

-- add foreign key
ALTER TABLE book ADD FOREIGN KEY (author_id) REFERENCES author(id);

-- add authors
INSERT INTO author VALUES
('author-1', 'Joanne', 'Rowling'),
('author-2', 'Herman', 'Melville'),
('author-3', 'Anne', 'Rice');

-- add books
INSERT INTO book VALUES
('book-1', 'Harry Potter and the Philosopher''s Stone', 223, 'author-1'),
('book-2', 'Moby Dick', 635, 'author-2'),
('book-3', 'Interview with the vampire', 371, 'author-3')
