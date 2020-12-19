package com.developerhelperhub.ms.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.developerhelperhub.ms.entity.mongodb.HealthEntity;

@Repository
public interface HealthRepository extends MongoRepository<HealthEntity, String> {

}
