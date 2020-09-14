# Thrift入门

## windows使用Thrift生成java文件

1. 下载编译器文件

   [下载地址](http://us.mirrors.quenda.co/apache/thrift/0.13.0/thrift-0.13.0.exe)

2. 将`thrift-0.13.0.exe`改名为`thrift.exe`

3. 将改名后的文件放到一个文件夹,例如`D:\test`文件夹下

4. 在命令行中进入这个路径`cd D:\test\`

   或者将`thrift.exe`加入到path中就可以在任意路径访问

5. 将`tutorial.thrift`和`shared.thrift`文件也放到文件夹`D:\test`下

6. 运行生成命令

   `D:\test > thrift -r --gen java D:\test\tutorial.thrift `

7. 如果没有报错就可以看到当前文件夹下有一个`gen-java`的文件夹,里面就是生成的文件

## 简单示例

1. 编写thrift文件`data.thrift`

   ```thrift
   namespace java com.demo
   
   typedef i16 short
   typedef i32 int
   typedef i64 long
   typedef bool boolean
   typedef string String
   
   struct Person {
       1: optional String username,
       2: optional int age,
       3: optional boolean married
   }
   
   exception DataException {
       1: optional String message,
       2: optional String callStack,
       3: optional String date
   }
   
   service PersonService {
       Person getPersonByUsername(1: required String username) throws (1: DataException dataException),
       void savePerson(1: required Person person) throws (1: DataException dataException)
   }
   ```

2. 运行命令生成java代码

   `thrift -r --gen java D:\test\data.thrift`

3. 编写`PersonService`接口实现代码`PersonServiceImpl`

   ```java
   import org.apache.thrift.TException;
   
   public class PersonServiceImpl implements PersonService.Iface {
       @Override
       public Person getPersonByUsername(String username) throws DataException, TException {
           System.out.println("接收参数 : ["+username+"]");
           Person person = new Person();
           person.setUsername("张三");
           person.setAge(1);
           person.setMarried(false);
           return person;
       }
   
       @Override
       public void savePerson(Person person) throws DataException, TException {
           System.out.println("接收保存参数 : [");
           System.out.println(person.getUsername());
           System.out.println(person.getAge());
           System.out.println(person.isMarried());
       }
   }
   ```

4. 编写服务端代码`ThriftServer`

   ```java
   import org.apache.thrift.TProcessorFactory;
   import org.apache.thrift.protocol.TCompactProtocol;
   import org.apache.thrift.server.THsHaServer;
   import org.apache.thrift.server.TServer;
   import org.apache.thrift.server.TSimpleServer;
   import org.apache.thrift.transport.TFramedTransport;
   import org.apache.thrift.transport.TNonblockingServerSocket;
   import org.apache.thrift.transport.TServerSocket;
   import org.apache.thrift.transport.TServerTransport;
   import org.apache.thrift.transport.TTransportException;
   
   public class ThriftServer {
   
       public static void main(String[] args) {
           try {
               TNonblockingServerSocket socket = new TNonblockingServerSocket(8899);
               THsHaServer.Args args1 = new THsHaServer.Args(socket).minWorkerThreads(2).maxWorkerThreads(5);
               PersonService.Processor<PersonServiceImpl> processor = new PersonService.Processor<>(new PersonServiceImpl());
   
               args1.protocolFactory(new TCompactProtocol.Factory())  //协议层
                    .transportFactory(new TFramedTransport.Factory()) //传输层
                    .processorFactory(new TProcessorFactory(processor));
               //THsHaServer -> 半同步半异步
               THsHaServer server = new THsHaServer(args1);
               System.out.println("Thrift Server Started!");
               server.serve();
           } catch (TTransportException e) {
               e.printStackTrace();
           }
       }
   }
   ```

5. 编写客户端代码`ThriftClient`

   ```java
   import org.apache.thrift.TException;
   import org.apache.thrift.protocol.TCompactProtocol;
   import org.apache.thrift.transport.TFramedTransport;
   import org.apache.thrift.transport.TSocket;
   import org.apache.thrift.transport.TTransportException;
   
   public class ThriftClient {
       public static void main(String[] args) {
   
           TFramedTransport transport = new TFramedTransport(new TSocket("localhost", 8899), 1000);
           TCompactProtocol protocol = new TCompactProtocol(transport);
           PersonService.Client client = new PersonService.Client(protocol);
   
           try {
               transport.open();
   
               Person person = client.getPersonByUsername("张三");
               System.out.println(person.getUsername());
               System.out.println(person.getAge());
               System.out.println(person.isMarried());
   
               System.out.println("====================");
               Person person2 = new Person();
               person2.setUsername("张三");
               person2.setAge(1);
               person2.setMarried(false);
   
               client.savePerson(person2);
               System.out.println("Client finished!");
           } catch (TTransportException e) {
               e.printStackTrace();
           } catch (DataException e) {
               e.printStackTrace();
           } catch (TException e) {
               e.printStackTrace();
           }finally {
               transport.close();
           }
       }
   }
   ```

6. 运行服务端,再运行客户端,就会打印出信息.

### Thrift相关知识点

### 支持的容器类型:

* list:有序列表
* set:无序不可重复集合
* map: key-value类型,相当于java中的hashmap

### Thrift 传输格式:

* TBinaryPrortocol : 二进制格式
* TCompactProtocol: 压缩格式
* TJSONProtocol: JSON格式
* TSimpleJSONProtocol:提供JSON只写协议,生成的文件很容易通过脚本语言解析
* TDebugProtocol: 使用易懂的可读文本格式,以便于Debug

### Thrift数据传输方式

* TSocket: 阻塞式socket
* TFramedTransport: 以Frame为单位进行传输,非阻塞式服务中使用
* TFileTransport: 以文件形式进行传输
* TMemoryTransport:将内存用于I/O,Java实现时内部实际使用了简单的ByteArrayOutputStream
* TZlibTransport: 使用zlib进行压缩,与其他传输方式联合使用,当前无java实现

### Thrift支持的服务模型

* TServer : 简单的单线程服务模型,常用于测试
* TThreadPoolserver: 多线程服务模型,使用标准的阻塞式I/O
* TNonblockingServer : 多线程服务模型,使用井阻塞式l/O(需使用TFramedTranspor数据传输方式
* THsHaServer-THsHa : 引人了线程池去处理,其模型把读写任务放到线程池去处理;Half-sync/Half- async的处理模式, Half-aysnc是在处理I/O事件上( accept/read/ Write Io,Half-sync用于 handler对rpc的同步处理

