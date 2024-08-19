### use Pulsar with Java 
1. compile
    ````
    mvn compile
    ````
2. package
    ````
    mvn clean package -DskipTests  
    ````
3. build image
    ````
    docker build -t myproject .
    ````

4. create a docker-compose.yaml

    ````ymal
    # version: '3.8'
    
    services:
      pulsar:
        image: apachepulsar/pulsar:latest
        container_name: pulsar
        privileged: true
        ports:
          - "6650:6650"
          - "8080:8080"
        volumes:
          - pulsar-data:/pulsar/data
          - pulsar-conf:/pulsar/conf
        command: bin/pulsar standalone
        user: root
        networks:
          - mynetwork
      myproject:
        container_name: myproject
        image: myproject
        build: # your build path
        ports:
          - "8082:8082"
        depends_on:
          - pulsar
        environment:
          - PULSAR_ADMIN_URL=pulsar://pulsar:6650
          - PULSAR_SERVICE_URL=http://pulsar:8080
        networks:
          - mynetwork
      myproject-1:
        container_name: myproject-1
        image: myproject
        build: # your build path
        ports:
          - "8083:8082"
        depends_on:
          - pulsar
        environment:
          - PULSAR_ADMIN_URL=pulsar://pulsar:6650
          - PULSAR_SERVICE_URL=http://pulsar:8080
        networks:
          - mynetwork
      myproject-2:
        container_name: myproject-2
        image: myproject
        build: # your build path
        ports:
          - "8084:8082"
        depends_on:
          - pulsar
        environment:
          - PULSAR_ADMIN_URL=pulsar://pulsar:6650
          - PULSAR_SERVICE_URL=http://pulsar:8080
        networks:
          - mynetwork
    
    volumes:
      pulsar-data:
      pulsar-conf:
    
    networks:
      mynetwork:
        driver: bridge
    ````

### 註記：
本專案為本機練習使用，故設定為 ``standalone``
筆者嘗試在本機使用 cluster, docker 差點爆掉，請謹慎服用＾＾
``standalone`` 下，因為 pulsar 這包 image zookeeper bookkeeper 都包在裡面故可以直接使用
cluster 的使用需要另外做設定
