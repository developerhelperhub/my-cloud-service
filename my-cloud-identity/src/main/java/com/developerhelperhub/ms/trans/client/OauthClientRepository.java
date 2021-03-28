package com.developerhelperhub.ms.trans.client;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthClientRepository extends MongoRepository<OauthClientEntity, String> {

}
