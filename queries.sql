/*CREATES TABLE OF BUSINESS_HOUR*/
CREATE TABLE BUSINESS_HOUR AS
    SELECT DISTINCT H.business_id, H.b_day, H.b_open, H.b_close
    FROM BUSINESS_HOURS H
    WHERE (H.business_id, H.b_day, H.b_open, H.b_close) IN
    (SELECT DISTINCT H.business_id, H.b_day, H.b_open, H.b_close
    FROM BUSINESS_HOURS H)  
    AND ROWNUM<=300000


/*find subcategories from main categories*/
SELECT DISTINCT sc.subc 
FROM SUBCATEGORIES sc, MAIN_CATEGORY mc 
WHERE sc.business_id = mc.business_id AND ( mc.mainc = 'Beauty & Spas' AND/OR mc.mainc='Restaurants') 
ORDER BY sc.subc

/*find attributes from subcategories, main categories*/
SELECT DISTINCT at.attr 
FROM Attributes at, SUBCATEGORIES sc1
WHERE at.business_id = sc1.business_id AND at.business_id IN 
(SELECT mc.business_id 
FROM SUBCATEGORIES sc, MAIN_CATEGORY mc 
WHERE sc.business_id = mc.business_id AND ( mc.mainc = 'Beauty & Spas' OR mc.mainc='Restaurants'))
ORDER BY at.attr

/*find state and city from main category, attributes and subcategories*/
SELECT DISTINCT B.city, B.state
FROM YELP_BUSINESS B
WHERE B.business_id IN
(SELECT at.business_id
FROM Attributes at, SUBCATEGORIES sc1
WHERE at.business_id = sc1.business_id AND at.attr='Accepts Credit Cards_true' AND at.business_id IN 
(SELECT mc.business_id 
FROM SUBCATEGORIES sc, MAIN_CATEGORY mc 
WHERE sc.business_id = mc.business_id AND ( mc.mainc = 'Beauty & Spas' OR mc.mainc='Restaurants')))
ORDER BY B.STATE

/*find day of the week from main category, attributes, subcategories, state and city*/
SELECT DISTINCT H.B_day
FROM BUSINESS_HOUR H, YELP_BUSINESS B1  
WHERE H.business_id=B1.business_id AND B1.city='Phoenix' AND B1.state='AZ' AND B1.business_id IN
(SELECT at.business_id
FROM Attributes at, SUBCATEGORIES sc1
WHERE at.business_id = sc1.business_id AND at.attr='Accepts Credit Cards_true' AND at.business_id IN 
(SELECT mc.business_id 
FROM SUBCATEGORIES sc, MAIN_CATEGORY mc 
WHERE sc.business_id = mc.business_id AND ( mc.mainc = 'Beauty & Spas' OR mc.mainc='Restaurants'))))
ORDER BY H.B_day

/*find FROM time from day of the week, main category, attributes, subcategories, state and city*/
SELECT DISTINCT H1.B_open
FROM BUSINESS_HOUR H1
WHERE H1.B_day='Monday' AND H1.business_id IN
(SELECT B1.business_id
FROM BUSINESS_HOUR H, YELP_BUSINESS B1  
WHERE H.business_id=B1.business_id AND B1.city='Phoenix' AND B1.state='AZ' AND B1.business_id IN
(SELECT at.business_id
FROM Attributes at, SUBCATEGORIES sc1
WHERE at.business_id = sc1.business_id AND subc='Mexican' AND at.attr='Accepts Credit Cards_true' AND at.business_id IN 
(SELECT mc.business_id 
FROM SUBCATEGORIES sc, MAIN_CATEGORY mc 
WHERE sc.business_id = mc.business_id AND ( mc.mainc = 'Beauty & Spas' OR mc.mainc='Restaurants'))))
ORDER BY H1.B_open

/*find TO time from day of the week, main category, attributes, subcategories, state and city*/
SELECT DISTINCT H1.B_close
FROM BUSINESS_HOUR H1
WHERE H1.B_day='Monday' AND H1.business_id IN
(SELECT B1.business_id
FROM BUSINESS_HOUR H, YELP_BUSINESS B1  
WHERE H.business_id=B1.business_id AND B1.city='Phoenix' AND B1.state='AZ' AND B1.business_id IN
(SELECT at.business_id
FROM Attributes at, SUBCATEGORIES sc1
WHERE at.business_id = sc1.business_id AND subc='Mexican' AND at.attr='Accepts Credit Cards_true' AND at.business_id IN 
(SELECT mc.business_id 
FROM SUBCATEGORIES sc, MAIN_CATEGORY mc 
WHERE sc.business_id = mc.business_id AND ( mc.mainc = 'Beauty & Spas' OR mc.mainc='Restaurants'))))
ORDER BY H1.B_close

/*search button with everything*/
SELECT DISTINCT B2.name, B2.city, B2.State, B2.Stars
FROM YELP_BUSINESS B2
WHERE B2.business_id IN
(SELECT H2.business_id
FROM BUSINESS_HOUR H2
WHERE H2.B_open='08:00' AND H2.B_close='17:00' AND H2.business_id IN
(SELECT H1.business_id
FROM BUSINESS_HOUR H1
WHERE H1.B_day='Monday' AND H1.business_id IN
(SELECT B1.business_id
FROM BUSINESS_HOUR H, YELP_BUSINESS B1  
WHERE H.business_id=B1.business_id AND B1.city='Phoenix' AND B1.state='AZ' AND B1.business_id IN
(SELECT at.business_id
FROM Attributes at, SUBCATEGORIES sc1
WHERE at.business_id = sc1.business_id AND subc='Mexican' AND at.attr='Accepts Credit Cards_true' AND at.business_id IN 
(SELECT mc.business_id 
FROM SUBCATEGORIES sc, MAIN_CATEGORY mc 
WHERE sc.business_id = mc.business_id AND ( mc.mainc = 'Beauty & Spas' OR mc.mainc='Restaurants'))))))
ORDER BY B2.name


/*review button with everything*/
SELECT R.create_date AS REVIEWDATE, R.stars AS STARS, R.text AS REVIEWTEXT, R.user_id AS USERID, R.votes AS VOTES
FROM YELP_REVIEW R
WHERE R.business_id IN
(SELECT B2.business_id
FROM YELP_BUSINESS B2
WHERE B2.name='Canyon Cafe' AND B2.business_id IN
(SELECT H2.business_id
FROM BUSINESS_HOUR H2
WHERE H2.B_open='08:00' AND H2.B_close='17:00' AND H2.business_id IN
(SELECT H1.business_id
FROM BUSINESS_HOUR H1
WHERE H1.B_day='Monday' AND H1.business_id IN
(SELECT B1.business_id
FROM BUSINESS_HOUR H, YELP_BUSINESS B1  
WHERE H.business_id=B1.business_id AND B1.city='Phoenix' AND B1.state='AZ' AND B1.business_id IN
(SELECT at.business_id
FROM Attributes at, SUBCATEGORIES sc1
WHERE at.business_id = sc1.business_id AND subc='Mexican' AND at.attr='Accepts Credit Cards_true' AND at.business_id IN 
(SELECT mc.business_id 
FROM SUBCATEGORIES sc, MAIN_CATEGORY mc 
WHERE sc.business_id = mc.business_id AND ( mc.mainc = 'Beauty & Spas' OR mc.mainc='Restaurants'))))))
ORDER BY R.create_date

