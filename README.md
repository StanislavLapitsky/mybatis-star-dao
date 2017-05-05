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
  VALUES (#{id},#{name},#{email}, ...)
</insert>
```
```xml
<update id="update">
  UPDATE USER SET
    LOGIN = #{name},
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
    private String name;
    private String email;

    //getters/setters there
}
```
and now we need a mapping for the class. The mapping is almost the same for all entities (see below).
```xml
<mapper namespace="User">

    <!--a reference to parent base-mapper.xml where CRUD logic mapping is done-->
    <cache-ref namespace="GenericDAO" />

    <resultMap id="defaultResultMap" type="User">
        <id property="id" column="ID"/>
        <result property="name" column="LOGIN"/>
        <result property="email" column="EMAIL"/>
    </resultMap>

    <!--customize DB table name there to be used in CRUD-->
    <sql id="TableName">USERS</sql>

    <!--we cannot move selectAll to base-mapper.xml because User.defaultResultMap is not available there-->
    <select id="selectAll" resultMap="defaultResultMap">
        SELECT * FROM ${tableName}
    </select>

    <!--we cannot move selectById to base-mapper.xml because User.defaultResultMap is not available there-->
    <select id="selectById" resultMap="defaultResultMap">
        SELECT * FROM ${tableName}
        WHERE ID=#{entity}
    </select>

</mapper>
```
The mapping needs to define two things - table name sql and defaultResultMap where 
POJO properties and DB table columns are mapped. <br />
The last step is to define UserDAO class and all the CRUD operations are available.
```java
public class UserDao extends GenericDAO<User, Long> {

    public UserDao(org.apache.ibatis.session.SqlSession session, String mappingName) {
        super(session, mappingName);
    }
}
```
The dao can be used this way (See also UserDaoTest).
```java
public class App {

    public static void main(String[] args) {
        InputStream inputStream = App.class.getResourceAsStream("/org/mybatis/mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sqlSessionFactory.openSession();
        //initDB(session.getConnection());

        User user = new User(u -> {
            u.setLogin("login2");
            u.setEmail("login2@test.test");
        });

        UserDao dao = new UserDao(session, "User");
        int insertedCount = dao.insert(user);
        System.out.println("insertedCount=" + insertedCount);

        user = dao.getById(user.getId());

        user.setLogin("UPD:login2");
        user.setEmail("UPD:login2@test.test");
        int updatedCount = dao.update(user);
        System.out.println("updatedCount=" + updatedCount);

        int deletedCount = dao.delete(user);
        System.out.println("deletedCount=" + deletedCount);

        List<User> users = dao.getAll();
        System.out.println(users);
    }
}
```