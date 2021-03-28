package com.developerhelperhub.ms.monitor.actuator.info;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoRepository extends MongoRepository<InfoEntity, String> {

}
