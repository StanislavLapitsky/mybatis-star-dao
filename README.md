# MyBatis-star-dao
MyBatis generic CRUD DAO based on ResultMapping only. Could be useful 
when DB structure is actively changed e.g. on project start.

## Introduction
MyBatis is a great ORM flexible and powerful but the flexibility requires 
additional efforts. Normally you define mapping and define CRUD operations 
in the mapping INSERT/UPDATE/DELETE statements. <br />
INSERT and UPDATE normally looks like this:
```xml
<insert id="insert">
  INSERT INTO USERS (ID, LOGIN, EMAIL, ...)
  VALUES (#{id},#{login},#{email}, ...)
</insert>
```
```xml
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
That's really annoying. To fix this the GenericDAO was created (yeah, 
one more:).

With the Generic DAO extension no need to manually add new column to 
INSERT and UPDATE statements. It is enough to define property/column 
mapping and INSERT/UPDATE statements get the new property on fly.

## How to use
Initially MyBatis setup is the same. The mybatis-config.xml should be 
created and list of **<typeAliases>** and **<mappers>** should be defined.<br />
Let's consider we have a User entity POJO and corresponding table in DB. The User class 
is a simple one with getters/setters.
```java
public class User {
    private Long id;
    private String login;
    private String email;

    //getters/setters there
}
```
and now we need a mapping for the class. The mapping is almost the same for all entities (see below).
```xml
<mapper namespace="User">
    <resultMap id="User" type="User">
        <id property="id" column="ID"/>
        <result property="login" column="LOGIN"/>
        <result property="email" column="EMAIL"/>
    </resultMap>

    <insert id="insert" keyProperty="entity.id" keyColumn="ID" useGeneratedKeys="true">
        INSERT INTO USERS
        <!--(ID, LOGIN, EMAIL)-->
        <foreach collection="resultMappings" item="entry" separator="," open="(" close=")">
            ${entry.column}
        </foreach>
        VALUES
        <!--(#{id}, #{login}, #{email})-->
        <foreach collection="resultMappings" item="entry" separator="," open="(" close=")">
            #{entity.${entry.property}}
        </foreach>
    </insert>

    <update id="update" keyProperty="id" keyColumn="ID">
        UPDATE USERS
        SET
        <foreach collection="resultMappings" item="entry" separator=",">
            <if test="!entry.isId">
                ${entry.column} = #{entity.${entry.property}}
            </if>
        </foreach>
        WHERE
        <foreach collection="resultMappings" item="entry" separator=" AND ">
            <if test="entry.isId">
                ${entry.column} = #{entity.${entry.property}}
            </if>
        </foreach>
    </update>
    ...
</mapper>
```
If you look at the **<insert>** and **<update>** statement you can see that no 
field and column names are used. The names are automatically resolved from the 
**<resultMap>**. If a new column/propery is added we just adapt the resultMap 
and the field's value is inserted/updated on fly.<br />
The UserDAO is added and all the CRUD methods are available.
```java
public class UserDao extends GenericDAO<User, Long> {

    public UserDao(SqlSession session, String mappingName) {
        super(session, mappingName);
    }
}
```