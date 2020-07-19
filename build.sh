cd config
./mvnw clean package -P docker
cd ..

cd eureka
./mvnw clean package -P docker
cd ..

cd zuul
./mvnw clean package -P docker
cd ..

cd boarding
./mvnw clean package -P docker
cd ..

cd card-terminal
./mvnw clean package -P docker
cd ..

cd visualization
./mvnw clean package -P docker
cd ..

docker-compose -f build.yml build