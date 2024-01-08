package com.innitiatechlab.utils.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ServiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);
    private final String port;
    private String serviceAddress= null;

    @Autowired
    public ServiceUtils(@Value("${server.port}") String port) {
        this.port = port;
    }

    public String getServiceAddress(){
        if(serviceAddress == null){
            serviceAddress = findMyHostName().concat("/").concat(findMyIpAddress()).concat(":").concat(port);
        }
        return serviceAddress;
    }

    private String findMyIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "unknown IP address";
        }
    }

    private String findMyHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown host name";
        }
    }

}
