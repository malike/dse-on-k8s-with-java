package com.malike.dse.poc.repository;

import com.malike.dse.poc.model.Data;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface DataRepository extends CassandraRepository<Data, String> {
}
