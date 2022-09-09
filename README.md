# spring-boot-starter-rpc

### 使用方式

**下载项目，打包到本地，引入项目**

```xml
<dependency>
   <groupId>com.lsstop</groupId>
   <artifactId>spring-boot-starter-rpc</artifactId>
   <version>1.0-RELEASE</version>
</dependency>
```

### 服务端

**1、定义接口**

```java
public interface Hello {

    String hello(String name);
}
```

**2、实现接口，并标注`@RpcService`注解，表明这是一个rpc服务**

```java
@RpcService
public class HelloImpl implements Hello {
    @Override
    public String hello(String name) {
        return "测试：" + name;
    }
}
```

**3、配置**

```yaml
spring:
  rpc:
    registryAddress: nacos://127:0:0:1:8848  #注册中心地址
    name: DEMO  #注册服务名称
    port: 9000  #服务端口
    registry: true #是否注册服务，默认为true
    weight: 9 #权重
```

**4、启动服务，并标注`@RpcServiceScan`扫描服务，默认为启动类包**

```java
@RpcServiceScan
@SpringBootApplication
public class BootServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootServerApplication.class, args);
    }

}
```

### 消费端

**1、定义一个和服务端一样的接口，并标注`@RpcClient`注解，注解value为服务端注册名称**

```java
@RpcClient(value = "DEMO")
public interface Hello {
    String hello(String name);
}
```

**2、yaml配置**

```yaml
spring:
  rpc:
    registryAddress: nacos://127:0:0:1:8848 #注册中心地址
    registry: false  #是否注册服务，false为消费端
```

**3、引用时标注`@RpcResource`注解，即可调用**

```java
@RestController
public class TestController {

    @RpcResource
    private Hello hello;


    @GetMapping("hello")
    public String hello(String name){
        return hello.hello(name);
    }
}
```

