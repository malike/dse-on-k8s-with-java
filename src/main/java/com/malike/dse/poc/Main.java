package com.malike.dse.poc;


import com.malike.dse.poc.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements ApplicationRunner {

    @Autowired
    private DataService dataService;


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        dataService.createDummy();
    }
}
