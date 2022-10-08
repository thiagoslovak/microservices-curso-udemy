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

       - `docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 -d --network cursoms-network rabbitmq:3.10-management`

     - Após subir o container, crie uma queue com nome: emissao-cartoes

       

   - KeyCloak

     - Subindo container do keycloak:
       - `docker run -p 8081:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin --network cursoms-network --name keycloak -d quay.io/keycloak/keycloak:18.0.0 start-dev`
     - Após subir o container, import a [realm](https://github.com/thiagoslovak/microservices-curso-udemy/tree/master/keycloack/keycloak) no keycloack.

     

- São duas formas de se usar a aplicação. 

  - Indo em projeto-modular e rodando cada serviço pela IDE.

    - Antes deve ir nas configurações da realm do keycloak: 
      - Em Realm Settings e em Frontend URL e verificar se o campo não está preenchido, se estiver preenchido deixe em branco.

  - Ou pelo Docker.

    

  1. Pelo Docker:

     - Antes deve ir nas configurações da realm do keycloak: 
       - Em Realm Settings e adicionar em Frontend URL: http://cursoms-keycloak:8080
     - Após alterar as configurações do realm, deve criar cada imagem docker a partir do arquivo Dockerfile.
       - Comando para criar uma imagem:
         - `docker build --tag cursoms-cartoes . `
         - Importante criar cada imagem com --tag corresponde ao nome do projeto (--tag cursoms-cartoes, cursoms-clientes, cursoms-avaliadorcredito, cursoms-gateway, cursoms-eureka)

  2. Após criar todas as imagens dos 5 serviços, deve subir os container a partir de cada imagem:

     - Para subir o serviço eurekaserver:
       - ` docker run --name cursoms-eureka -p 8761:8761 --network cursoms-network cursoms-eureka`
     - Para subir o serviço msavaliadorcredito:
       - `docker run --name cursoms-avaliadorcredito -P --network cursoms-network -e RABBITMQ_SERVER=rabbitmq -e EUREKA_SERVER=cursoms-eureka -d cursoms-avaliadorcredito`  
     - Para subir o serviço mscartoes:
       - `docker run --name curso-mscartoes -e RABBITMQ_SERVER=rabbitmq -e EUREKA_SERVER=cursoms-eureka -P --network cursoms-network cursoms-cartoes`
     - Para subir o serviço msclientes:
       - `docker run --name cursoms-clientes --network cursoms-network -e EUREKA_SERVER=cursoms-eureka cursoms-clientes`

     - Para subir o serviço mscloudgateway:
       - `docker run --name cursoms-gateway -p 8080:8080 -e EUREKA_SERVER=cursoms-eureka -e KEYCLOAK_SERVER=keycloak -e KEYCLOAK_PORT=8080 --network cursoms-network -d cursoms-gateway`

## Usando a aplicação

Após subir todos os containers ou subindo a aplicação pela IDE e adicionado as collections no Postman.

O Fluxo de utilização é a seguinte:

- Cada collection vai ter um GET - keycloak-token

  - Para pegar o token deve ir no keycloack, Clients achar o cliente com nome "mscredito" ir em Credentials e copiar o Secret que estiver disponivel. Após isso vá na collections na aba Authorization, no campo Client Secret e cole a credentials copiada. Após isso clique em Get New Acces Token para copiar o token para fazer as requisições.

    

- Collection ms-cliente:

   	1. POST - Save, para salvar um cliente.
   	2. GET - Dados Cliente (CPF), para buscar cliente por cpf.
   	3. GET - Status, para saber o status da aplicação.

- Collection ms-cartoes:

  1. POST - Cadastra, para cadastrar um cartão.
  2. GET - Cartoes Renda Ateh (renda), para buscar cartões com renda até a informada por parâmetro.
  3. GET - Cartoes by Cliente (cpf), para buscar cartões associado ao cpf. 

  

- Collection ms-avaliador-credito

  1. GET - status, para saber o status da aplicação.
  2. GET - Consulta Situacao Cliente, para buscar os cartões vinculado ao cliente.
  3. POST - Realizar Avaliacao, para realizar a avaliação dos cartões disponíveis para renda passada.
  4. POST - Solicitar Cartao, para solicitar cartão para o cliente.



