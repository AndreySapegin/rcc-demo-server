package test.app;


import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.RentCarApp;
import app.business.dto.RccCarDTO;
import app.business.dto.RccDriverDTO;
import app.business.dto.RccModelDTO;
import app.business.dto.RccRentRecordDTO;
import app.business.repositories.CarRepository;
import app.business.repositories.DriverRepository;
import app.business.repositories.ModelRepository;
import app.business.repositories.RentRecordRepository;
import app.config.DatabaseProperty;
import app.security.dto.HeaderDTO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RentCarApp.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class RecordTest {

	private static final String USER_APP = "admin";
	private static final String USER_PSW_APP = "12345";

		@Autowired
		MockMvc http;
		
		@Autowired
		ObjectMapper mapper;
		
		@Autowired
		CarRepository carRepo;
		@Autowired
		ModelRepository modelRepo;
		@Autowired
		RentRecordRepository recordRepo;
		@Autowired
		DriverRepository driverRepo;
		@Autowired
		DatabaseProperty config; 
		
		private static final String TEST_CAR_ID = "11111111#2";
		private static final String TEST_MODEL_ID = "TestModel#2";
		private static final String TEST_DRIVER_ID = "000002ABC";
		private int idRecord;
		private int idRecord1;
		private int idRecord2;
		private String token;
		
		
		@Test
//		@Disabled
		@Order(2)
		public void addRecord() throws JsonProcessingException, Exception {
			
			var model = new RccModelDTO(TEST_MODEL_ID,10,100,null);
			var car = new RccCarDTO(TEST_CAR_ID,false,false,model);
			var driver = new RccDriverDTO(TEST_DRIVER_ID,LocalDate.of(2000, 01, 01),"11-111-11-11" , "aaa@com.com",null);
			var dateNow = LocalDate.of(2020, 05, 12);
			
			var record = new RccRentRecordDTO(0, driver, car, dateNow,1,null,null,100.,0.,0.,false,false);

			var tokenDTOJSON = http.perform(get("/login")
					.with(httpBasic(USER_APP,USER_PSW_APP)))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
			var  tokenDTO= mapper.readValue(tokenDTOJSON, HeaderDTO.class);
			token = "Bearer " + tokenDTO.getHeader();

			http.perform(post("/mng/add_mdl")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(model)))
			.andExpect(status().isOk());
			
			http.perform(post("/mng/add_cr")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(car)))
			.andExpect(status().isOk());
			
			http.perform(post("/stf/new_drv")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(driver)))
			.andExpect(status().isOk());
			
			
			var result = http.perform(post("/stf/new_rnt")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			var recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()>0);
			idRecord = recordNew.getIdRecord();
			assertTrue(recordNew.getDriver().getIdDriver().equals(TEST_DRIVER_ID));
			assertTrue(recordNew.getCar().getIdCar().equals(TEST_CAR_ID));
			assertTrue(recordNew.getRentDate().equals(dateNow));
			assertTrue(recordNew.getRentDurationDays()==1);
			assertNull(recordNew.getReturnDate());
			assertTrue(recordNew.getFuelInTank()==100.);
			assertTrue(recordNew.getDamageRepairPrice()==-1.);
			assertTrue(recordNew.getTotal()==-1.);
			assertTrue(!recordNew.isReceived());
			assertTrue(!recordNew.isRecordClosed());
		}
		
		@Test
//		@Disabled
		@Order(3)
		public void businessProcessTest() throws IllegalStateException, UnsupportedEncodingException, JsonProcessingException, Exception {
			// full tank, no damage, return in time
			
			var recordEmpty = new RccRentRecordDTO();
			recordEmpty.setIdRecord(idRecord);
	
			var result = http.perform(get("/mng/gt_rcd_unrec").header("Authorization", token))
					.andExpect(status().isOk())
					.andReturn()
					.getResponse()
					.getContentAsString();

			var typeList = new TypeReference<List<RccRentRecordDTO>>(){};
			var listRecord = mapper.readValue(result, typeList);
			assertTrue(listRecord.size()>0);
			assertTrue(listRecord.stream().map(RccRentRecordDTO::getIdRecord).filter(i -> i==idRecord).count()==1);
			RccRentRecordDTO record = listRecord.stream().filter(i -> i.getIdRecord() == idRecord).findFirst().get();

			result = http.perform(get("/stf/gt_rcd_drvid")
					.header("Authorization", token)
					.param("driverId", TEST_DRIVER_ID)
					.param("onDate","2020-05-14"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			
			listRecord = mapper.readValue(result, typeList);
			assertTrue(listRecord.size()>0);
			assertTrue(listRecord.stream().map(RccRentRecordDTO::getDriver).filter(i -> i.getIdDriver().equals(TEST_DRIVER_ID)).count()==1);
			
			result = http.perform(post("/stf/st_fulltank")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			var recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord);
			assertTrue(recordNew.getFuelInTank() == 100.);
			
			result = http.perform(post("/stf/st_no_damage")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord);
			assertTrue(recordNew.getDamageRepairPrice() == 0.);	
			
			record.setReturnDate(LocalDate.of(2020, 05, 13));
			result = http.perform(post("/stf/rcv_car")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord);
			assertTrue(recordNew.getTotal() == 10.);	
			assertTrue(!recordNew.getCar().isInUse());
			
			result = http.perform(post("/stf/gt_total")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			var total = mapper.readValue(result, Double.class);
			assertTrue(total == 10.);
			
			result = http.perform(get("/mng/gt_rcd_uncls").header("Authorization", token))
					.andExpect(status().isOk())
					.andReturn()
					.getResponse()
					.getContentAsString();
			
			listRecord = mapper.readValue(result, typeList);
			assertTrue(listRecord.size()>0);
			assertTrue(listRecord.stream().map(RccRentRecordDTO::getIdRecord).filter(i -> i==idRecord).count()==1);
			
			result = http.perform(get("/mng/cls_rcd")
					.header("Authorization", token)
					.param("recordId", String.valueOf(idRecord))
					.param("total", total.toString()))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord);
			assertTrue(recordNew.isRecordClosed());	
			
			
		}
		
		@Test
		@Order(4)
		public void businessProcessTest1() throws JsonProcessingException, Exception {
			
			var dateNow = LocalDate.of(2020, 05, 12);
			var driver = new RccDriverDTO();
			driver.setIdDriver(TEST_DRIVER_ID);
			var car = new RccCarDTO();
			car.setIdCar(TEST_CAR_ID);
			
			var record = new RccRentRecordDTO(0, driver, car, dateNow,1,null,null,100.,0.,0.,false,false);
			
			http.perform(post("/stf/new_rnt")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isBadRequest());

			dateNow = LocalDate.of(2020, 05, 01);
			record.setRentDate(dateNow);

			var result = http.perform(post("/stf/new_rnt")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			var recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()>0);
			idRecord1 = recordNew.getIdRecord();

			// all penalty without damage
			
			record.setIdRecord(idRecord1);
			
			result = http.perform(post("/stf/st_fuel")
					.header("Authorization", token)
					.param("volume", String.valueOf(50.))
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord1);
			assertTrue(recordNew.getFuelInTank() == 50.);
			
			result = http.perform(post("/stf/st_no_damage")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord1);
			assertTrue(recordNew.getDamageRepairPrice() == 0.);	
			
			record.setReturnDate(LocalDate.of(2020, 05, 03));
			result = http.perform(post("/stf/rcv_car")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord1);
			var total = recordNew.getCar().getModel().getDailyRate() *(1 + config.getDelayPenalty()) + 0.5 * recordNew.getCar().getModel().getTankVolume()*config.getFuelPrice();
			assertTrue(recordNew.getTotal() == total);	
			assertTrue(!recordNew.getCar().isInUse());
			
			result = http.perform(post("/stf/gt_total")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			assertTrue(mapper.readValue(result, Double.class) == total);
			
			result = http.perform(get("/mng/cls_rcd")
					.header("Authorization", token)
					.param("recordId", String.valueOf(idRecord1))
					.param("total", String.valueOf(total)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord1);
			assertTrue(recordNew.isRecordClosed());	
			
		}
		
		@Test
		@Order(5)
		public void businessProcessTest2() throws JsonProcessingException, Exception {
			
			var dateNow = LocalDate.of(2020, 05, 04);
			var driver = new RccDriverDTO();
			driver.setIdDriver(TEST_DRIVER_ID);
			var car = new RccCarDTO();
			car.setIdCar(TEST_CAR_ID);
			
			var record = new RccRentRecordDTO(0, driver, car, dateNow,1,null,null,100.,0.,0.,false,false);
			
			var result = http.perform(post("/stf/new_rnt")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			var recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()>0);
			idRecord2 = recordNew.getIdRecord();

			// all penalty with damage
			
			record.setIdRecord(idRecord2);
			
			http.perform(post("/stf/st_fuel")
					.header("Authorization", token)
					.param("volume", String.valueOf(50.))
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk());
			
			record.setReturnDate(LocalDate.of(2020, 05, 06));
			result = http.perform(post("/stf/rcv_car")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord2);
			var total = recordNew.getCar().getModel().getDailyRate() *(1 + config.getDelayPenalty()) + 0.5 * recordNew.getCar().getModel().getTankVolume()*config.getFuelPrice();
			assertTrue(recordNew.getTotal() == total);
			assertTrue(recordNew.getCar().isInUse());

			result = http.perform(post("/stf/gt_total")
					.header("Authorization", token)
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			assertTrue(mapper.readValue(result, Double.class) == total);
			
			record.setEndRepairDate(LocalDate.of(2020, 05, 10));

			result = http.perform(post("/mng/st_dmg_cost")
					.header("Authorization", token)
					.param("cost", String.valueOf(1000.))
					.contentType("application/json")
					.content(mapper.writeValueAsString(record)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord2);
			assertTrue(recordNew.getTotal() == total+1000.);
			assertTrue(!recordNew.getCar().isInUse());
			
			result = http.perform(get("/mng/cls_rcd")
					.header("Authorization", token)
					.param("recordId", String.valueOf(idRecord2))
					.param("total", String.valueOf(total+1000)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
			recordNew = mapper.readValue(result, RccRentRecordDTO.class);
			assertTrue(recordNew.getIdRecord()==idRecord2);
			assertTrue(recordNew.isRecordClosed());	
			
		}

		@Test
		@Order(6)
		public void testModelAnalitic() throws JsonProcessingException, Exception {
			

			var typeList = new TypeReference<List<RccRentRecordDTO>>(){};

			var result = http.perform(get("/stf/gt_rds_drvid_inrnge")
					.header("Authorization", token)
					.param("driverId", TEST_DRIVER_ID)
					.param("begin","2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			
			var listRecord = mapper.readValue(result, typeList);
			assertTrue(listRecord.size()>0);
			assertTrue(listRecord.stream().map(RccRentRecordDTO::getDriver).filter(i -> i.getIdDriver().equals(TEST_DRIVER_ID)).count()==3);

			result = http.perform(get("/atic/rds_range")
					.header("Authorization", token)
					.param("begin","2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			
			listRecord = mapper.readValue(result, typeList);
			assertTrue(listRecord.size()>0);
			assertTrue(listRecord.stream().map(RccRentRecordDTO::getDriver).filter(i -> i.getIdDriver().equals(TEST_DRIVER_ID)).count()==3);

			result = http.perform(get("/atic/rds_dly_range")
					.header("Authorization", token)
					.param("begin","2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			
			listRecord = mapper.readValue(result, typeList);
			assertTrue(listRecord.size()>0);
			assertTrue(listRecord.stream().map(RccRentRecordDTO::getDriver).filter(i -> i.getIdDriver().equals(TEST_DRIVER_ID)).count()==2);

			result = http.perform(get("/atic/rds_md_range")
					.header("Authorization", token)
					.param("modelId", TEST_MODEL_ID)
					.param("begin","2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			
			listRecord = mapper.readValue(result, typeList);
			assertTrue(listRecord.size()>0);
			assertTrue(listRecord.stream().map(RccRentRecordDTO::getCar).filter(i -> i.getModel().getIdModel().equals(TEST_MODEL_ID)).count()==3);
			var total = listRecord.stream().filter(i -> i.getCar().getModel().getIdModel().equals(TEST_MODEL_ID)).map(RccRentRecordDTO::getTotal).reduce(0., (a,c)-> a+c);
			var fine = config.getDelayPenalty();
			var income = listRecord.stream().filter(i -> i.getCar().getModel().getIdModel().equals(TEST_MODEL_ID)).reduce(0., 
					 (sum, rec) -> sum + (rec.getRentDurationDays() + (ChronoUnit.DAYS.between(rec.getRentDate(), rec.getReturnDate())-rec.getRentDurationDays())*fine)*rec.getCar().getModel().getDailyRate()
					 ,Double::sum);
			
			result = http.perform(get("/atic/ttl_md_range")
					.header("Authorization", token)
					.param("modelId", TEST_MODEL_ID)
					.param("begin","2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			assertEquals(mapper.readValue(result, Double.class), total);
			
			result = http.perform(get("/atic/inc_md_range")
					.header("Authorization", token)
					.param("modelId", TEST_MODEL_ID)
					.param("begin","2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			assertEquals(mapper.readValue(result, Double.class),income);
		}
		
		@Test
		@Order(7)
		public void testMostMethods() throws UnsupportedEncodingException, Exception {
			
			var result = http.perform(get("/atic/most_usd_mds").header("Authorization", token))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			var typeListModel = new TypeReference<List<RccModelDTO>>() {};
			var listModel = mapper.readValue(result, typeListModel);
			assertTrue(listModel.size()>=1);
			listModel.forEach(System.out::println);
			// for empty database
			//assertEquals(listModel.get(0).getIdModel(), TEST_MODEL_ID);

			result = http.perform(get("/atic/most_sal_mds").header("Authorization", token))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			
			listModel = mapper.readValue(result, typeListModel);
			assertTrue(listModel.size()>=1);
			listModel.forEach(System.out::println);
			//for empty database
			//assertEquals(listModel.get(0).getIdModel(), TEST_MODEL_ID);

			result = http.perform(get("/atic/most_usd_cr").header("Authorization", token))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			var typeListCar= new TypeReference<List<RccCarDTO>>() {};
			var listCar = mapper.readValue(result, typeListCar);
			assertTrue(listCar.size()>=1);
			listCar.forEach(System.out::println);
			// for empty database
			//assertEquals(listCar.get(0).getIdCar(), TEST_CAR_ID);

			result = http.perform(get("/atic/most_sal_cr").header("Authorization", token))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			
			listCar = mapper.readValue(result, typeListCar);
			assertTrue(listCar.size()>=1);
			listCar.forEach(System.out::println);
			//for empty database
			//assertEquals(listCar.get(0).getIdCar(), TEST_CAR_ID);

			result = http.perform(get("/atic/most_pay_drv").header("Authorization", token))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			var typeListDrv= new TypeReference<List<RccDriverDTO>>() {};
			var listDrv = mapper.readValue(result, typeListDrv);
			assertTrue(listDrv.size()>=1);
			listDrv.forEach(System.out::println);
			// for empty database
			//assertEquals(listDrv.get(0).getIdDriver(), TEST_DRIVER_ID);

			result = http.perform(get("/atic/most_bad_drv").header("Authorization", token))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			
			listDrv = mapper.readValue(result, typeListDrv);
			
			assertTrue(listDrv.size()>=1);
			listDrv.forEach(System.out::println);
			//for empty database
			//assertEquals(listDrv.get(0).getIdDriver(), TEST_DRIVER_ID);			
		}
		
		@Test
		@Order(8)
		public void testCarAndCommonAnalistic() throws UnsupportedEncodingException, Exception {

			var result = http.perform(get("/atic/rds_cr_range")
					.header("Authorization", token)
					.param("carId", TEST_CAR_ID)
					.param("begin", "2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			var typeListRecord = new TypeReference<List<RccRentRecordDTO>>() {};
			var listRecords = mapper.readValue(result, typeListRecord);
			assertEquals(listRecords.size(),3);
			var total = listRecords.stream().map(RccRentRecordDTO::getTotal).reduce(0., (a,s) -> a+s);
			var fine = config.getDelayPenalty();
			var income = listRecords.stream().reduce(0., 
					 (sum, rec) -> sum + (rec.getRentDurationDays() + (ChronoUnit.DAYS.between(rec.getRentDate(), rec.getReturnDate())-rec.getRentDurationDays())*fine)*rec.getCar().getModel().getDailyRate()
					 ,Double::sum);
			

			result = http.perform(get("/atic/ttl_cr_range")
					.header("Authorization", token)
					.param("carId", TEST_CAR_ID)
					.param("begin", "2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			var totalInDB = mapper.readValue(result, Double.class);
			assertEquals(totalInDB,total);

			result = http.perform(get("/atic/inc_cr_range")
						.header("Authorization", token)
						.param("carId", TEST_CAR_ID)
						.param("begin", "2020-05-01")
						.param("end", "2020-05-31"))
						.andExpect(status().isOk())
						.andReturn() 
						.getResponse()
						.getContentAsString();
			var incomeInDB = mapper.readValue(result, Double.class);
			assertEquals(incomeInDB,income);
			
			//rds_dmg_range
			result = http.perform(get("/atic/rds_dmg_range")
					.header("Authorization", token)
					.param("carId", TEST_CAR_ID)
					.param("begin", "2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();

			listRecords = mapper.readValue(result, typeListRecord);
			assertEquals(listRecords.size(),1);
			assertEquals(listRecords.get(0).getCar().getIdCar(), TEST_CAR_ID);

			result = http.perform(get("/atic/rds_range")
					.header("Authorization", token)
					.param("begin", "2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();

			listRecords = mapper.readValue(result, typeListRecord);
			assertTrue(listRecords.size()>0);
			total = listRecords.stream().map(RccRentRecordDTO::getTotal).reduce(0., (a,s) -> a+s);
			income = listRecords.stream().reduce(0., 
					 (sum, rec) -> sum + (rec.getRentDurationDays() + (ChronoUnit.DAYS.between(rec.getRentDate(), rec.getReturnDate())-rec.getRentDurationDays())*fine)*rec.getCar().getModel().getDailyRate()
					 ,Double::sum);
			var fuelPrice = config.getFuelPrice();

			var ff_ = listRecords.stream().reduce(0., 
					 (sum, rec) -> sum + (1 - rec.getFuelInTank()/100)*rec.getCar().getModel().getTankVolume()*fuelPrice
					 ,Double::sum);
			
			var planIncome = listRecords.stream().reduce(0., 
					 (sum, rec) -> sum + rec.getRentDurationDays()*rec.getCar().getModel().getDailyRate()
					 ,Double::sum);
			
			var delay_ = listRecords.stream().reduce(0., 
					 (sum, rec) -> sum + (ChronoUnit.DAYS.between(rec.getRentDate(), rec.getReturnDate())-rec.getRentDurationDays())*fine*rec.getCar().getModel().getDailyRate()
					 ,Double::sum);

			result = http.perform(get("/atic/ttl_range")
					.header("Authorization", token)
					.param("begin", "2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			totalInDB = mapper.readValue(result, Double.class);
			assertEquals(totalInDB,total);

			result = http.perform(get("/atic/inc_range")
					.header("Authorization", token)
					.param("begin", "2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			incomeInDB = mapper.readValue(result, Double.class);
			assertEquals(incomeInDB,planIncome);

			result = http.perform(get("/atic/dly_range")
					.header("Authorization", token)
					.param("begin", "2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			var delay = mapper.readValue(result, Double.class);
			assertEquals(income - planIncome, delay);

			result = http.perform(get("/atic/ff_range")
					.header("Authorization", token)
					.param("begin", "2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			var ff = mapper.readValue(result, Double.class);


			result = http.perform(get("/atic/dmg_range")
					.header("Authorization", token)
					.param("begin", "2020-05-01")
					.param("end", "2020-05-31"))
					.andExpect(status().isOk())
					.andReturn() 
					.getResponse()
					.getContentAsString();
			var dmg = mapper.readValue(result, Double.class);
			System.out.printf(" ---------------- > %f=%f %f=%f (%f = %f, %f = %f, %f)",total, totalInDB, planIncome, incomeInDB, delay ,delay_ , ff , ff_, dmg);
			assertEquals(total,incomeInDB + delay + ff + dmg);
			
		}
		
		@AfterAll
		public void deleteModel() {
			recordRepo.deleteById(idRecord2);
			assertFalse(recordRepo.existsById(idRecord2));
			recordRepo.deleteById(idRecord1);
			assertFalse(recordRepo.existsById(idRecord1));
			recordRepo.deleteById(idRecord);
			assertFalse(recordRepo.existsById(idRecord));
			carRepo.deleteById(TEST_CAR_ID);
			assertFalse(carRepo.existsById(TEST_CAR_ID));
			modelRepo.deleteById(TEST_MODEL_ID);
			assertFalse(modelRepo.existsById(TEST_MODEL_ID));
			driverRepo.deleteById(TEST_DRIVER_ID);
			assertFalse(driverRepo.existsById(TEST_DRIVER_ID));
		}
}
