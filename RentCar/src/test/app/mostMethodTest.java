package test.app;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import app.RentCarApp;
import app.business.repositories.MaxCriteria;
import app.business.repositories.RentRecordRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RentCarApp.class)
@AutoConfigureMockMvc
public class mostMethodTest {

	@Autowired
	RentRecordRepository recRepo;
	
	@Test
	@Transactional(readOnly = true)
	public void testMethors() {
		
		try(var streamModel = recRepo.findModelStatisticAndSort(Sort.by("sumSale").descending())){
			
			streamModel.forEach(o -> System.out.printf("Model %s count %d sum %f%n", o.getModel().toString(),o.getRentCount(),o.getSumSale()));
		}
		
		var list = recRepo.findMaxModelCtiteria(MaxCriteria.COUNT);
		list.forEach(o -> System.out.printf("Model %s %n", o.toString()));

		list = recRepo.findMaxModelCtiteria(MaxCriteria.SUMM);
		list.forEach(o -> System.out.printf("Model %s %n", o.toString()));
		
		
		try (var streamCar = recRepo.findCarStatisticAndSort(Sort.by("countDay").descending())){
			streamCar.forEach(o -> System.out.printf("Car %s count %d sum %f%n", o.getCar().toString(),o.getCountDay(),o.getSumSale()));
		}
		
		try (var streamDrv = recRepo.findDriverStatisticAndSort(Sort.by("sumSale").descending())){
			streamDrv.forEach(o-> System.out.printf("Car %s count %d sum %f  damage %f%n", o.getDriver().toString(),o.getRentCount(),o.getSumSale(),o.getSumDamage()));
		}
		
		System.out.println(recRepo.findSumDelayInRangeDate(LocalDate.of(2020, 05, 01), LocalDate.of(2020, 05, 31)));
	}
	
}
