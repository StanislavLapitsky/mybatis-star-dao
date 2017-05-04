package org.mybatis.star.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.star.App;
import org.mybatis.star.entity.User;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * @author stanislav.lapitsky created 5/4/2017.
 */
public class UserDaoTest {

    private static UserDao dao;

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Before
    public void setup() {
        if (dao == null) {
            InputStream inputStream = App.class.getResourceAsStream("/org/mybatis/mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession session = sqlSessionFactory.openSession();
            initDB(session.getConnection());

            dao = new UserDao(session, "User");
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

        User userFromDB = dao.getById(1L);
        Assert.assertEquals(1L, (long) userFromDB.getId());
        Assert.assertEquals(user.getLogin(), userFromDB.getLogin());
        Assert.assertEquals(user.getEmail(), userFromDB.getEmail());
    }

    @Test
    public void testGetAll() {
        User user = new User(u -> {
            u.setLogin("test");
            u.setEmail("test@some-test.test");
        });

        List<User> all = dao.getAll();
        Assert.assertTrue(all.size() >= 1);
    }

    @Test
    public void testUserInsert() {
        User user = new User(u -> {
            u.setLogin("login2");
            u.setEmail("login2@test.test");
        });

        int insertedCount = dao.insert(user);
        Assert.assertEquals(1, insertedCount);
    }

    @Test
    public void testUpdate() {
        User user = new User(u -> {
            u.setLogin("test2");
            u.setEmail("test2@some-test.test");
        });

        int insertedCount = dao.insert(user);
        Assert.assertEquals(1, insertedCount);

        user.setLogin("UPDATE:" + user.getLogin());
        user.setEmail("UPDATE:" + user.getEmail());

        int updatedCount = dao.update(user);
        Assert.assertEquals(1, updatedCount);

        User userFromDB = dao.getById(user.getId());
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

        int insertedCount = dao.insert(user);
        Assert.assertEquals(1, insertedCount);

        int deletedCount = dao.delete(user);
        Assert.assertEquals(1, deletedCount);

        User userFromDB = dao.getById(user.getId());
        Assert.assertNull(userFromDB);
    }
}
