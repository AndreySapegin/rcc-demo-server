package test.app;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.RentCarApp;
import app.business.dto.RccDriverDTO;
import app.business.repositories.DriverRepository;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RentCarApp.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DriverTest {

	private static final String TEST_DRIVER_ID = "000001ABC";

	@Autowired
	MockMvc http;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	DriverRepository driverRepo;
	
	@Test
	@Order(1)
	public void addDriverTest() throws IllegalStateException, UnsupportedEncodingException, JsonProcessingException, Exception {

		var driver = new RccDriverDTO(TEST_DRIVER_ID,LocalDate.of(2000, 01, 01),"aaa@com.com" , "1000000",null);
		
		var result = http.perform(post("/stf/new_drv")
				.contentType("application/json")
				.content(mapper.writeValueAsString(driver)))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();
		var driverNew = mapper.readValue(result, RccDriverDTO.class);
		assertTrue(driverNew.getIdDriver().equals(driver.getIdDriver()));
		assertTrue(driverNew.getEMail().equals(driver.getEMail()));
		assertTrue(driverNew.getPhoneNumber().equals(driver.getPhoneNumber()));
		assertTrue(driverNew.getBirthDay().isEqual(driver.getBirthDay()));
	}

	@Test
	@Order(2)
	public void getDriverTest() throws UnsupportedEncodingException, Exception {
		
		var driver = new RccDriverDTO(TEST_DRIVER_ID,LocalDate.of(2000, 01, 01),"aaa@com.com" , "1000000",null);
		String result  = http.perform(get("/stf/gt_drv_by_id")
				.param("driverId", TEST_DRIVER_ID))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		var driverNew = mapper.readValue(result, RccDriverDTO.class);
		assertTrue(driverNew.getIdDriver().equals(driver.getIdDriver()));
		assertTrue(driverNew.getEMail().equals(driver.getEMail()));
		assertTrue(driverNew.getPhoneNumber().equals(driver.getPhoneNumber()));
		assertTrue(driverNew.getBirthDay().isEqual(driver.getBirthDay()));

	}
	
	@Test
	@Order(3)
	public void updatetDriverTest() throws IllegalStateException, UnsupportedEncodingException, JsonProcessingException, Exception {

		var driver = new RccDriverDTO(TEST_DRIVER_ID,LocalDate.of(2000, 01, 01),"bbb@com.com" , "1000000",null);
		var result = http.perform(post("/stf/upd_drv")
				.contentType("application/json")
				.content(mapper.writeValueAsString(driver)))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();
		var driverNew = mapper.readValue(result, RccDriverDTO.class);
		assertTrue(driverNew.getIdDriver().equals(driver.getIdDriver()));
		assertTrue(driverNew.getEMail().equals(driver.getEMail()));		
	}
	
	@AfterAll
	public void deleteDriver() {
		driverRepo.deleteById(TEST_DRIVER_ID);
		assertFalse(driverRepo.existsById(TEST_DRIVER_ID));
	}
}
