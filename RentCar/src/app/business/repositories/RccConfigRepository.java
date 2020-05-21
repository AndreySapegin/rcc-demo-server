package app.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import app.business.entities.RccConfig;

public interface RccConfigRepository extends JpaRepository<RccConfig, Integer> {

}
