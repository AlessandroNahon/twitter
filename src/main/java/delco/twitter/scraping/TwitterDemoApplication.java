package delco.twitter.scraping;

import delco.twitter.scraping.bootstrap.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.Thread.sleep;

@SpringBootApplication
@EnableConfigurationProperties(Configuration.class)
@ConfigurationPropertiesScan("delco.twitter.resources.application")
public class TwitterDemoApplication {

	public static void main(String[] args) {
		System.setProperty("javax.xml.transform.TransformerFactory","com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

//		URI uri= null;
//		try {
//			uri = new URI("http://localhost:8083/index");
//			java.awt.Desktop.getDesktop().browse(uri);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		SpringApplication. run(TwitterDemoApplication.class, args);

	}


}
