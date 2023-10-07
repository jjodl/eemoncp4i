EGW_BOOTSTRAP_SERVER=my-eem-gateway-ibm-egw-rt-event-automation.apps.jarjar.coc-ibm.com:443

EGW_API_CLIENTID=ad492007-b2ed-4dd3-8221-88a8f210dfce
EGW_APIKEY=64bc7ab1127b74672b285db55d5ab870
EGW_APISECRET=9f62716b30e529f52dda46fe7d1cc787

~/kafka_2.13-3.2.1/bin/kafka-console-consumer.sh --bootstrap-server $EGW_BOOTSTRAP_SERVER \
  --group "1" \
  --consumer-property "client.id=$EGW_API_CLIENTID" \
  --key-deserializer "org.apache.kafka.common.serialization.StringDeserializer" \
  --value-deserializer "org.apache.kafka.common.serialization.StringDeserializer" \
  --consumer-property "security.protocol=SASL_SSL" \
  --consumer-property "sasl.mechanism=PLAIN" \
  --consumer-property "sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$EGW_APIKEY\" password=\"$EGW_APISECRET\";" \
  --consumer-property "ssl.truststore.location=./egw-cert.p12" \
  --consumer-property "ssl.truststore.password=passw0rd" \
  --consumer-property "ssl.truststore.type=PKCS12" \
  --topic "FLIGHT.LANDINGS"
