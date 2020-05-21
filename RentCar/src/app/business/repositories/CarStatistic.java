package app.business.repositories;

import app.business.entities.Car;

public interface CarStatistic {
	public Car getCar();
	public Integer getCountDay();
	public Double getSumSale();
}