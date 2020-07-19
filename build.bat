cd scheduler
call mvnw clean package
cd ..

docker-compose -f build.yml build