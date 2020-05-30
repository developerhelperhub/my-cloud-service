package com.developerhelperhub.ms.id.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.developerhelperhub.ms.id.entity.mongodb.ApplicationEntity;

@Repository
public interface ApplicationRepository extends MongoRepository<ApplicationEntity, String> {

}
