# windows 系统 protobuf 生成java代码

1. 下载`protobuf`( [protoc-3.10.1-win64.zip](https://github.com/protocolbuffers/protobuf/releases/download/v3.10.1/protoc-3.10.1-win64.zip)) 地址为 https://github.com/protocolbuffers/protobuf/releases 可以自己选择其他版本.

2. 解压` protoc-3.10.1-win64.zip` 到任意文件夹,然后添加到path中

3. 编写`.ptoto`文件

   例如 : `addressbook.proto`

       ```proto
// [START declaration]
syntax = "proto3";
package tutorial;

import "google/protobuf/timestamp.proto";
// [END declaration]

// [START java_declaration]
option java_package = "com.example.tutorial";
option java_outer_classname = "AddressBookProtos";
// [END java_declaration]

// [START csharp_declaration]
option csharp_namespace = "Google.Protobuf.Examples.AddressBook";
// [END csharp_declaration]

// [START messages]
message Person {
  string name = 1;
  int32 id = 2;  // Unique ID number for this person.
  string email = 3;

  enum PhoneType {
    MOBILE = 0;
    HOME = 1;
    WORK = 2;
  }

  message PhoneNumber {
    string number = 1;
    PhoneType type = 2;
  }

  repeated PhoneNumber phones = 4;

  google.protobuf.Timestamp last_updated = 5;
}

// Our address book file is just one of these.
message AddressBook {
  repeated Person people = 1;
}
// [END messages]
       ```

4. 打开cmd,先运行` protoc --version`查看是否安装好,然后再运行命令

   ` protoc -I=D:\demo\java --java_out=D:\demo\java  D:\demo\proto\addressbook.proto  --proto_path=D:\demo\prot`

   上面的命令就是解析`D:\demo\proto\`文件夹下的`addressbook.proto`文件,然后生成java文件放到`D:\demo\java`文件夹下

   运行命令之后打开`D:\demo\java`文件下就会发现添加了一个`com`文件夹,这个文件夹就对应上面``addressbook.proto``文件中的`option java_package = "com.example.tutorial";`,`com.example.tutorial`就是生成的包名.进入这个包就可以看到生成的`AddressBookProtos.java`文件了,这个文件名字对应``addressbook.proto``中的`option java_outer_classname = "AddressBookProtos"`值.