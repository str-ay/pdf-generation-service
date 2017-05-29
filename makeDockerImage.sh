docker kill pdfgs
docker rm pdfgs
ver="$(cat build.gradle | grep "def ver = " | tr -d "'")" &&
ver="$(echo ${ver:10})" &&

./gradlew -Pprofile=docker clean bootRepackage &&
cp build/libs/pdf-generation-service-${ver}.war docker/pdf-generation-service.war
cd docker &&
docker build -t jnesspro/pdf-generation-service .
#docker run --name pdfgs -d -p 8085:8080 jnesspro/pdf-generation-service
#docker exec pdfgs fc-list