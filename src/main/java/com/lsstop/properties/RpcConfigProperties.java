package com.lsstop.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lss
 * @date 2022/09/07
 */
@Component
@ConfigurationProperties(prefix = "spring.rpc")
public class RpcConfigProperties {

    /**
     * 注册中心地址
     */
    private String registryAddress;

    /**
     * 注册名称
     */
    private String name;

    /**
     * ip
     */
    private String host = "127.0.0.1";

    /**
     * 端口
     */
    private int port = 8080;

    /**
     * 权重
     */
    private Integer weight = 1;

    /**
     * 序列化方式
     */
    private String serializer = "kryo";

    /**
     * 负载均衡算法
     */
    private String loadbalancer = "weightRoundRobin";

    /**
     * redis密码
     */
    private String password;

    /**
     * 是否注册服务
     */
    private Boolean registry = true;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRegistry() {
        return registry;
    }

    public void setRegistry(Boolean registry) {
        this.registry = registry;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public String getLoadbalancer() {
        return loadbalancer;
    }

    public void setLoadbalancer(String loadbalancer) {
        this.loadbalancer = loadbalancer;
    }
}
