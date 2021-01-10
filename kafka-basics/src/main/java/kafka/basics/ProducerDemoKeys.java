package kafka.basics;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemoKeys {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        final Logger log = LoggerFactory.getLogger(ProducerDemoKeys.class);

        String bootstrapServers = "127.0.0.1:9092";

        //create producer properties
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 10; i++ ) {
            // create a producer record

            String topic = "first_topic";
            String value = "hello world " + i;
            String key = "id_" + i;
            //create a producer record
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            log.info("Key: " + key); // log the key
            //The key to understand is that the keys always got to the same partition for a fixed number of partitions.

            //send data - asynchronous
            producer.send(record, (recordMetadata, e) -> {
                //executes everytime a record is successfully sent or an exception is thrown
                if (e == null) {
                    // the record was successfully sent
                    log.info("Received new metadata. \n" +
                            "Topic:" + recordMetadata.topic() + "\n" +
                            "Partition: " + recordMetadata.partition() + "\n" +
                            "Offset: " + recordMetadata.offset() + "\n" +
                            "Timestamp: " + recordMetadata.timestamp());
                } else {
                    log.error("Error while producing", e);
                }
            }).get(); // block the .send() to make it synchronous - don't do this in production!
        }

        //flush data
        producer.flush();
        //flush and close data
        producer.close();
    }
}
