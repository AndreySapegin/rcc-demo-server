package app.business.repositories;

import app.business.entities.Model;

public interface ModelStatistic {
	public Model getModel();
	public Integer getRentCount();
	public Double getSumSale();
}