package app.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import app.business.entities.Driver;

public interface DriverRepository extends JpaRepository<Driver, String> {

}
