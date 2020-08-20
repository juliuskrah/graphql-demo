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
  page_count TINYINT,
  author_id VARCHAR(20)
);

-- add foreign key
ALTER TABLE book ADD FOREIGN KEY (author_id) REFERENCES author(id)