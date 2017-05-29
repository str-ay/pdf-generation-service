docker kill pdfgs
docker rm pdfgs
./gradlew -Pprofile=docker clean makeDockerExecutable &&
cd docker &&
docker build -t jnesspro/pdf-generation-service .
#docker run --name pdfgs -d -p 80:8080 jnesspro/pdf-generation-service
#docker exec pdfgs fc-list