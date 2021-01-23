CREATE TABLE YELP_BUSINESS(
business_id         VARCHAR2(50) PRIMARY KEY,
full_address        VARCHAR2(200) ,
city            VARCHAR2(50),
state           VARCHAR2(50),
review_count	int,
name       VARCHAR2(300),
stars           VARCHAR2(100)

);

CREATE TABLE BUSINESS_HOURS(
BUSINESS_ID VARCHAR2(50),
B_day varchar(50),
B_open varchar(50),
B_close varchar(50),
FOREIGN KEY (business_id) REFERENCES YELP_BUSINESS(business_id) ON
DELETE CASCADE
);

CREATE TABLE MAIN_CATEGORY(
business_id varchar(50),
mainc varchar(2000),
foreign key(business_id) REFERENCES YELP_BUSINESS(business_id) ON DELETE CASCADE);

CREATE TABLE SUBCATEGORIES(
business_id varchar(50), 
subc varchar(2000),
foreign key(business_id) REFERENCES YELP_BUSINESS(business_id) ON DELETE CASCADE
);

CREATE TABLE ATTRIBUTES(
business_id varchar(50),
attr varchar(2000),
foreign key(business_id) REFERENCES YELP_BUSINESS(business_id) ON DELETE CASCADE
);

CREATE TABLE YELP_USER(
user_name           VARCHAR2(300),
user_id         VARCHAR2(50) PRIMARY KEY
);

CREATE TABLE YELP_CHECKIN(
checkin_info      VARCHAR2(2000),
rev_type              VARCHAR2(200),
business_id         VARCHAR2(500) REFERENCES YELP_BUSINESS(business_id) ON DELETE CASCADE
);

CREATE TABLE YELP_REVIEW(
user_id         VARCHAR2(50) REFERENCES YELP_USER(user_id) ON DELETE CASCADE,
review_id         VARCHAR2(50) PRIMARY KEY,
stars           VARCHAR2(100),
create_date     VARCHAR2(50),
text            CLOB,
business_id         VARCHAR2(50) REFERENCES YELP_BUSINESS(business_id) ON DELETE CASCADE,
votes           int
);
