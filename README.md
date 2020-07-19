RoDSStar
===

To run application do the following:
- DockerDesktopVM needs at least 3GB RAM
- run ```build.bat```
- after build complete run ```start-services.bat```
- after all services are up run ```start-apps.bat```

To stop application:
- run ```stop-apps.bat```
- run ```stop-services.bat```

The following services are used:
- postgresql

The application:
- scheduler-service:
    - ```localhost:8080/createOptimalization```
    - ```localhost:8080/loadSchedule```

Additional information:
- for postman testing the files located under ```postman``` directory
- postgresql 
    - user: ```postgres``` 
    - password: ```docker```
    - ```localhost:5432```

Curl examples:
- ```curl --location --request POST localhost:8080/createOptimalization --form file=@sample_input/orders.csv --output sample_output/output.zip```
- ```curl --location --request POST localhost:8080/createOptimalization --form file=@sample_input/orders.csv startDate='2020.07.20. 06:00' --output sample_output/output.zip```
- ```curl --location --request POST localhost:8080/loadSchedule --form file=@sample_input/orders.csv --output sample_output/output.zip```