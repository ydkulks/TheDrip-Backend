CREATE TABLE IF NOT EXISTS users (
	id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	email VARCHAR(255) UNIQUE NOT NULL,
	username VARCHAR(100) UNIQUE NOT NULL,
	password VARCHAR(255) NOT NULL,
	role VARCHAR(50) DEFAULT 'Customer',
	created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CREATE OR REPLACE FUNCTION update_timestamp()
-- RETURNS TRIGGER AS $$
-- BEGIN
-- 	NEW.updated=CURRENT_TIMESTAMP;
-- 	RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
-- 
-- CREATE OR REPLACE TRIGGER set_updated_timestamp
-- BEFORE UPDATE ON users
-- FOR EACH ROW
-- EXECUTE FUNCTION update_timestamp();

CREATE TABLE IF NOT EXISTS categories (
	category_id SERIAL PRIMARY KEY,
	category_name TEXT UNIQUE NOT NULL,
	created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- CREATE OR REPLACE TRIGGER set_updated_timestamp
-- BEFORE UPDATE ON categories
-- FOR EACH ROW
-- EXECUTE FUNCTION update_timestamp();
-- 
CREATE TABLE IF NOT EXISTS product_sizes (
	size_id SERIAL PRIMARY KEY,
	size_name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
	product_id SERIAL PRIMARY KEY,
	product_name TEXT NOT NULL UNIQUE,
	category_id INTEGER REFERENCES categories(category_id) ON DELETE SET NULL,
	series_id INTEGER REFERENCES product_series(series_id) ON DELETE SET NULL,
	user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
	product_price MONEY NOT NULL CHECK (product_price > 0),
	product_description TEXT,
	product_stock INTEGER CHECK (product_stock >= 0),
  product_sold INTEGER DEFAULT NULL,
	created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- CREATE OR REPLACE TRIGGER set_updated_timestamp
-- BEFORE UPDATE ON product
-- FOR EACH ROW
-- EXECUTE FUNCTION update_timestamp();

CREATE TABLE IF NOT EXISTS product_product_sizes (
	id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	product_id INTEGER NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
	size_id INTEGER NOT NULL REFERENCES product_sizes(size_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product_colors (
	color_id SERIAL PRIMARY KEY,
	color_name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS product_product_colors (
	id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	product_id INTEGER NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
	color_id INTEGER NOT NULL REFERENCES product_colors(color_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product_series (
	series_id SERIAL PRIMARY KEY,
	series_name TEXT UNIQUE NOT NULL
);
-- ALTER TABLE product
-- ADD COLUMN series_id INTEGER REFERENCES product_series(series_id) ON DELETE SET NULL;
-- ALTER TABLE product
-- ADD COLUMN product_sold INTEGER DEFAULT NULL;

CREATE TABLE IF NOT EXISTS product_images (
	img_id SERIAL PRIMARY KEY,
	img_name TEXT NOT NULL,
	img_type TEXT NOT NULL,
	img_data TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS product_product_images (
	id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	product_id INTEGER NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
	img_id INTEGER NOT NULL REFERENCES product_images(img_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_reviews (
	review_id SERIAL PRIMARY KEY,
	user_id INTEGER,
	product_id INTEGER NOT NULL,
	review_title TEXT,
	review_text TEXT,
	rating INTEGER CHECK (rating >= 1 AND rating <= 5),
	created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	
	FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
	FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE ON UPDATE CASCADE,
	-- Enforce that a user can only review a product once
	UNIQUE (user_id, product_id)
);

CREATE TABLE IF NOT EXISTS cart (
	cart_id SERIAL PRIMARY KEY,
	user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
	created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
	cart_items_id SERIAL PRIMARY KEY,
	cart_id INT NOT NULL REFERENCES cart(cart_id) ON DELETE CASCADE,
	product_id INT NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
	quantity INT NOT NULL CHECK (quantity > 0),
  color TEXT DEFAULT 'original',
  size TEXT DEFAULT 'medium',
	created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UNIQUE (cart_id, product_id)
);

/*
-- NOTE: Example insert queries
INSERT INTO categories (category_name) VALUES
	('short_sleeve_tees'), ('long_sleeve_tees'), ('button_down_shirt'), ('hoodies'),
	('cargos'), ('shorts'), ('sweat_pants'), ('tops'), ('bottoms'), ('bomber_jackets');

INSERT INTO product_sizes (size_name) VALUES
	('small'),('medium'),('large'),('extra_large'),('double_extra_large');

INSERT INTO product_colors (color_name) VALUES
	('original'), ('white'), ('black');

INSERT INTO product (
	product_name,
	category_id,
	user_id,
	product_price,
	product_description,
	product_stock
) VALUES (
	'David Martinez Black Hoodie',
	4,
	2,
	70.00,
	'Printed art on front and back. Drawstring hood. Long sleeves',
	100
);

INSERT INTO product_product_sizes (product_id, size_id) VALUES
	(1, 1),
	(1, 3),
	(1, 5);

INSERT INTO product_product_colors (product_id, color_id) VALUES (1, 1);

INSERT INTO product_series (series_name) VALUES ('Cyberpunk: Edgerunners');
-- UPDATE product SET series_id=1 WHERE product_id=1;
INSERT INTO product_product_images VALUES (1, 1);

-- NOTE: Select querie to get all the details about the product
SELECT 
	p.product_id,
	p.product_name,
	p.product_description,
	p.product_price,
	p.product_stock,
	pseries.series_name,
	c.category_name,
	u.username AS seller_name,
	ARRAY_AGG(DISTINCT ps.size_name) AS sizes,
	ARRAY_AGG(DISTINCT pc.color_name) AS colors,
	ARRAY_AGG(DISTINCT pi.img_path) AS images
FROM 
	product p
LEFT JOIN 
	product_series pseries ON p.series_id = pseries.series_id
LEFT JOIN 
	categories c ON p.category_id = c.category_id
LEFT JOIN 
	users u ON p.user_id = u.id
LEFT JOIN 
	product_product_sizes pps ON p.product_id = pps.product_id
LEFT JOIN 
	product_sizes ps ON pps.size_id = ps.size_id
LEFT JOIN 
	product_product_colors ppc ON p.product_id = ppc.product_id
LEFT JOIN 
	product_colors pc ON ppc.color_id = pc.color_id
LEFT JOIN 
	product_product_images ppi ON p.product_id = ppi.product_id
LEFT JOIN 
	product_images pi ON ppi.img_id = pi.img_id
WHERE 
	p.product_id = 1 -- Replace 1 with the desired product_id
GROUP BY 
	p.product_id, pseries.series_name, c.category_name, u.username;

*/
