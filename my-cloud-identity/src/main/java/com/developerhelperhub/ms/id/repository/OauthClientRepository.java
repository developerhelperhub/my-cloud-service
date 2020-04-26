package com.developerhelperhub.ms.id.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.developerhelperhub.ms.id.entity.OauthClientEntity;

@Repository
public interface OauthClientRepository extends MongoRepository<OauthClientEntity, String> {

}
