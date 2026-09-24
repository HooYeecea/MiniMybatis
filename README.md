# MiniMyBatis

迷你 MyBatis：在标准 JDBC（`java.sql`）之上实现 `SqlSession` / Mapper 代理 / 简单 XML 映射。

## 跑 Demo

```bash
mvn -pl MiniMyBatis -am install -DskipTests
mvn -f MiniMyBatis/pom.xml exec:java
```

## 第一期能力

- `Configuration` / `SqlSessionFactory` / `SqlSession`
- `SimpleExecutor` + `PreparedStatement`
- `#{param}` 参数绑定
- Mapper 接口动态代理
- 简单 mapper XML（无动态 SQL）
