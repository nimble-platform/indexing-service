package eu.nimble.indexing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.service.ApiInfo.DEFAULT_CONTACT;

@Configuration
@EnableSwagger2
@EnableWebMvc
public class SwaggerConfig {

    @Value("${nimble.platformHost:}")
    private String platformHost;

    public static final ApiInfo DEFAULT_API_INFO = new ApiInfoBuilder()
            .title("Nimble Indexing Service API")
            .description(
                    "API documentation for Indexing Service's APIs.")
            .version("V1.0.0")
            .contact(new Contact("Dileepa Jayakody", null, "dileepa.jayakody@salzburgresearch.at"))
            .build();

    @Bean
    public Docket api() {
        platformHost = platformHost.replace("https://", "");
        platformHost = platformHost.replace("http://","");
        platformHost = platformHost.replace("/index/indexing-service","/index");
        platformHost = platformHost.replace("/index/index","/index");


        return new Docket(DocumentationType.SWAGGER_2)
                .host(platformHost)
                .apiInfo(DEFAULT_API_INFO)
                .select()
                .apis(RequestHandlerSelectors.basePackage("eu.nimble"))
                .paths(PathSelectors.any())
                .build();
    }

}
