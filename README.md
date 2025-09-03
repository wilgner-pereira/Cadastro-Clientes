Cadastro de Clientes – API RESTful






Sobre

API RESTful para gerenciar clientes: cadastro, atualização, listagem, exclusão e cálculo automático da idade. Desenvolvida com foco em funcionalidade, segurança e manutenção, pronta para uso imediato e fácil de evoluir.

Tecnologias

Java 21, Spring Boot, Spring Data JPA

Spring Security com JWT (HMAC)

DTOs com validação e validação personalizada de CPF

JUnit 5 e Mockito para testes

Swagger para documentação

Docker para deploy

Funcionalidades

Cadastro de clientes com validações (CPF, datas, nomes)

Atualização de clientes (atenção especial ao CPF)

Listagem de clientes com paginação

Pesquisa por CPF

Idade calculada automaticamente a partir da data de nascimento

Endpoints protegidos por JWT

Respostas padronizadas com ApiResponse

Acesso

API hospedada: [Swagger UI](https://cliente-cadastro-api-latest.onrender.com/swagger-ui/index.html#/)

LinkedIn: [Meu LinkedIn](https://www.linkedin.com/in/wilgner-dev/)

Uso

Cadastro/Login

/api/auth/register → cadastrar usuário

/api/auth/login → receber Bearer token

Inserir token no cadeado do Swagger para acessar endpoints protegidos

Clientes

Apenas CPFs válidos e datas corretas são aceitos

Idade calculada automaticamente

Erros retornam mensagens claras e padronizadas

Testes

Cobertura com JUnit 5 e Mockito

Testa lógica de negócio, validação de dados e cenários de sucesso/falha

Deploy com Docker
docker build -t clientes-api .
docker run -p 8080:8080 clientes-api

Observações

No projeto, todos os usuários podem atualizar o CPF. Em um sistema real, alterações sensíveis deveriam ser restritas a administradores ou gerentes.

Estruturada para permitir fácil evolução e manutenção, com foco em segurança e confiabilidade.

Processo Criativo

Cada decisão foi tomada pensando em como a API seria usada na prática, equilibrando funcionalidade, segurança e manutenção. Validações, cálculo da idade derivada, testes automatizados e Docker foram implementados para criar um projeto consistente e confiável.
