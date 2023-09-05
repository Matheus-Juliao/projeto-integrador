##Selecionando a imagem
FROM openjdk:17
##expondo porta 8080
EXPOSE 8080
##Criando pasta para o app
WORKDIR /src
## Copiando o arquivo jar para a pasta
COPY /target/*.jar /src/task-management.jar
##Lista de comandos a serem execultadas
ENTRYPOINT [ "java", "-jar", "/src/task-management.jar" ]