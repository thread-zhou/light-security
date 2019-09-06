package com.lightsecurity;


import com.lightsecurity.core.filter.GenericFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;

@SpringBootApplication
public class LightSecurityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LightSecurityDemoApplication.class, args);
    }

}
