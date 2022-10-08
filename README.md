## ARQUITETURA DE MICRO-SERVIÇOS

Aplicação criada acompanhando curso - [Domine Micro serviços](https://www.udemy.com/course/domine-microservicos-e-mensageria-com-spring-cloud-e-docker/)..

Serviços desenvolvidos utilizando:

- Java 11
- Spring-Boot
- Spring-Cloud Open Feign para comunicação Síncrona entre os Micro-serviços
- Authorization Server com Keycloak
- Spring-Cloud, Service Discovery, Api Gateway
- Serviços de Mensageria com RabbitMQ
- Balanceamento de Carga
- Docker

## Instruções de uso da aplicação:

1. Importe as collection que se encontra nos serviços(msavaliadorcredito, mscartoes e msclientes) no Postman.

2. Primeiro temos que criar uma network:
   - `docker network create cursoms-network`

3. Antes de usar a aplicação deve se subir dois containers: 

- RabbitMq
  - Subindo container do rabbitMq:
    - `docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 --network cursoms-network rabbitmq:3.10-management`
- KeyCloak
  - Subindo container do keycloak:
    - `docker run -p 8081:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin --network cursoms-network --name cursoms-keycloak quay.io/keycloak/keycloak:18.0.0 start-dev`

- São duas formas de se usar a aplicação. 
  - Indo em projeto-modular e rodando cada serviço pela IDE.
  - Ou pelo Docker.
- Pelo Doc



