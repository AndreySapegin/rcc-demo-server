package test.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.RentCarApp;
import app.business.controllers.BossController;
import app.business.dto.RccConfigDTO;
import app.security.dto.HeaderDTO;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RentCarApp.class)
@AutoConfigureMockMvc
public class BossControllerTest {

	private static final String USER_APP = "admin";
	private static final String USER_PSW_APP = "12345";

	@Autowired
	private WebApplicationContext context;

	@Autowired
	BossController  controller;
	
	@Autowired 
	MockMvc http;
	
	@Autowired
	ObjectMapper mapper;

	
	@Test
	public void configTest() {
		
		var config = new RccConfigDTO(3.0, 9.0);
		controller.setConfig(config);
		var configSaved = controller.getConfig();
		assertEquals(3.0, configSaved.getDelayPenalty(),"Wroing save configuration");
		assertEquals(9.0, configSaved.getFuelPrice(),"Wroing save configuration");
	}
	
	
	@Test
	public void configHTTPRequest() throws JsonProcessingException, Exception {

//		http = MockMvcBuilders
//				.webAppContextSetup(context)
//				.apply(springSecurity()) 
//				.build();

		
		var config = new RccConfigDTO(3.5, 8.0);

		var tokenDTOJSON = http.perform(get("/login")
				.with(httpBasic(USER_APP,USER_PSW_APP)))
		.andExpect(status().isOk())
		.andReturn().getResponse().getContentAsString();
		var  tokenDTO= mapper.readValue(tokenDTOJSON, HeaderDTO.class);
		var token = "Bearer " + tokenDTO.getHeader();
		
		http.perform(post("/boss/st_cfg")
				.header("Authorization", token)
				.contentType("application/json")
				.content(mapper.writeValueAsString(config)))
		.andExpect(status().isOk());
		
		String result  = http.perform(get("/boss/gt_cfg").header("Authorization", token))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();
		
		var configSaved = mapper.readValue(result, RccConfigDTO.class);
		assertEquals(3.5, configSaved.getDelayPenalty(),"HTTP: Wroing save configuration");
		assertEquals(8.0, configSaved.getFuelPrice(),"HTTP: Wroing save configuration");
		
	}
}
