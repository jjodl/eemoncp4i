import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.CommonClientConfigs;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

public class AsyncApi_Consume_Flight_Landing_Events_EEM {
	public static final void main(String args[]) {
		
		Properties inputs = new Properties();
		
		//String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		//String appConfigPath = rootPath + "./config.properties";
		String appConfigPath = "./config.properties";
		//String catalogConfigPath = rootPath + "catalog";

		Properties appProps = new Properties();
		
		try {
			appProps.load(new FileInputStream(appConfigPath));
			System.out.println("Config file successfully loaded.");
			
		} catch (Exception e)
		{
			System.out.println("Problem loading the configuration file.");
			System.exit(0);
		}
		
		String bootstrap_servers = appProps.getProperty("bootstrap.servers");
		System.out.println( bootstrap_servers );

		String group_id = appProps.getProperty("group.id");
		String client_id = appProps.getProperty("client.id");
		String username = appProps.getProperty("username");
		String password = appProps.getProperty("password");
		String SslConfigs_SSL_TRUSTSTORE_LOCATION_CONFIG = appProps.getProperty("SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG");
		String SslConfigs_SSL_TRUSTSTORE_PASSWORD_CONFIG = appProps.getProperty("SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG");
		String SslConfigs_SSL_TRUSTSTORE_TYPE_CONFIG = appProps.getProperty("SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG");
		String topic = appProps.getProperty("FLIGHT.LANDING.TOPIC");
		
		Properties props = new Properties();

		// props.put("bootstrap.servers", "my-egw-ibm-egw-rt-event-automation.ocp-dev-e8d7fad266f3d495445c089746d902f0-0000.us-east.containers.appdomain.cloud:443");
		props.put("bootstrap.servers", bootstrap_servers);
	    //props.put("group.id", "2");
		props.put("group.id", group_id);
	    //props.put("client.id", "40ae1649-1049-41c5-a71d-8814e38a5bfe");
	    props.put("client.id", client_id);
		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");

	    
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");


		props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
//	    props.put(SaslConfigs.SASL_JAAS_CONFIG,
//	    	      "org.apache.kafka.common.security.plain.PlainLoginModule required " 
//	    	      "username=\"62f9a33d1917ffeb271220e1f4aebbbe\" " +
//	    	      "password=\"7ef9c56330652ec22678343a96a35386\";");
	    props.put(SaslConfigs.SASL_JAAS_CONFIG,
	    	      "org.apache.kafka.common.security.plain.PlainLoginModule required " +
	    	      "username=\"" + username + "\"" +
	    	      "password=\"" + password + "\";");
	    
	    // The Kafka cluster may have encryption enabled. Contact the API owner for the appropriate TrustStore configuration.
	    props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, SslConfigs_SSL_TRUSTSTORE_LOCATION_CONFIG);
	    props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, SslConfigs_SSL_TRUSTSTORE_PASSWORD_CONFIG);
	    props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, SslConfigs_SSL_TRUSTSTORE_TYPE_CONFIG);
	    props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");

		KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<String, byte[]>(props);
		consumer.subscribe(Collections.singletonList(topic));
		//try {
			while (true) {
				ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofSeconds(10));
				for (ConsumerRecord<String, byte[]> record : records) {
					byte[] value = record.value();
					String key = record.key();
					ObjectMapper om = new ObjectMapper();
					JsonNode jsonNode;
					try {
						jsonNode = om.readTree(value);
					
					// Do something with your JSON data
						
					String flightNumber = jsonNode.get("flight").asText();
					String terminal = jsonNode.get("terminal").asText();
					String numPassengers = jsonNode.get("passengers").asText();
			
					System.out.println("DEBUG: A FLIGHT HAS LANDED!");
					System.out.println("           time landed: " + jsonNode.get("timestamp").asText());
					System.out.println("              location: " + jsonNode.get("location").asText());
					System.out.println("               airport: " + jsonNode.get("airport").asText());
					System.out.println("               airline: " + jsonNode.get("airline").asText());
					System.out.println("         flight number: " + flightNumber);
					System.out.println("              terminal: " + terminal);
					System.out.println("                  gate: " + jsonNode.get("gate").asText());
					System.out.println("  number of passengers: " + numPassengers);
					
					System.out.println("  ");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
	}
}