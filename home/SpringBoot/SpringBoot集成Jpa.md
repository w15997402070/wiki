# SpringBoot集成Jpa

[toc]



SpringBoot版本: 2.2.4

## 简单demo

在pom文件中添加依赖:

```xml
   <dependency>
     <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
   </dependency>
```

配置数据库连接

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/note?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
```

添加实体

```java
@Entity(name = "notebook")
@ToString
@Getter
@Setter
public class Note implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
}
```

添加repository

```java
public interface NoteRepository extends JpaRepository<Note,Integer> {
}
```

添加service

```java
public interface NoteService {

    Note getNote(Integer id);
}
```

service实现

```java
@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public Note getNote(Integer id) {
        return noteRepository.findById(id).get();
    }
}
```

添加Controller

```java
@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @RequestMapping("/get")
    public Note getNote(Integer id){
        return noteService.getNote(id);
    }
}
```

## 问题

在 `NoteServiceImpl` 中 `noteRepository.findById(id).get();`如果换成`noteRepository.getOne(id)`就会报下面的错

```java
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: com.springboot.jpa.model.Note$HibernateProxy$LH4iQSqZ["hibernateLazyInitializer"])
```

### 实体和数据库映射问题

```java
@Column(name = "createTime")
private Date createTime;
//上面的name设置会报错 createTime 不能大写会转换成create_time,应该全部小写
@Column(name = "createtime")
private Date createTime;
```



## JPA相关知识

### 主要类结构

![Repository继承结构](D:\data\notes\notes\springBoot\SpringBoot集成Jpa\image-20200301150900759.png)

`SimpleJpaRepository`解析

`save(S entity)`方法会先查询,再执行插入还是更新

```java
    @Transactional
	@Override
	public <S extends T> S save(S entity) {
		if (entityInformation.isNew(entity)) {
			em.persist(entity);
			return entity;
		} else {
			return em.merge(entity);
		}
	}
```

`delete(T entity)`方法也会先做查询,再执行删除	

```java
    @Override
	@Transactional
	@SuppressWarnings("unchecked")
	public void delete(T entity) {

		Assert.notNull(entity, "Entity must not be null!");

		if (entityInformation.isNew(entity)) {
			return;
		}

		Class<?> type = ProxyUtils.getUserClass(entity);

		T existing = (T) em.find(type, entityInformation.getId(entity));

		// if the entity to be deleted doesn't exist, delete is a NOOP
		if (existing == null) {
			return;
		}

		em.remove(em.contains(entity) ? entity : em.merge(entity));
	}
```

### JPA查询关键字列表

| 关键字                   | 示例                                                | JPQL表达                                     |
| ------------------------ | --------------------------------------------------- | -------------------------------------------- |
| And                      | findByNameAndAge                                    | ... where x.name = ?1 and x.age = ?2         |
| Or                       | findByNameOrAge                                     | ... where x.name = ?1 or x.age = ?2          |
| Is,Equals                | findByName,<br />findByNameIs<br />findByNameEquals | ... where x.name = ?1                        |
| Between                  | findByStartDateBetween                              | ... where x.startDate between ?1 and ?2      |
| LessThan                 | findByAgeLessThan                                   | ... where x.age < ?1                         |
| LessThanEqual            | findByAgeLessThanEqual                              | ... where x.age <= ?1                        |
| GreaterThan              | findByAgeGreaterThan                                | ... where x.age > ?1                         |
| GretaerThanEqual         | findByAgeGreaterThanEqual                           | ... where x.age >= ?1                        |
| After                    | findByStartDateAfter                                | ... where x.startDate > ?1                   |
| IsNull                   | findByAgelsNull                                     | …where x.age is null                         |
| IsNotNull，<br />NotNull | findByAge(Is)NotNull                                | ... where x.age is not null                  |
| Like                     | findByFirstnameLike                                 | …wherex.firstname like？1                    |
| NotLike                  | findByFirstnameNotLike                              | …wherex.firstname not like？1                |
| staringwith              | findByFistameStatingwith                            | …where x.firstname like？1(参数前加前缀%。） |
| Endingwith               | findByFistameEndingwith                             | …where x.firstname like？1(参数增加后后缀%） |
| Containing               | findByFirstmameContaining                           | …where x.firstname like？1（参数被%包）      |
| OrderBy                  | findByAgeOrderByLastnameDesc                        | …where x.age=？1 order by x.lastname desc    |
| Not                      | findByLastnameNot                                   | …where x.lastname>21                         |
| In                       | findByAgeln（Collection<Age>ages）                  | …where x.age in？1                           |
| Notln                    | fimdByAgeNotln（Collections<Age> ages               | ...where x.age not in ？1                    |
| True                     | findByActiveTrue(）                                 | …where x.active=true                         |
| False                    | findByActiveFalse）                                 | …where x.active=false                        |
| IgnoreCase               | findByFirstnamelgnoreCase                           | …where UPPER(x.firstame）=UPPER(?1）         |
|                          |                                                     |                                              |

除了find的前缀之外，我们查看PartTree的源码，还有如下几种前缀：

```java
private static final String QUERY PATTERN="findI readlgetIquerylstream"; 
private static final String COUNT PATTERN="count";
private static final String EXISTS PATTERN="exists";
private static final String DELETE PATTERN="deletelremove";
```

### 查询结果的处理

#### 1. 查询方法中使用 Pageable,Slice和Sort

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

Page<Note> findByTitle(String title, Pageable pageable);
Slice<Note> findByTitle(String title, Pageable pageable);
List<Note> findByTitle(String title, Sort sort);
List<Note> findByTitle(String title, Pageable pageable);
```

Slice 和 Page 的区别?

Slice的作用是，只知道是否有下一个Slice可用，不会执行count，所以当查询较大的结果集时，只知道数据是足够的就可以了，而且相关的业务场景也不用关心一共有多少页。

Page知道可用的元素和页面的总数。它通过基础框架里面触发计数查询来计算总数。由于这可能是昂贵的，具体取决于所使用的场景，说白了，当用到Pageable的时候会默认执行一条cout语句。

#### 2. 限制查询结果

```java
    Note findFirstByOrderByAgeAsc();
    Note findTopByOrderByAgeDesc();
    Page<Note> queryFirst10ByTitle(String title,Pageable pageable);
    Slice<Note> findTop3ByTitle(String title,Pageable pageable);
    List<Note> findFirst3ByTitle(String title,Pageable pageable);
```

#### 3. 流式查询结果

```java
    @Query("select n from notebook n")
    Stream<Note> findAllByCustomAndStream();

    Stream<Note> readAllByTitleNotNull();

    @Query("select n from notebook ")
    Stream<Note> streamAllPaged(Pageable pageable);
```

流的关闭问题

```java
    Stream<Note> stream = null;
        try {
            stream = noteRepository.findAllByCustomAndStream();
            stream.forEach(note -> {
                note.setType("");
            });
        } catch (Exception e){
            e.printStackTrace();
        } finally {
          if (stream != null){
              stream.close();
          } 
        }
```

#### 4. 异步查询

一般用在定时任务中

```java
    @Async
    Future<Note> findByTitle(String title);
    @Async
    CompletableFuture<Note> findOneByTitle(String title);
    @Async
    ListenableFuture<Note> findOneByContent(String content);
```

#### 5. 查询结果字段的处理

Spring JPA对Projections扩展的支持是非常好的。从字面意思上理解就是映射，指的是和DB查询结果的字段映射关系。一般情况下，返回的字段和DB查询结果的字段是一一对应的，但有的时候，我们需要返回一些指定的字段，不需要全部返回，或者只返回一些复合型的字段，还要自己写逻辑。Spring Data正是考虑到了这一点，允许对专用返回类型进行建模，以便我们有更多的选择，将部分字段显示成视图对象

例如一个实体类`Person`

```java
@Entity 
class Person{
    @Id 
    UUID id; 
    String firstname,lastname; 
    Address address; 
        @Entity 
        static class Address{
                String zipCode,city,street;
        }
} 
```

查询的接口

```java
 interface PersonRepository extends Repository<Person,UUID>{
    Collection<Person>findByLastname(String lastname);
 }
```

1. 如果只想返回`firstname`和`lastname`字段,就可以定义一个接口

   ```java
   interface NamesOnly{
       String getFirstname(); 
       String getLastname();
   }
   ```

   更改 `Repository` 接口:

   ```java
   interface PersonRepository extends Repository<Person,UUID>{
       Collection<NamesOnly>findByLastname(String lastname);
   }
   ```

   

2. 查询关联的对象

   ```java
   interface NamesOnly{
       String getFirstname(); 
       String getLastname();
       AddressSummary getAddress();
       interface AddressSummary{
           String getCity();
       }
   }
   ```

3. `@Value` 和`SPEL`

   ```java
   interface NamesOnly{
       @Value("#{target.firstname + ' ' + target.lastname}")
       String getFullName(); 
       
   }
   ```

4. 对`SPEL`更多支持

   ```java
   class Mybean(){
       String getFullName(Person person){
           //自定义运算
       }
   }
   interface Namesonly{
       @Value("#{@Mybean.getFullName(target)}")
       String getFullName();
   }
   ```

5. 通过Spel表达式取到方法里面的参数值。

   ```java
   interface NamesOnly{
       @Value("#{args[0] + ' ' + target.firstname + '!'}")
       String getSalutation(String prefix);
   }
   ```

   

6. 实体类

   ```java
   class NamesOnlyDto{
       private final String firstname，lastname；
       //注意构造方法
       NamesOnlyDto（String firstname，String lastname）{
           this.firstname=firstname；
           this.lastname=lastname；
       }
       String getFirstname（）{return this.firstname；}
       String getLastname（）{return this.lastname；}
   }
   ```

7. 支持动态projections。通过泛化，可以根据不同的业务情况返回不同的字段集合。可以对PersonRepository做一定的变化

   ```java
   interface PersonRepository extends Repository<Person，UUID>{
       Collection<T>findByLastname（String lastname，Class<T> type）；
   }
   //调用方可以通过class类型动态指定返回不同字段的结果集合
   public void someMethod（PersonRepository people）{
   //想包含全字段，直接用原始entity（Person.class）接收即可
       Collection<Person> aggregates = people.findByLastname（"Matthews"，Person.class）；
   //如果想仅仅返回名称，只需要指定Dto即可
   Collection<NamesOnlyDto> aggregates=
   people.findByLastname（"Matthews"，NamesOnlyDto.class）；
   }
   ```

   

