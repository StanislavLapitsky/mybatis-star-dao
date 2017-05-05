package org.mybatis.star.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.star.App;
import org.mybatis.star.entity.City;
import org.mybatis.star.entity.User;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * Tests basic CRUD operations for UserDao extending GenericDAO
 * @author stanislav.lapitsky created 5/4/2017.
 */
public class UserDaoTest {

    private static UserDao userDao;
    private static CityDao cityDao;

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Before
    public void setup() {
        if (userDao == null) {
            InputStream inputStream = App.class.getResourceAsStream("/org/mybatis/mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession session = sqlSessionFactory.openSession();
            initDB(session.getConnection());

            userDao = new UserDao(session, "User");
            cityDao = new CityDao(session, "City");
        }
    }

    private void initDB(Connection conn) {
        try {
            Statement st = conn.createStatement();
            InputStream is = App.class.getResourceAsStream("/org/mybatis/init-db.sql");
            String initDbSQL = convertStreamToString(is);
            st.execute(initDbSQL);
            st.execute("INSERT INTO USERS (LOGIN, EMAIL) VALUES('test','test@some-test.test')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetById() {
        User user = new User(u -> {
            u.setLogin("test");
            u.setEmail("test@some-test.test");
        });

        User userFromDB = userDao.getById(1L);
        Assert.assertEquals(1L, (long) userFromDB.getId());
        Assert.assertEquals(user.getLogin(), userFromDB.getLogin());
        Assert.assertEquals(user.getEmail(), userFromDB.getEmail());
    }

    @Test
    public void testGetAll() {
        List<User> all = userDao.getAll();
        Assert.assertTrue(all.size() >= 1);
    }

    @Test
    public void testUserInsert() {
        User user = new User(u -> {
            u.setLogin("login2");
            u.setEmail("login2@test.test");
        });

        int insertedCount = userDao.insert(user);
        Assert.assertEquals(1, insertedCount);
    }

    @Test
    public void testUpdate() {
        User user = new User(u -> {
            u.setLogin("test2");
            u.setEmail("test2@some-test.test");
        });

        int insertedCount = userDao.insert(user);
        Assert.assertEquals(1, insertedCount);

        user.setLogin("UPDATE:" + user.getLogin());
        user.setEmail("UPDATE:" + user.getEmail());

        int updatedCount = userDao.update(user);
        Assert.assertEquals(1, updatedCount);

        User userFromDB = userDao.getById(user.getId());
        Assert.assertEquals(user.getId(), userFromDB.getId());
        Assert.assertEquals(user.getLogin(), userFromDB.getLogin());
        Assert.assertEquals(user.getEmail(), userFromDB.getEmail());
    }

    @Test
    public void testDelete() {
        User user = new User(u -> {
            u.setLogin("test3");
            u.setEmail("test3@some-test.test");
        });

        int insertedCount = userDao.insert(user);
        Assert.assertEquals(1, insertedCount);

        int deletedCount = userDao.delete(user);
        Assert.assertEquals(1, deletedCount);

        User userFromDB = userDao.getById(user.getId());
        Assert.assertNull(userFromDB);
    }

    @Test
    public void testWorkWithAssociation() {
        City c1 = new City(c -> {
            c.setName("Minsk");
        });
        City c2 = new City(c -> {
            c.setName("London");
        });
        cityDao.insert(c1);
        cityDao.insert(c2);
        User user = new User(u -> {
            u.setLogin("test4");
            u.setEmail("test4@some-test.test");
            u.setCity(c1);
        });

        int insertedCount = userDao.insert(user);
        Assert.assertEquals(1, insertedCount);

        user.setLogin("UPDATE:" + user.getLogin());
        user.setEmail("UPDATE:" + user.getEmail());
        user.setCity(c2);

        int updatedCount = userDao.update(user);
        Assert.assertEquals(1, updatedCount);

        User userFromDB = userDao.getById(user.getId());
        Assert.assertEquals(user.getId(), userFromDB.getId());
        Assert.assertEquals(user.getLogin(), userFromDB.getLogin());
        Assert.assertEquals(user.getEmail(), userFromDB.getEmail());
        Assert.assertEquals(user.getCity().getName(), c2.getName());
    }

}
