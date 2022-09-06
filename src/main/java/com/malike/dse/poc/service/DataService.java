package com.malike.dse.poc.service;

import com.malike.dse.poc.model.Data;
import com.malike.dse.poc.repository.DataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DataService {

    @Autowired
    private DataRepository dataRepository;

    public void createDummy() {
        try {
            Data d = new Data();
            d.setId("123");
            d.setName("Sample Data");
            dataRepository.save(d);
            log.info("Saved successfully as connection is correct");
        } catch (Exception e) {
            log.error("error " + e.getMessage());
        }
    }

}
