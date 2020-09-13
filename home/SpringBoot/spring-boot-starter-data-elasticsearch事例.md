# spring-boot-starter-data-elasticsearch示例

## 版本

* spring-boot : 2.1.10.RELEASE
* elasticsearch : 6.2.2

### 1. 引入依赖

创建springBoot项目,在pom文件中引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

### 2. 添加配置

```properties
# elasticsearch设置
spring.data.elasticsearch.cluster-name=elasticsearch
spring.data.elasticsearch.clusternodes=127.0.0.1:9300
spring.data.elasticsearch.repositories.enabled=true
```

### 3. java代码

### 实体

```java
@Document(indexName = "test",type = "search")
public class SearchModal implements Serializable {

    @Id
    private Integer id;

    private String uuid;

    private String name;

}
```

### 接口

```java
import com.test.search.model.SearchModal;
import org.springframework.data.repository.CrudRepository;

public interface SearchRepository extends CrudRepository<SearchModal,Integer> {
}

```

### service代码

```java
public interface SearchService {
    int saveAll(List<SearchModal> list);
}
```

```java
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchRepository repository;

    @Override
    public int saveAll(List<SearchModal> list) {
        List<SearchModal> searchModalList =(List<SearchModal>)repository.saveAll(list);
        return searchModalList.size();
    }
}

```

### Controller代码

```java
@RestController
@RequestMapping("/demo")
public class DemoController {
    @RequestMapping("/save")
    public String saveList(){
        List<SearchModal> searchList = getSearchList();
        searchService.saveAll(searchList);
        return "test";
    }
     private List<SearchModal> getSearchList(){
        List<SearchModal> list = new ArrayList<>();
        SearchModal search = new SearchModal();
        search.setId(1);
        search.setName("test1");
        search.setUuid(UUID.randomUUID().toString().replaceAll("-",""));
        list.add(search);
         SearchModal search2 = new SearchModal();
        search2.setId(2);
        search2.setName("test2");
        search2.setUuid(UUID.randomUUID().toString().replaceAll("-",""));
        list.add(search);
        log.info(JSON.toJSONString(list));
        return list;
    }
}
```

