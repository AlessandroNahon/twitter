package delco.twitter.scraping;

import delco.twitter.scraping.bootstrap.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;

@SpringBootApplication
@EnableConfigurationProperties(Configuration.class)
@ConfigurationPropertiesScan("delco.twitter.resources.application")
public class TwitterDemoApplication {

	public static void main(String[] args) {
		System.setProperty("javax.xml.transform.TransformerFactory","com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		SpringApplication. run(TwitterDemoApplication.class, args);
	}


}
