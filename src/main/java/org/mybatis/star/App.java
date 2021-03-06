package org.mybatis.star;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.star.dao.CityDao;
import org.mybatis.star.dao.UserDao;
import org.mybatis.star.entity.City;
import org.mybatis.star.entity.User;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * Just test application. Used for debug only.
 * @author stanislav.lapitsky created 5/3/2017.
 */
public class App {

    public static void main(String[] args) {
        InputStream inputStream = App.class.getResourceAsStream("/org/mybatis/mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sqlSessionFactory.openSession();
        initDB(session.getConnection());

        City city = new City(c -> {
            c.setName("Minsk");
        });
        CityDao cityDao = new CityDao(session, "City");
        cityDao.insert(city);
        List<City> cities = cityDao.getAll();

        User user = new User(u -> {
            u.setLogin("login2");
            u.setEmail("login2@test.test");
            u.setCity(city);
        });

        UserDao userDao = new UserDao(session, "User");
        int insertedCount = userDao.insert(user);
        System.out.println("insertedCount=" + insertedCount);

        user = userDao.getById(user.getId());

        user.setLogin("UPD:login2");
        user.setEmail("UPD:login2@test.test");
        int updatedCount = userDao.update(user);
        System.out.println("updatedCount=" + updatedCount);

//        int deletedCount = userDao.delete(user);
//        System.out.println("deletedCount=" + deletedCount);

        List<User> users = userDao.getAll();
        System.out.println(users);
    }

    private static void initDB(Connection conn) {
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

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
