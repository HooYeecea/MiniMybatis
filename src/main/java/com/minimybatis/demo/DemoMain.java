package com.minimybatis.demo;

import com.minimybatis.Configuration;
import com.minimybatis.SqlSession;
import com.minimybatis.SqlSessionFactory;
import com.minimybatis.datasource.UnpooledDataSource;
import com.minimybatis.session.DefaultSqlSessionFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * 不依赖 IoC：直接打开 SqlSession，用 Mapper 代理跑 H2 增删改查。
 */
public class DemoMain {

    public static void main(String[] args) throws Exception {
        UnpooledDataSource dataSource = new UnpooledDataSource(
                "org.h2.Driver",
                "jdbc:h2:mem:minimybatis;DB_CLOSE_DELAY=-1",
                "sa",
                "");

        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("""
                    CREATE TABLE users (
                      id INT PRIMARY KEY,
                      name VARCHAR(64),
                      age INT
                    )
                    """);
        }

        Configuration configuration = new Configuration();
        configuration.setDataSource(dataSource);
        configuration.addTypeAlias("User", User.class);
        configuration.addMapperXml("mappers/UserMapper.xml");

        SqlSessionFactory factory = new DefaultSqlSessionFactory(configuration);

        try (SqlSession session = factory.openSession(true)) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            mapper.insert(new User(1, "Alice", 20));
            mapper.insert(new User(2, "Bob", 22));

            User alice = mapper.findById(1);
            System.out.println("findById(1) = " + alice);

            List<User> all = mapper.findAll();
            System.out.println("findAll = " + all);

            mapper.update(new User(1, "Alice-Updated", 21));
            System.out.println("after update = " + mapper.findById(1));

            mapper.deleteById(2);
            System.out.println("after delete = " + mapper.findAll());
        }
    }
}
