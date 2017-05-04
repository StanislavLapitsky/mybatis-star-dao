# MyBatis-star-dao
MyBatis generic CRUD DAO based on ResultMapping only.

## Introduction
MyBatis is a great ORM flexible and powerful but the flexibility requires 
additional efforts. Normally you define mapping and define CRUD operations in the 
mapping INSERT/UPDATE/DELETE statements. <br />
INSERT and UPDATE normally looks like this:
```
<insert id="insert">
  INSERT INTO USERS (ID, LOGIN, EMAIL, ...)
  VALUES (#{id},#{login},#{email}, ...)
</insert>
```
```
<update id="update">
  UPDATE USER SET
    LOGIN = #{login},
    EMAIL = #{email},
    ...
  WHERE ID = #{id}
</update>
```

Every time when you need to add a new field/column you have to go to the 
INSERT and UPDATE statements and manually add the new COLUMN and property 
for the column. <br />
That's really annoying. To fix this a new (yeah, one more:) GenericDAO was
created.