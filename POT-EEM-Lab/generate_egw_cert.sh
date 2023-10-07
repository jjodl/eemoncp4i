

# generating egw-cert.jks on the kafka pods.

oc project event-automation

EGW_INSTANCE=my-eem-gateway
export EGW_BOOTSTRAP=`oc get routes | grep ${EGW_INSTANCE}-ibm-egw-rt | awk '{print$2}'`

echo $EGW_BOOTSTRAP

echo -n | openssl s_client -connect $EGW_BOOTSTRAP:443 -servername $EGW_BOOTSTRAP -showcerts | openssl x509 > egw-cert.pem

oc project cp4i-eventstreams

oc exec -it es-demo-kafka-2 -- /bin/bash -c "cd /tmp && rm egw-cert.*"

# copy egw-cert.pemto kafka pod
oc cp egw-cert.pem es-demo-kafka-2:/tmp

oc exec -it es-demo-kafka-2 -- /bin/bash -c "cd /tmp && keytool -import -noprompt \
        -alias egwcertca \
        -file egw-cert.pem \
        -keystore egw-cert.p12 -storepass passw0rd"

oc exec -it es-demo-kafka-2 -- /bin/bash -c "cd /tmp && keytool -importkeystore -srckeystore egw-cert.p12 \
        -srcstoretype PKCS12 \
        -destkeystore egw-cert.jks \
        -deststoretype JKS \
        -srcstorepass passw0rd \
        -deststorepass passw0rd \
        -noprompt"

oc cp es-demo-kafka-2:/tmp/egw-cert.p12 ./egw-cert.p12
oc cp es-demo-kafka-2:/tmp/egw-cert.jks ./egw-cert.jks

exit 0



# generating egw-cert.jks on the kafka pods.

oc project event-automation

EGW_INSTANCE=my-eem-gateway
export EGW_BOOTSTRAP=`oc get routes | grep ${EGW_INSTANCE}-ibm-egw-rt | awk '{print$2}'`

echo -n | openssl s_client -connect $EGW_BOOTSTRAP:443 -servername $EGW_BOOTSTRAP -showcerts | openssl x509 > egw-cert.pem

openssl x509 -outform der -in egw-cert.pem -out egw-cert.der

keytool -import -noprompt -alias ca -file egw-cert.der -keystore egw-cert.p12 -storepass passw0rd

keytool -importkeystore -srckeystore egw-cert.p12 \
        -srcstoretype PKCS12 \
        -destkeystore egw-cert.jks \
        -deststoretype JKS \
        -srcstorepass passw0rd \
        -deststorepass passw0rd \
        -noprompt

exit 0
