# jdom

jdom读取xml文件

引入包

```xml
    <dependency>
      <groupId>org.jdom</groupId>
      <artifactId>jdom</artifactId>
      <version>2.0.2</version>
    </dependency>
```

代码解析`pom.xml`文件

```java
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class XmlUtil {

    /**
    * 解析pom文件
    */
    public void parseXml(){
        String fileName = "pom.xml";
        SAXBuilder saxBuilder = new SAXBuilder();
        File file = new File(fileName);
        try {
            Document document = saxBuilder.build(file);
            Element rootNode = document.getRootElement();
            //获取namespace
            Namespace namespace = rootNode.getNamespace();
            List<Element> children = rootNode.getChildren();
            for (Element child : children) {
                log.info(child.getName());
            }
            //下面获取节点这要带上namespace,不然获取不到数据
            Element node = rootNode.getChild("dependencies",namespace);
            List<Element> dependencyList = node.getChildren("dependency",namespace);
            for (int i1 = 0; i1 < dependencyList.size(); i1++) {
                Element element = dependencyList.get(i1);
                String artifactId = element.getChildText("artifactId",namespace);
                log.info(artifactId);
            }

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
    * 解析一般xml文件
    */
     public void parseXml2(){
        String fileName = ".\\xml\\test.xml";
        SAXBuilder saxBuilder = new SAXBuilder();
        File file = new File(fileName);
        try {
            Document document = saxBuilder.build(file);
            Element rootElement = document.getRootElement();
            //这里不需要Namespace
            List<Element> list = rootElement.getChildren("author");
            for (Element element : list) {
                log.info(element.getChildText("firstname"));
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new XmlUtil().parseXml();
    }
}
```

`test.xml`

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<book>
    <author>
        <firstname>Alice</firstname>
        <lastname>Peterson</lastname>
    </author>
    <author>
        <firstname>John</firstname>
        <lastname>Doe</lastname>
    </author>
</book>
```

