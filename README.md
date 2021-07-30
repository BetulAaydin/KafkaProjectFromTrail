# KafkaProjectFromTrail

#My first kafka project from the kafka lessons published on Udemmy platform.
here is url:
   https://turkcell.udemy.com/course/apache-kafka-for-developers-using-springboot/learn/lecture/17307818?start=0#overview

github url:
   https://github.com/dilipsundarraj1/kafka-for-developers-using-spring-boot/blob/master/SetUpKafka.md


#some usefull command suitable for my locak environment;


Run zookeper: cd /D D://Dev/kafka_2.13-2.7.0/bin/windows
zookeeper-server-start.bat ..\..\config\zookeeper.properties

Broker1
kafka-server-start.bat ..\..\config\server1.properties

Broker2
kafka-server-start.bat ..\..\config\server2.properties

Broker3
kafka-server-start.bat ..\..\config\server3.properties


kafka-console-producer.bat --broker-list localhost:9092 --topic library-events

kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic library-events --from-beginning






Consumer
kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic library-events --from-beginning