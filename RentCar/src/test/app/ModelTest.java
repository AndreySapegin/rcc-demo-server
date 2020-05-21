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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.RentCarApp;
import app.business.dto.RccModelDTO;
import app.business.repositories.ModelRepository;
import app.security.dto.HeaderDTO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RentCarApp.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class ModelTest {

	private static final String USER_APP = "admin";
	private static final String USER_PSW_APP = "12345";

	@Autowired
	MockMvc http;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	ModelRepository modelRepo;
	
	 private static String TEST_MODEL_ID = "TestModel#1";
	 private String token;
	
	@Test
	@Order(1)
	public void addModel() throws JsonProcessingException, Exception {
		var model = new RccModelDTO(TEST_MODEL_ID,10,100,null);
		
		var tokenDTOJSON = http.perform(get("/login")
				.with(httpBasic(USER_APP,USER_PSW_APP)))
		.andExpect(status().isOk())
		.andReturn().getResponse().getContentAsString();
		var  tokenDTO= mapper.readValue(tokenDTOJSON, HeaderDTO.class);
		token = "Bearer " + tokenDTO.getHeader();
		
		var result = http.perform(post("/mng/add_mdl")
				.header("Authorization", token)
				.contentType("application/json")
				.content(mapper.writeValueAsString(model)))
		.andExpect(status().isOk())
		.andReturn()
		.getRequest()
		.getContentAsString();
		var modelNew = mapper.readValue(result, RccModelDTO.class);
		assertTrue(modelNew.getIdModel().equals(model.getIdModel()));
		assertTrue(modelNew.getDailyRate() == model.getDailyRate());
		assertTrue(modelNew.getTankVolume() == model.getTankVolume());

	}
	
	@Test
	@Order(2)
	public void getModel() throws UnsupportedEncodingException, Exception {
		
		assertTrue(modelRepo.existsById(TEST_MODEL_ID));

		String result  = http.perform(get("/atic/mds_all").header("Authorization", token))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		var modelList = mapper.readValue(result, new TypeReference<List<RccModelDTO>>(){});
		assertTrue(modelList.size()>0);
		assertTrue(modelList.stream().map(RccModelDTO::getIdModel).filter(id -> id.equalsIgnoreCase(TEST_MODEL_ID)).count()>0);
		
	}
	
	@Test
	@Order(3)
	public void getModelWithoutCar() throws UnsupportedEncodingException, Exception {
		String result  = http.perform(get("/atic/mds_w_crs").header("Authorization", token))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();

		var modelList = mapper.readValue(result, new TypeReference<List<RccModelDTO>>(){});
		assertTrue(modelList.size()>0);
		assertTrue(modelList.stream().map(RccModelDTO::getIdModel).filter(id -> id.equalsIgnoreCase(TEST_MODEL_ID)).count()>0);
		
	} 
	
	@AfterAll
	public void deleteModel() {
		modelRepo.deleteById(TEST_MODEL_ID);
		assertFalse(modelRepo.existsById(TEST_MODEL_ID));
	}
	
}
