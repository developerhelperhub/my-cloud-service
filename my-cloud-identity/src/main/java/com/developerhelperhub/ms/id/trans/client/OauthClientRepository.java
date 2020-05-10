package com.developerhelperhub.ms.id.trans.client;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthClientRepository extends MongoRepository<OauthClientEntity, String> {

}
