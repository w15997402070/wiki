
出现这样的问题
org.apache.catalina.util.SessionIdGeneratorBase.createSecureRandom Creation of SecureRandom instance for session ID generation using [SHA1PRNG] took [343,120] milliseconds.


http://blog.csdn.net/u011627980/article/details/54024974

http://hongjiang.info/jvm-random-and-entropy-source/


```shell
# vim /usr/local/tomcat/bin/catalina.sh
---------------------------------------------------
JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
---------------------------------------------------
``` 
