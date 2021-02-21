create table warehouse
(
	id          serial  not null    primary key,
	product_id  serial  not null,
	amount      int     not null,
	is_in_stock   boolean
);
