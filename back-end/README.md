# Desafio @ Justa Back-end

## Descrição

Rest API que cadastra usuários e gerencia carteiras digitais com câmbio de moeda.  

#### Desenvolvido com as Tecnologias:  
  - Java 11;  
  - Spring Boot;  
  - PostgreSQL;  

#### Também Utilizado:  
  - GitFlow;  
  - JavaDoc;  
  - Swagger;  
  - Testes Unitários;  

#### Apis Consumidas:  
  1. http://api.eva.pingutil.com/email?email= → Validar email e evitar spam.  
  2. https://api.currencyscoop.com/v1/convert?from=&to=&amount=&api_key=ab01db841dcd32f4a495dfce24e1fa54 → Conversão de saldo da carteira.  

#### Rotas:  
  - `POST`    : `/api/login` → Valida email e senha e retorna um Token JWT de acesso.
<br><br>
  - `GET`     : `/api/usuarios` → Lista todos os usuários cadastrados no banco.  
  - `GET`     : `/api/usuarios/{id}` → Busca um usuario por ID.  
  - `DELETE`  : `/api/usuarios/{id}` → Deletar um usuario por ID.  
  - `POST`    : `/api/usuarios` → Criar um usuário com carteira e login.  
  - `PUT`     : `/api/usuarios/{id}` → Atualizar dados de um usuário.
<br><br>
  - `GET`     : `/api/carteira/info` → Busca informações de uma carteira.  
  - `POST`    : `/api/carteira/deposito?valor=` → Deposita um valor em uma carteira.  
  - `PUT`     : `/api/carteira/estado?ativo=` → Ativa ou desativa uma carteira.  
  - `PUT`     : `/api/carteira/cambio?para=` → Realiza o câmbio de moeda de uma carteira.  
