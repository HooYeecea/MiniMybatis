package com.minimybatis.parsing;

import com.minimybatis.Configuration;
import com.minimybatis.mapping.MappedStatement;
import com.minimybatis.mapping.SqlCommandType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * 解析简单 mapper XML：namespace + select/insert/update/delete，支持 #{}。
 */
public final class XmlMapperParser {

    private XmlMapperParser() {
    }

    public static void parse(Configuration configuration, String classpathLocation) {
        String path = classpathLocation.startsWith("/")
                ? classpathLocation.substring(1)
                : classpathLocation;
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new RuntimeException("找不到 mapper XML: " + classpathLocation);
        }
        try (in) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            // 避免外部 DTD 拉取
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document doc = factory.newDocumentBuilder().parse(in);
            Element root = doc.getDocumentElement();
            if (!"mapper".equals(root.getTagName())) {
                throw new RuntimeException("根节点必须是 <mapper>: " + classpathLocation);
            }
            String namespace = root.getAttribute("namespace");
            if (namespace == null || namespace.isBlank()) {
                throw new RuntimeException("mapper 缺少 namespace: " + classpathLocation);
            }

            try {
                Class<?> mapperType = Class.forName(namespace);
                if (mapperType.isInterface()) {
                    configuration.addMapper(mapperType);
                }
            } catch (ClassNotFoundException ignored) {
                // XML-only：允许没有对应接口
            }

            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element element = (Element) node;
                SqlCommandType commandType = toCommandType(element.getTagName());
                if (commandType == null) {
                    continue;
                }
                String id = element.getAttribute("id");
                if (id == null || id.isBlank()) {
                    throw new RuntimeException("语句缺少 id: " + classpathLocation);
                }
                String resultTypeName = element.getAttribute("resultType");
                Class<?> resultType = configuration.resolveType(resultTypeName);
                String sql = element.getTextContent().trim().replaceAll("\\s+", " ");
                String statementId = namespace + "." + id;
                configuration.addMappedStatement(
                        new MappedStatement(statementId, commandType, sql, resultType));
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("解析 mapper XML 失败: " + classpathLocation, e);
        }
    }

    private static SqlCommandType toCommandType(String tag) {
        return switch (tag) {
            case "select" -> SqlCommandType.SELECT;
            case "insert" -> SqlCommandType.INSERT;
            case "update" -> SqlCommandType.UPDATE;
            case "delete" -> SqlCommandType.DELETE;
            default -> null;
        };
    }
}
