package com.developerhelperhub.ms.id.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.developerhelperhub.ms.id.entity.mongodb.InfoEntity;

@Repository
public interface InfoRepository extends MongoRepository<InfoEntity, String> {

}
