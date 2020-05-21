package app.security.repositories;

import app.security.entities.Account;

public interface AccountMongoRepository extends org.springframework.data.mongodb.repository.MongoRepository<Account, String> {

}
