# RabbitMQ

[toc]

## 参考书`<<基于SpringBoot实现:Java分布式中间件开发入门与实战>>`

## 本地安装位置

`C:\Program Files\RabbitMQ Server\rabbitmq_server-3.6.10\sbin`

启动 : 双击`rabbitmq-server.bat`

## RabbitMQ简介

RabbitMQ作为一款能实现高性能存储分发消息的分布式中间件，具有异步通信、服务解耦、接口限流、消息分发和延迟处理等功能

* 异步通信

    用户注册:

    ![传统用户注册](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200719215001722.png)

    注册流程需要: 写入数据库 + 发送短信验证 /邮箱验证,如果发邮件或发短信出故障整个流程就走不下去

    ![异步发消息验证](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200719215212642.png)

* 服务解耦

* 接口限流

    * 传统抢购

        首先会校验用户和商品等信息的合法性；当校验通过之后，会判断当前商品的库存是否充足；如果充足，则代表当前用户将能成功抢购到商品，最后将用户抢购成功的相关数据记录存入数据库，并异步通知用户抢购成功、尽快进行付款等。

    ![传统用户抢购流程](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200719215338105.png)

    * 引入RabbitMq

        RabbitMQ的引入主要是从以下两个方面优化系统的整体处理流程。

        （1）接口限流：前端产生的高并发请求并不会像“无头苍蝇”一样立即到达后端系统接口，而是像每天上班时的地铁限流一样，将这些请求按照“先来后到”的顺序存入RabbitMQ的队列，即在某种程度上实现“接口限流”。

        （2）消息异步分发：当商品库存充足时，当前抢购的用户将可以抢到该商品，之后会异步地通过发送短信、发送邮件等方式通知用户抢购成功，并告知用户尽快付款，即在某种程度上实现了“消息异步分发”。

        ![引入队列流程](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200719215700814.png)

    * 

* 消息分发

    

* 延迟处理

    实现消息的延时、延迟处理,例如: 12306抢火车票(请在30分钟内付款)

    ![付款流程](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200719220018338.png)

    RabbitMQ的引入

    该优化流程中可以看出RabbitMQ的引入主要是取消了传统处理流程的“定时器处理逻辑”，取而代之的是采用RabbitMQ的延迟队列进行处理。延迟队列，顾名思义，指的是可以延迟一定的时间再处理相应的业务逻辑

    ![RabbitMQ引入](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200719220116834.png)

* 

## 组件

核心基础组件:

* 生产者：用于产生、发送消息的程序。
* 消费者：用于监听、接收、消费和处理消息的程序。
* 消息：可以看作是实际的数据，如一段文字、一张图片和一篇文章等，在RabbitMQ底层系统架构中，消息是通过二进制的数据流进行传输的。
* 队列：指的是消息的暂存区或者存储区，可以看作是一个中转站，消息经过这个“中转站”后，便将消息传输到消费者手中。
* 交换机：同样也可以看作是消息的中转站，用于首次接收和分发消息，其中包括Headers、Fanout、Direct和Topic。
* 路由：相当于密钥、地址或者“第三者”，一般不单独使用，而是与交换机绑定在一起，将消息路由分支到指定的队列。

![基本消息模型](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200720211046063.png)

## 事件驱动模型

Spring内置的基于`ApplicationEvent`和`ApplicationListener`的事件驱动模型。

Spring的事件驱动模型，顾名思义，是通过“事件驱动”的方式实现业务模块之间的交互。交互的方式有同步和异步两种。某种程度上，“事件”也可以看作是“消息”。

![事件驱动模型](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200719220408318.png)



基于SpringBoot 项目

（1）需要创建用户登录成功后的事件实体类LoginEvent，该实体类需要继承ApplicationEvent并实现序列化机制，代码如下。

```java
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * Created on 2020/7/19
 *
 * @author wang
 */
@Setter
@Getter
@ToString
public class LoginEvent extends ApplicationEvent implements Serializable {

    private String userName;
    private String loginTime;
    private String ip;

    public LoginEvent(Object source) {
        super(source);
    }

    public LoginEvent(Object source, String userName, String loginTime, String ip) {
        super(source);
        this.userName = userName;
        this.loginTime = loginTime;
        this.ip = ip;
    }
}
```

（2）开发监听消息的消费者Consumer类，该类需要实现ApplicationListener接口并绑定事件源LoginEvent，其代码如下。

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * Created on 2020/7/19
 *
 * @author wang
 */
@EnableAsync
@Component
public class Consumer implements ApplicationListener<LoginEvent> {

    public static Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Override
    @Async
    public void onApplicationEvent(LoginEvent event) {
        logger.info("Spring事件驱动模型-接收消息: {}", event);
        //TODO : 实现自身的业务逻辑,如写入数据库等
    }
}
```

（3）开发用于发送消息或产生事件的生产者Producer，其主要是通过Spring内置的ApplicationEventPublisher组件实现消息的发送。代码如下。

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created on 2020/7/19
 *
 * @author wang
 */
@Component
public class Publisher {

    public static Logger logger = LoggerFactory.getLogger(Publisher.class);

    @Autowired
    private ApplicationEventPublisher publisher;

    public void sendMsg(){
        LoginEvent loginEvent = new LoginEvent(this, "debug", LocalDateTime.now().toString(), "127.0.0.1");
        publisher.publishEvent(loginEvent);
        logger.info("Spring事件驱动模型-发送消息: {}",loginEvent);
    }
}
```

（4）创建Java单元测试方法触发消息的发送和接收，其代码如下。

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created on 2020/7/19
 *
 * @author wang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class EventTest {
    @Autowired
    private Publisher publisher;

    @Test
    public void testEvent(){
        publisher.sendMsg();
    }
}
```

(5) 结果

```java
2020-07-19 22:23:17.150  INFO 4708 --- [           main] com.springboot.rabbitmq.event.Publisher  : Spring事件驱动模型-发送消息: LoginEvent(userName=debug, loginTime=2020-07-19T22:23:17.133, ip=127.0.0.1)
2020-07-19 22:23:17.151  INFO 4708 --- [         task-1] com.springboot.rabbitmq.event.Consumer   : Spring事件驱动模型-接收消息: LoginEvent(userName=debug, loginTime=2020-07-19T22:23:17.133, ip=127.0.0.1)

```

## SpringBoot整合RabbitMQ

1. 添加依赖

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    ```

2. 添加配置

    ```properties
    server.port=8081
    spring.rabbitmq.host=localhost
    spring.rabbitmq.port=5672
    spring.rabbitmq.username=guest
    spring.rabbitmq.password=guest
    mq.env=local
    ```

3. 配置类

    ```java
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.amqp.core.AcknowledgeMode;
    import org.springframework.amqp.core.Binding;
    import org.springframework.amqp.core.BindingBuilder;
    import org.springframework.amqp.core.DirectExchange;
    import org.springframework.amqp.core.Queue;
    import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
    import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
    import org.springframework.amqp.rabbit.core.RabbitTemplate;
    import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.core.env.Environment;
    
    import java.util.Objects;
    
    /**
     * @since  2020-07-20
     * @author wang
     */
    @Configuration
    @Slf4j
    public class RabbitMqConfig {
        /**
         * rabbitMq链接工厂
         */
        @Autowired
        private CachingConnectionFactory connectionFactory;
        /**
         * 自动装配消息监听器所在的容器工厂配置类实例
         */
        @Autowired
        private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;
        /**
         * 环境变量
         */
        @Autowired
        private Environment environment;
    
        /**
         * 单一消费者实例配置
         * @return SimpleRabbitListenerContainerFactory
         */
        @Bean(name = "singleListenerContainer")
        public SimpleRabbitListenerContainerFactory singleListenerContainer(){
            //定义消息监听器所在容器工厂
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            //设置容器工厂的实例
            factory.setConnectionFactory(connectionFactory);
            //设置消息在传输中的格式,这里采用JSON格式传输
            //发送字符串的时候,不能要下面这一行,发送对象的时候需要这一行
            factory.setMessageConverter(new Jackson2JsonMessageConverter());
            //设置并发消费者实例的初始数量
            factory.setConcurrentConsumers(1);
            //设置并发消费者的最大数量
            factory.setMaxConcurrentConsumers(1);
            //设置并发消费者实例中每个实例拉取的消息数量
            factory.setPrefetchCount(1);
            return factory;
        }
        @Bean(name = "multiListenerContainer")
        public SimpleRabbitListenerContainerFactory multiListenerContainer(){
            //定义消息监听器所在容器工厂
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            //设置容器工厂的实例
            factoryConfigurer.configure(factory,connectionFactory);
            //设置消息在传输中的格式,这里采用JSON格式传输
            factory.setMessageConverter(new Jackson2JsonMessageConverter());
            //设置消息的确认模式
            factory.setAcknowledgeMode(AcknowledgeMode.NONE);
            //设置并发消费者实例的初始数量
            factory.setConcurrentConsumers(10);
            //设置并发消费者的最大数量
            factory.setMaxConcurrentConsumers(15);
            //设置并发消费者实例中每个实例拉取的消息数量
            factory.setPrefetchCount(10);
            return factory;
        }
    
        @Bean
        public RabbitTemplate rabbitTemplate(){
            //发送消息后确认`connectionFactory.setPublisherConfirms(true)`;
            connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
            //发送消息后返回确认信息
            connectionFactory.setPublisherReturns(true);
            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setMandatory(true);
            //发送成功回调方法
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info("消息发送成功: correlationData({}),ack({}),cause({})",correlationData,ack,cause));
            //发送消息后,如果发送失败,则输出消息
            rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingkry) -> log.info("消息丢失: exchange({}),route({}),replyCode({}),replyText({}),message({})",exchange,routingkry,replyCode,replyText,message));
            return rabbitTemplate;
        }
    
        /**
         * 创建简单消息模型: 队列
         * @return Queue
         */
        @Bean(name = "basicQueue")
        public Queue basicQueue(){
            return new Queue(Objects.requireNonNull(environment.getProperty("mq.basic.info.queue.name")),true);
        }
        /**
         * 创建简单消息模型: 对象队列
         * @return Queue
         */
        @Bean(name = "objectQueue")
        public Queue objectQueue(){
            return new Queue(Objects.requireNonNull(environment.getProperty("mq.object.info.queue.name")),true);
        }
        /**
         * 创建简单消息模型: 对象交换机
         * @return DirectExchange
         */
        @Bean
        public DirectExchange objectExchange(){
            return new DirectExchange(environment.getProperty("mq.object.info.exchange.name"),true,false);
        }
        /**
         * 创建简单消息模型: 基础交换机
         * @return DirectExchange
         */
        @Bean
        public DirectExchange basicExchange(){
            return new DirectExchange(environment.getProperty("mq.basic.info.exchange.name"),true,false);
        }
    
        /**
         * 创建基础绑定
         * @return Binding
         */
        @Bean
        public Binding basicBinding(){
            return BindingBuilder.bind(basicQueue())
                    .to(basicExchange())
                    .with(environment.getProperty("mq.basic.info.routing.key.name"));
        }
    
        /**
         * 创建对象绑定
         * @return Binding
         */
        @Bean
        public Binding objectBinding(){
            return BindingBuilder.bind(objectQueue())
                    .to(objectExchange())
                    .with(environment.getProperty("mq.object.info.routing.key.name"));
        }
    
    }
    ```

4. RabbitMQ发送和接收消息实战

    1. 在RabbitmqConfig类中创建队列、交换机、路由及其绑定

        在`application.properties`中添加

        ```properties
        #定义基本消息模型中队列,交换机和路由的名称	
        mq.basic.info.queue.name=${mq.env}.mq.basic.info.queue
        mq.basic.info.exchange.name=${mq.env}.mq.basic.info.exchange
        mq.basic.info.routing.key.name=${mq.env}.mq.basic.info.routing.key
        
        # 定义对象消息
        mq.object.info.queue.name=${mq.env}.mq.object.info.queue
        mq.object.info.exchange.name=${mq.env}.mq.object.info.exchange
        mq.object.info.routing.key.name=${mq.env}.mq.object.info.routing.key
        ```

        在`RabbitMqConfig`中添加

        ```java
        /**
             * 创建简单消息模型: 队列
             * @return Queue
             */
            @Bean(name = "basicQueue")
            public Queue basicQueue(){
                return new Queue(Objects.requireNonNull(environment.getProperty("mq.basic.info.queue.name")),true);
            }
            /**
             * 创建简单消息模型: 对象队列
             * @return Queue
             */
            @Bean(name = "objectQueue")
            public Queue objectQueue(){
                return new Queue(Objects.requireNonNull(environment.getProperty("mq.object.info.queue.name")),true);
            }
            /**
             * 创建简单消息模型: 对象交换机
             * @return DirectExchange
             */
            @Bean
            public DirectExchange objectExchange(){
                return new DirectExchange(environment.getProperty("mq.object.info.exchange.name"),true,false);
            }
            /**
             * 创建简单消息模型: 基础交换机
             * @return DirectExchange
             */
            @Bean
            public DirectExchange basicExchange(){
                return new DirectExchange(environment.getProperty("mq.basic.info.exchange.name"),true,false);
            }
        
            /**
             * 创建基础绑定
             * @return Binding
             */
            @Bean
            public Binding basicBinding(){
                return BindingBuilder.bind(basicQueue())
                        .to(basicExchange())
                        .with(environment.getProperty("mq.basic.info.routing.key.name"));
            }
        
            /**
             * 创建对象绑定
             * @return Binding
             */
            @Bean
            public Binding objectBinding(){
                return BindingBuilder.bind(objectQueue())
                        .to(objectExchange())
                        .with(environment.getProperty("mq.object.info.routing.key.name"));
            }
        ```

        创建Person对象

        ```java
        /**
         * @since 2020-07-20
         * @author wang
         */
        @Data
        @ToString
        public class Person implements Serializable {
        
            private Integer id;
            private String name;
            private String userName;
        
            public Person(Integer id, String name, String userName) {
                this.id = id;
                this.name = name;
                this.userName = userName;
            }
        
            public Person(){
            }
        }
        ```

    2. 创建发送消息的生产者

        ```java
        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.springboot.rabbitmq.model.Person;
        import lombok.extern.slf4j.Slf4j;
        import org.apache.logging.log4j.util.Strings;
        import org.springframework.amqp.AmqpException;
        import org.springframework.amqp.core.Message;
        import org.springframework.amqp.core.MessageBuilder;
        import org.springframework.amqp.core.MessageDeliveryMode;
        import org.springframework.amqp.core.MessagePostProcessor;
        import org.springframework.amqp.core.MessageProperties;
        import org.springframework.amqp.rabbit.core.RabbitTemplate;
        import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
        import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.core.env.Environment;
        import org.springframework.stereotype.Component;
        import org.springframework.util.StringUtils;
        
        import java.nio.charset.StandardCharsets;
        
        /**
         * @since 2020-07-20
         * @author wang
         */
        @Component
        @Slf4j
        public class BasicPublisher {
        
            @Autowired
            private ObjectMapper objectMapper;
        
            @Autowired
            private RabbitTemplate rabbitTemplate;
            @Autowired
            private Environment environment;
        
            public void sendMsg(String message){
                if (!StringUtils.isEmpty(message)){
                    try {
                        //定义消息传输格式为json字符串格式
        //                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                        //指定消息模型中的交换机
                        rabbitTemplate.setExchange(environment.getProperty("mq.basic.info.exchange.name"));
                        //执行路由
                        rabbitTemplate.setRoutingKey(environment.getProperty("mq.basic.info.routing.key.name"));
                        //将字符串转换成发送的消息
        //                Message msg = MessageBuilder.withBody(message.getBytes(StandardCharsets.UTF_8)).build();
                        //转化并发送消息
                        rabbitTemplate.convertAndSend(message);
                        log.info("基本消息模型-生产者-发送消息: {}",message);
                    }catch (Exception e){
                        log.error("基本消息模型-生产者-发送消息发生异常: {}",message,e.fillInStackTrace());
                    }
                }
            }
        
            public void sendObjMsg(Person person){
                try {
                    //指定消息模型中的交换机
                    rabbitTemplate.setExchange(environment.getProperty("mq.object.info.exchange.name"));
                    //执行路由
                    rabbitTemplate.setRoutingKey(environment.getProperty("mq.object.info.routing.key.name"));
                    rabbitTemplate.convertAndSend(person, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            //获取消息属性
                            MessageProperties messageProperties = message.getMessageProperties();
                            //设置消息持久化属性
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            //设置消息类型
                            messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,Person.class);
                            return message;
                        }
                    });
                    log.info("对象消息类型-生产者-发送对象类型的消息:{}",person);
                }catch (Exception e){
                    log.info("对象消息类型-生产者-发送对象类型的消息发生异常 :{}",person,e.fillInStackTrace());
                }
            }
        }
        ```

    3. 创建监听并接收消费处理消息的消费者实例BasicConsumer

        ```java
        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.springboot.rabbitmq.model.Person;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.amqp.rabbit.annotation.RabbitListener;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.messaging.handler.annotation.Payload;
        import org.springframework.stereotype.Component;
        
        import java.nio.charset.StandardCharsets;
        
        /**
         * @since 2020-07-20
         * @author wang
         */
        @Component
        @Slf4j
        public class BasicConsumer {
        
            @Autowired
            private ObjectMapper objectMapper;
        
            @RabbitListener(queues = "${mq.basic.info.queue.name}",containerFactory = "singleListenerContainer")
            public void consumeMsg(@Payload byte [] msg){
                String message = new String(msg, StandardCharsets.UTF_8);
                log.info("基本消息模型-消费者-监听消费到消息: {}",message);
            }
        
            @RabbitListener(queues = "${mq.object.info.queue.name}",containerFactory = "singleListenerContainer")
            public void consumeObjectMsg(@Payload Person person){
                try {
                    log.info("对象消息模型-消费者-监听消费到消息: {}",person);
                } catch (Exception e) {
                    log.info("对象消息模型-消费者-发生异常: ",e.fillInStackTrace());
                }
            }
        }
        ```

    4. 创建Java单元测试类RabbitmqTest

        ```java
        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.springboot.rabbitmq.model.Person;
        import com.springboot.rabbitmq.publisher.BasicPublisher;
        import lombok.extern.slf4j.Slf4j;
        import org.junit.jupiter.api.Test;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.context.SpringBootTest;
        
        
        @SpringBootTest
        @Slf4j
        public class RabbitMqTest {
        
            @Autowired
            private ObjectMapper objectMapper;
            /**
             * 生产者实例
             */
            @Autowired
            private BasicPublisher basicPublisher;
        
            @Test
            public void testBasicPublisher(){
                String msg = "简单消息模型";
                String json = "{\"foo\" : \"value\" }";
                basicPublisher.sendMsg(json);
            }
        
            @Test
            public void testObjectPublish(){
                Person person = new Person(1,"test-name","test-username");
                basicPublisher.sendObjMsg(person);
            }
        }
        ```

    ## 消息模型分类

    ### 基于HeadersExchange的消息模型(很少使用)

    ### 基于FanoutExchange的消息模型

    FanoutExchange是交换机的一种，具有广播消息的作用。即当消息进入这个中转站时，交换机会检查哪个队列跟自己是绑定在一起的，找到相应的队列后，将消息传输到相应的绑定队列，并最终由队列对应的消费者进行监听消费处理。

    ![FanoutExchange消息模型](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200720215516573.png)

    ### 基于DirectExchange的消息模型

    DirectExchange也是RabbitMQ的一种交换机，具有直接传输消息的作用。即当消息进入这个中转站时，交换机会检查哪个路由跟自己绑定在一起，并根据生产者发送消息指定的路由进行匹配。如果能找到对应的绑定模型，则将消息直接路由传输到指定的队列，最终由队列对应的消费者进行监听消费。

    ![DirectExchange消息模型](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200720220022849.png)

    ### 基于TopicExchange的消息模型

TopicExchange也是RabbitMQ交换机的一种，是一种“发布-主题-订阅”式的交换机，在实际生产环境中同样具有很广泛的应用。

此种消息模型同样是由交换机、路由和队列严格绑定构成。它和前面介绍的另外两种消息模型相比，最大的不同之处在于此种消息模型支持通配式的路由，即可以通过为路由的名称指定特定的通配符*和#，从而绑定到不同的队列。其中通配符*表示一个特定的单词，而通配符#则可以表示任意的单词（可以是一个，也可以是多个，也可以没有）。某种程度上通配符#表示的路由范围大于等于通配符*表示的路由范围，即前者可以包含后者。

![TopicExchange消息模型](D:\data\notes\notes\消息队列\RabbitMQ\RabbitMQ简介\image-20200720220628767.png)

## 死信队列

死信队列也是队列的一种，同样也是由基本的交换机和基本的路由“绑定”而成。只不过跟普通队列相比，它拥有“延迟、延时处理消息”的功能。而之所以死信队列具有此种功能，主要还是得归功于它的“组成成分”，即死信队列主要由3个成员组成：DLX（死信交换机）、DLK（死信路由）和TTL（存活时间）。其中死信交换机和死信路由是必需的组成成分，而存活时间是非必需的成分

DLX（死信交换机）、DLK（死信路由）和TTL（存活时间）

* DLX：即Dead-Letter-Exchange，中文名称叫“死信交换机”，是交换机的一种类型，不过是一种特殊的类型。
* DLK：即Dead-Letter-Routing-Key，中文名称叫“死信路由”，同样也是一种特殊的路由，主要是跟DLX组合在一起构成死信队列。
* TTL：即Time-To-Live，指的是进入死信队列的消息可以存活的时间，一旦达到TTL，将意味着该消息“死了”，从而进入下一个中转站，等待被真正的消息队列监听消费。

### 死信队列实战

（1）DeadInfo实体类

```java
@ToString
@Getter
@Setter
public class DeadInfo implements Serializable {

    private Integer id;
    private String msg;

}
```

（2）在RabbitmqConfig配置类中创建包含死信队列的基本消息模型和包含真正队列的真正消息模型

```java
@Configuration
@Slf4j
public class RabbitMqConfig {
    /**
     * rabbitMq链接工厂
     */
    @Autowired
    private CachingConnectionFactory connectionFactory;
    /**
     * 自动装配消息监听器所在的容器工厂配置类实例
     */
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;
    /**
     * 环境变量
     */
    @Autowired
    private Environment environment;

    /**
     * 单一消费者实例配置
     * @return SimpleRabbitListenerContainerFactory
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory singleListenerContainer(){
        //定义消息监听器所在容器工厂
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置容器工厂的实例
        factory.setConnectionFactory(connectionFactory);
        //设置消息在传输中的格式,这里采用JSON格式传输
        //发送字符串的时候,不能要下面这一行,发送对象的时候需要这一行
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置并发消费者实例的初始数量
        factory.setConcurrentConsumers(1);
        //设置并发消费者的最大数量
        factory.setMaxConcurrentConsumers(1);
        //设置并发消费者实例中每个实例拉取的消息数量
        factory.setPrefetchCount(1);
        return factory;
    }
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer(){
        //定义消息监听器所在容器工厂
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置容器工厂的实例
        factoryConfigurer.configure(factory,connectionFactory);
        //设置消息在传输中的格式,这里采用JSON格式传输
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置消息的确认模式
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        //设置并发消费者实例的初始数量
        factory.setConcurrentConsumers(10);
        //设置并发消费者的最大数量
        factory.setMaxConcurrentConsumers(15);
        //设置并发消费者实例中每个实例拉取的消息数量
        factory.setPrefetchCount(10);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(){
        //发送消息后确认`connectionFactory.setPublisherConfirms(true)`;
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        //发送消息后返回确认信息
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setMandatory(true);
        //发送成功回调方法
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info("消息发送成功: correlationData({}),ack({}),cause({})",correlationData,ack,cause));
        //发送消息后,如果发送失败,则输出消息
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingkry) -> log.info("消息丢失: exchange({}),route({}),replyCode({}),replyText({}),message({})",exchange,routingkry,replyCode,replyText,message));
        return rabbitTemplate;
    }
@Bean
    public Queue basicDeadQueue(){
        //创建死信队列的组成成分Map,用于存放组成成分的相关成员
        Map<String, Object> args = new HashMap<>();
        //创建死信交换机
        args.put("x-dead-letter-exchange", environment.getProperty("mq.dead.exchange.name"));
        //创建死信路由
        args.put("x-dead-letter-routing-key", environment.getProperty("mq.dead.routing.key.name"));
        //设置TTL,单位为ms,这里是10s
        args.put("x-message-ttl", 10000);
        //创建并返回死信队列实例
        return new Queue(environment.getProperty("mq.dead.queue.name"), true, false, false, args);
    }

    /**
     * 创建"基本消息模型"的基本交换机,面向生产者
     * @return
     */
    @Bean
    public TopicExchange basicProducerExchange(){
        return new TopicExchange(environment.getProperty("mq.producer.basic.exchange.name"), true, false);
    }

    /**
     * 创建"基本消息模型"的基本绑定(基本交换机+基本路由),面向生产者
     * @return
     */
    @Bean
    public Binding basicProducerBinding(){
        //创建并返回基本消息模型的基本绑定
        return BindingBuilder.bind(basicDeadQueue()).to(basicProducerExchange()).with(environment.getProperty("mq.producer.basic.routing.key.name"));
    }

    /**
     * 创建真正队列,面向消费者
     * @return
     */
    @Bean
    public Queue realConsumerQueue() {
        return new Queue(environment.getProperty("mq.consumer.real.queue.name"), true);
    }

    /**
     * 创建死信交换机
     * @return
     */
    @Bean
    public TopicExchange basicDeadExchange() {
        //创建并返回死信交换机实例
        return new TopicExchange(environment.getProperty("mq.dead.exchange.name"), true, false);
    }
    @Bean
    public Binding basicDeadBinding() {
        return BindingBuilder.bind(realConsumerQueue()).to(basicDeadExchange()).with(environment.getProperty("mq.dead.routing.key.name"));
    }
}
```

（3）其中，读取环境变量实例env所读取的变量是配置在配置文件application.properties中的，

```properties
mq.dead.queue.name = ${mq.env}.middleware.dead.queue
mq.dead.exchange.name=${mq.env}.middleware.dead.exchange
mq.dead.routing.key.name=${mq.env}.middleware.dead.routing.key

mq.producer.basic.exchange.name=${mq.env}.middleware.producer.basic.exchange
mq.producer.basic.routing.key.name=${mq.env}.middleware.producer.basic.routing.key
mq.consumer.real.queue.name=${mq.env}.middleware.consumer.real.queue
```

（4）开发用于生产、发送消息的生产者DeadPublisher类，主要是将实体对象充当消息发送到“基本消息模型”中

```java
@Component
public class DeadPublisher {
    public static Logger logger = LoggerFactory.getLogger(DeadPublisher.class);
    @Autowired
    private Environment environment;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public void sendMsg(DeadInfo info){
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(environment.getProperty("mq.producer.basic.exchange.name"));
        rabbitTemplate.setRoutingKey(environment.getProperty("mq.producer.basic.routing.key.name"));
        rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //设置消息属性对象
                MessageProperties messageProperties = message.getMessageProperties();
                //设置消息的持久化模式
                messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                //设置消息头,即直接指定发送的消息所属的对象类型
                messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, DeadInfo.class);
                //设置消息的TTL,当消息和队列都设置了TTL,则取较短
                messageProperties.setExpiration(String.valueOf(10000));
                return message;
            }
        });
        logger.info("死信队列实战-发送消息对象类型的消息入死信队列-内容为: {}",info);
    }
}
```

(5)创建消费者

```java
@Component
public class DeadConsumer {
    
    public static Logger logger = LoggerFactory.getLogger(DeadConsumer.class);
    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "${mq.consumer.real.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload DeadInfo info){
        logger.info("死信队列实战-监听真正队列-消息队列中的消息-监听到消息内容为: {}",info);
    }
}
```

(6)测试类

```java
@SpringBootTest
@Slf4j
public class DeadQueueTest {

    @Autowired
    private DeadPublisher deadPublisher;

    @Test
    public void testDeadMsg() throws InterruptedException {
        DeadInfo deadInfo = new DeadInfo();
        deadInfo.setId(1);
        deadInfo.setMsg("死信队列第一条消息");
        deadPublisher.sendMsg(deadInfo);
        DeadInfo deadInfo2 = new DeadInfo();
        deadInfo2.setId(2);
        deadInfo2.setMsg("死信队列第二条消息");
        deadPublisher.sendMsg(deadInfo2);
        Thread.sleep(30000);
    }

}
```

