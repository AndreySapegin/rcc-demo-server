package test.app;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
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
import app.business.dto.RccModelDTO;
import app.business.repositories.CarRepository;
import app.business.repositories.ModelRepository;
import app.security.dto.HeaderDTO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RentCarApp.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class CarTest {

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
	
	 private static String TEST_CAR_ID = "11111111#1";
	 private static String TEST_MODEL_ID = "TestModel#1";
	 private String token;
	
	@Test
	@Order(1)
	public void addCarTest() throws JsonProcessingException, Exception {
		
		var model = new RccModelDTO(TEST_MODEL_ID,10,100,null);
		var car = new RccCarDTO(TEST_CAR_ID,false,false,model);
		
		var tokenDTOJSON = http.perform(get("/login")
				.with(httpBasic(USER_APP,USER_PSW_APP)))
		.andExpect(status().isOk())
		.andReturn().getResponse().getContentAsString();
		var  tokenDTO= mapper.readValue(tokenDTOJSON, HeaderDTO.class);
		token = "Bearer " + tokenDTO.getHeader();
		
		http.perform(post("/mng/add_cr")
				.header("Authorization", token)
				.contentType("application/json")
				.content(mapper.writeValueAsString(car)))
		.andExpect(status().isBadRequest());

		http.perform(post("/mng/add_mdl")
				.header("Authorization", token)
				.contentType("application/json")
				.content(mapper.writeValueAsString(model)))
		.andExpect(status().isOk());
		
		var result = http.perform(post("/mng/add_cr")
				.header("Authorization", token)
				.contentType("application/json")
				.content(mapper.writeValueAsString(car)))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();
		var carNew = mapper.readValue(result, RccCarDTO.class);
		assertTrue(carNew.getIdCar().equals(car.getIdCar()));
		assertTrue(carNew.isInUse() == car.isInUse());
		assertTrue(carNew.isWritenOff() == car.isWritenOff());
		assertTrue(carNew.getModel().equals(model));

	}
	
	@Test
	@Order(2)
	public void getCarTest() throws UnsupportedEncodingException, Exception {
		
		assertTrue(carRepo.existsById(TEST_CAR_ID));
		
		String result  = http.perform(get("/stf/gt_cr_by_id")
				.header("Authorization", token)
				.param("carId", TEST_CAR_ID))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		var car = mapper.readValue(result, RccCarDTO.class);
		assertTrue(car.getIdCar().equals(TEST_CAR_ID));
		assertTrue(!car.isInUse());
		assertTrue(!car.isWritenOff());
		assertTrue(car.getModel().getIdModel().equals(TEST_MODEL_ID));
		
	}
	
	@Test
	@Order(3)
	public void carClerckManagerMethodsTest() throws UnsupportedEncodingException, Exception {
		
		assertTrue(carRepo.existsById(TEST_CAR_ID));
		
		String result  = http.perform(get("/atic/mds_w_crs").header("Authorization", token))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		var modelList = mapper.readValue(result, new TypeReference<List<RccModelDTO>>(){});
		assertTrue(modelList.stream().map(RccModelDTO::getIdModel).filter(id -> id.equalsIgnoreCase(TEST_MODEL_ID)).count()==0);
		
		result  = http.perform(get("/mng/gt_all_cr").header("Authorization", token))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();
		
		var typeListCar = new TypeReference<List<RccCarDTO>>(){};

		var carList = mapper.readValue(result, typeListCar);
		assertTrue(carList.size()>0);
		assertTrue(carList.stream().map(RccCarDTO::getIdCar).filter(id -> id.equalsIgnoreCase(TEST_CAR_ID)).count()==1);

		result  = http.perform(get("/stf/gt_crs_avlb").header("Authorization", token))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		carList = mapper.readValue(result, typeListCar);
		assertTrue(carList.size()>0);
		assertTrue(carList.stream().map(RccCarDTO::getIdCar).filter(id -> id.equalsIgnoreCase(TEST_CAR_ID)).count()==1);

		result  = http.perform(get("/stf/gt_crs_avlb_by_mdlid")
				.header("Authorization", token)
				.param("modelId", TEST_MODEL_ID))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		carList = mapper.readValue(result, typeListCar);
		assertTrue(carList.size()>0);
		assertTrue(carList.stream().map(RccCarDTO::getIdCar).filter(id -> id.equalsIgnoreCase(TEST_CAR_ID)).count()==1);
		
		var carEntity = carRepo.findById(TEST_CAR_ID).get();
		carEntity.setInUse(true);
		carEntity = carRepo.save(carEntity);

		result  = http.perform(get("/stf/gt_crs_avlb").header("Authorization", token))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		carList = mapper.readValue(result, typeListCar);
		assertTrue(carList.size()>0);
		assertTrue(carList.stream().map(RccCarDTO::getIdCar).filter(id -> id.equalsIgnoreCase(TEST_CAR_ID)).count()==0);
		
		result  = http.perform(get("/mng/gt_crs_inuse").header("Authorization", token))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		carList = mapper.readValue(result, typeListCar);
		assertTrue(carList.size()>0);
		assertTrue(carList.stream().map(RccCarDTO::getIdCar).filter(id -> id.equalsIgnoreCase(TEST_CAR_ID)).count()==1);

		carEntity.setInUse(false);
		carEntity = carRepo.save(carEntity);

		var model = new RccModelDTO(TEST_MODEL_ID,10,100,null);
		var car = new RccCarDTO(TEST_CAR_ID,false,false,model);

		result  = http.perform(post("/boss/woff_cr")
				.header("Authorization", token)
				.contentType("application/json")
				.content(mapper.writeValueAsBytes(car)))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		var carNew = mapper.readValue(result, RccCarDTO.class);
		assertTrue(car.getIdCar().equals(TEST_CAR_ID));
		assertTrue(carNew.isWritenOff());

		result  = http.perform(get("/mng/gt_crs_woff").header("Authorization", token))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		carList = mapper.readValue(result, typeListCar);
		assertTrue(carList.size()>0);
		assertTrue(carList.stream().map(RccCarDTO::getIdCar).filter(id -> id.equalsIgnoreCase(TEST_CAR_ID)).count()==1);
		
		
	} 
	
	@AfterAll
	public void deleteModel() {
		carRepo.deleteById(TEST_CAR_ID);
		assertFalse(carRepo.existsById(TEST_CAR_ID));
		modelRepo.deleteById(TEST_MODEL_ID);
		assertFalse(modelRepo.existsById(TEST_MODEL_ID));
		
	}

}
