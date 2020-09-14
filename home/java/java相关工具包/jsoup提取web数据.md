# JSoup

引入包

```xml
<!--    jsoup提取web数据-->
    <dependency>
      <groupId>org.jsoup</groupId>
      <artifactId>jsoup</artifactId>
      <version>1.12.1</version>
    </dependency>
```

java代码

```java
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

@Slf4j
public class JsoupUtil {

    public void extractDataWithJsoup(){
        String href = "";
        try {
            Document document = Jsoup.connect(href)
                                        .timeout(10*1000)
                                        .userAgent("Mozilla")
                                        .ignoreHttpErrors(true)
                                        .get();
            if (document != null){
                String title = document.title();
                String text = document.body().text();
                log.info("title : "+title);
                log.info("text : "+text);
                Elements links = document.select("a[href]");
                for (Element link : links) {
                    String linkHref = link.attr("href");
                    String linkText = link.text();
                    String outerHtml = link.outerHtml();
                    String html = link.html();
                    log.info("linkhref : "+linkHref+"\n linkText : "+linkText+"\n outerHtml : "+outerHtml+"\n html : "+html);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new JsoupUtil().extractDataWithJsoup();
    }
}
```



