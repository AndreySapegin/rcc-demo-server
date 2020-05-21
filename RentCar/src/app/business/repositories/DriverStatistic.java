package app.business.repositories;

import app.business.entities.Driver;

public interface DriverStatistic {
	public Driver getDriver();
	public int getRentCount();
	public double getSumSale();
	public double getSumDamage();
}