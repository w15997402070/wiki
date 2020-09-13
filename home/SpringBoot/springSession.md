# spring-session-redis设置

spring-session 后面版本sessionId没有登录之前,创建session返回浏览器时采用base64编码,

如果一台一个项目用旧版本一个项目用新版本就会出现session不能共享

需要在新的项目中设置禁止base64编码

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * spring-session新版本禁止base64编码
 */
@Configuration
public class SessionConfig {

    @Bean
    public DefaultCookieSerializer getDefaultCookieSerializer(){
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setUseBase64Encoding(false);
        return defaultCookieSerializer;
    }
}
```

