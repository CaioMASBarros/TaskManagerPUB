✅ MODELO COMPLETO DE README.md (para colocar no GitHub)
📝 TaskManager API — Lista de Tarefas com Autenticação JWT

Este projeto é uma API completa de gerenciamento de listas de tarefas, desenvolvida com Spring Boot, seguindo boas práticas de arquitetura, segurança e modularização.
O sistema permite que usuários se registrem, façam login, criem listas de tarefas, gerenciem tarefas individualmente, acompanhem progresso e acessem seus dados de forma segura via JWT.

🚀 Tecnologias Utilizadas

Java 17+

Spring Boot 3

Spring Security 6 (JWT)

Spring Web

Spring Data JPA

MySQL

Lombok

Mappers (interfaces + implementação)

Arquitetura em camadas (Controller → Service → Repository)

Tratamento global de erros


Testes Unitários com JUnit + Mockito

🔐 Autenticação e Autorização

A API utiliza JWT (JSON Web Token) para autenticação:

Endpoint /login gera o token.

Rotas protegidas exigem o header:

Authorization: Bearer <token>


A autorização é baseada em scopes, como:

SCOPE_basic

SCOPE_admin

📚 Estrutura do Projeto
src/main/java/app/taskmanager
│
├── config/            # Configurações (Security, JWT, AdminUser default)
├── controller/        # Controllers REST
│   ├── dto/           # DTOs utilizados nas requisições/respostas
│   └── ...            # Exception Handler, TokenController etc
│
├── entities/          # Entidades do JPA (User, TaskList, Task, Role)
├── mappers/           # Mappers entre entidade <-> DTO
├── repositories/      # Interfaces do Spring Data
├── service/           # Interfaces de serviços
│   └── impl/          # Implementações contendo regras de negócio
└── ...

📌 Principais Funcionalidades
👤 Usuário

Criar conta

Fazer login

Receber JWT com o scope configurado

📝 Listas de Tarefas

Criar uma lista

Buscar todas as listas

Buscar por ID

Excluir

✔️ Tarefas

Criar tarefa ligada a uma lista

Atualizar tarefa (status, título, descrição)

Marcar como concluída

Deletar

🧪 Testes Unitários

O projeto contém testes baseados no padrão AAA (Arrange, Act, Assert):

Mock do repositório (Mockito)

Testes de serviço isolados

Testes de falhas em banco

Testes de exceções

Testes de repositório opcionais
