# GRPC入门

## idea maven 根据`.proto`文件生成java代码

1. 在`pom.xml`文件中加入依赖(如果依赖下不下来可以更换版本试试,注意和插件版本对应)

```xml
   <dependency>
      <groupId>io.grpc</groupId>
      <artifactId>grpc-netty-shaded</artifactId>
      <version>1.26.0</version>
    </dependency>
    <dependency>
      <groupId>io.grpc</groupId>
      <artifactId>grpc-protobuf</artifactId>
      <version>1.26.0</version>
    </dependency>
    <dependency>
      <groupId>io.grpc</groupId>
      <artifactId>grpc-stub</artifactId>
      <version>1.26.0</version>
    </dependency>
```

2. 在`pom.xml`文件中加入插件

```xml
<build>
    <extensions>
      <extension>
        <groupId>kr.motd.maven</groupId>
        <artifactId>os-maven-plugin</artifactId>
        <version>1.5.0.Final</version>
      </extension>
    </extensions>
    <plugins>
      <plugin>
        <groupId>org.xolstice.maven.plugins</groupId>
        <artifactId>protobuf-maven-plugin</artifactId>
        <version>0.5.1</version>
        <configuration>
          <protocArtifact>com.google.protobuf:protoc:3.6.1:exe:${os.detected.classifier}</protocArtifact>
          <pluginId>grpc-java</pluginId>
          <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.14.0:exe:${os.detected.classifier}</pluginArtifact>
         <!-- java文件生成位置配置,默认在target目录里面 -->
          <outputDirectory>${basedir}/src/main/java</outputDirectory>
          <clearOutputDirectory>false</clearOutputDirectory>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>compile</goal>
              <goal>compile-custom</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```

3. 编写`.proto`文件

   在`src/main/proto`文件夹下`HelloWorld.proto`文件

   ```proto
   syntax = "proto3";
   
   option java_multiple_files = true;
   option java_package = "com.demo.grpc.generate";
   option java_outer_classname = "HelloWorldProto";
   option objc_class_prefix = "HLW";
   
   package helloworld;
   
   service Greeter {
       rpc SayHello(HelloRequest) returns (HelloResponse){}
   }
   
   message HelloRequest {
       string name = 1;
   }
   
   message HelloResponse {
       string message = 1;
   }
   ```

   就会在`com.demo.grpc.generate`包中生成java文件

4. 在`maven -> plugins -> protobuf `

   点击运行 `protobuf : compile`这个生成java文件

   `protobuf : compile-custom`生成service对应的文件

## HelloWorld服务端和客户端编写

1. `GreeterServiceImpl`编写

   ```java
   import com.demo.grpc.generate.GreeterGrpc;
   import com.demo.grpc.generate.HelloRequest;
   import com.demo.grpc.generate.HelloResponse;
   import io.grpc.stub.StreamObserver;
   
   public class GreeterServiceImpl extends GreeterGrpc.GreeterImplBase {
   
       @Override
       public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
           HelloResponse response = HelloResponse.newBuilder().setMessage("Hello " + request.getName()).build();
           responseObserver.onNext(response);
           responseObserver.onCompleted();
       }
   }
   ```

   

2. HelloWorld服务端编写

```java
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HelloWorldServer {

    private Server server;

    public  void start() throws IOException {
        int port = 8899;
        server = ServerBuilder.forPort(port)
                .addService(new GreeterServiceImpl())
                .build()
                .start();
        log.info("Server started,listening on : "+port);

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                log.info("shutting down grpc server since JVM  is shutting down");
                try {
                    HelloWorldServer.this.stop();
                }catch (Exception e){
                    e.printStackTrace();
                }
                log.info("Server shut down");
            }
        });

    }

    private void stop() throws InterruptedException {
        if (server != null){
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        HelloWorldServer helloWorldServer = new HelloWorldServer();
        helloWorldServer.start();
        helloWorldServer.blockUntilShutdown();
    }

}
```

3. HelloWorld服务端编写

   ```java
   import com.demo.grpc.generate.GreeterGrpc;
   import com.demo.grpc.generate.HelloRequest;
   import com.demo.grpc.generate.HelloResponse;
   import io.grpc.ManagedChannel;
   import io.grpc.ManagedChannelBuilder;
   import lombok.extern.slf4j.Slf4j;
   
   @Slf4j
   public class HelloWorldClient {
       public static void main(String[] args) {
           String target = "localhost:8899";
           ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                   .usePlaintext()
                   .build();
           GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);
   
           HelloRequest helloRequest = HelloRequest.newBuilder().setName("test").build();
           HelloResponse response = blockingStub.sayHello(helloRequest);
           log.info(response.getMessage());
       }
   }
   ```

   

4. 依次启动服务端和客户端

## 注意事项

1. 注意生成代码的位置
2. 注意多次生成代码时位置和更新文件的问题

