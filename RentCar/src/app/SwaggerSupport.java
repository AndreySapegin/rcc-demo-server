package app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerSupport {

	@Bean
	public Docket api() {
		ParameterBuilder builder = new ParameterBuilder();
		builder.name("Authorization").modelRef(new ModelRef("string")).parameterType("header").hidden(true).defaultValue("Bearer ").required(false);
		List<Parameter> array = new ArrayList<>();
		array.add(builder.build());
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build()
				.pathMapping("")
				.globalOperationParameters(array);
	}
	
}
