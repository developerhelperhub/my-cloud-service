package com.developerhelperhub.ms.id.monitor.application;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends MongoRepository<ApplicationEntity, String> {

}
