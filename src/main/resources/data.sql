-- Users table (matches com.blog.entity.User)
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Categories table (matches com.blog.entity.Category)
CREATE TABLE IF NOT EXISTS categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Pages table (matches com.blog.entity.Page)
CREATE TABLE IF NOT EXISTS pages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  slug VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Contacts table (matches com.blog.entity.Contact)
CREATE TABLE IF NOT EXISTS contacts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  phone VARCHAR(20),
  message TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Blogs table (matches com.blog.entity.Blog)
CREATE TABLE IF NOT EXISTS blogs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  image LONGBLOB,
  is_featured BOOLEAN DEFAULT FALSE,
  is_active BOOLEAN DEFAULT TRUE,
  user_id BIGINT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_blogs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Clean existing data first (for re-initialization)
DELETE FROM blogs;
DELETE FROM users;
DELETE FROM categories;
DELETE FROM pages;
DELETE FROM contacts;

-- Seed users (using simplified columns present in entity.User)
-- Passwords are BCrypt-encoded (cost factor 10) - can be decoded with BCryptPasswordEncoder.matches(rawPassword, hash)
INSERT INTO users (username, password, first_name, last_name) VALUES
('Karianne','$2a$10$yc1LDA43WdjM0IEveUrdieYvbTSn.bYwoZ7rNJgxTDkULvX7rswl6','Patricia','Lebsack'),
('Kamren','$2a$10$rNBxr9ohHHz3mJn.eZAna.WlpFgbB0uqz9z1RvMfSh25T.V1h/jrC','Chelsey','Dietrich'),
('partha.chakra2','$2a$10$bL2deApaCLK9FVlubbpCI.GmxIcljiZ/IgF4YLMB7RL7r4ejwmUtu','Partha','Chakraborty'),
('v','$2a$10$ZSHZ9dsdXQ8pYm2kLRDfm.leKNEPC0C2xkzDvkgM5GWDJxLpkWVna','p','');

-- Seed categories
INSERT INTO categories (name) VALUES
('Technology'),
('Business'),
('Personal');

-- Seed pages
INSERT INTO pages (title, description, slug) VALUES
('About','About the blog','about'),
('Contact','Contact page','contact');

-- Seed contacts
INSERT INTO contacts (name, email, phone, message) VALUES
('Partha Chakraborty','partha.chakra2@gmail.com','9862029378','Initial contact message for testing');

-- Seed blogs (reference user ids 1..4)
INSERT INTO blogs (title, description, image, is_featured, is_active, user_id) VALUES
('Welcome to the Blog','This is the first seeded blog entry',FROM_BASE64(''),TRUE,TRUE,1),
('Second Post','Another seeded blog post',FROM_BASE64(''),FALSE,TRUE,2),
('Partha''s Post','Blog by Partha',FROM_BASE64(''),FALSE,TRUE,3),
('Test Post','Short test post',FROM_BASE64(''),FALSE,TRUE,4);

-- Notes:
-- Spring Boot will run this `data.sql` on startup when using the configured datasource.
-- The CREATE DATABASE statement requires the MySQL user to have CREATE privileges. If your user
-- lacks those privileges, create `userblog_DB` manually or run this script with an admin account.
