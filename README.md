----- Endpoints -----
O Endpoint é a porta de entrada pela rede. Ele recebe a requisição SOAP, extrai os dados do XML, chama o Service, e devolve a resposta em XML. Ele não tem lógica nenhuma, só repassa

----- Pom.xml e Maven -----
o pom.xml é uma lista de tudo que o projeto precisa para funcionar, e o Maven (que devemos baixar e adicionar ao Path) usa essa lista para montar tudo automaticamente.

-----JAX-WS-----
JAX-WS significa Java API for XML Web Services. É a API do Java para criar e consumir serviços SOAP.
Sem JAX-WS, para criar um serviço SOAP vocês teriam que escrever manualmente o XML de cada mensagem, montar o envelope SOAP, criar um servidor HTTP, interpretar as requisições que chegam e serializar as respostas. Isso seria centenas de linhas de código repetitivo.
O JAX-WS elimina tudo isso. Vocês só escrevem a classe Java normal com a lógica que interessa, colocam algumas anotações, e ele cuida do resto.

----- Banco H2 -----
Não precisa instalar nada separado. Bancos como MySQL ou PostgreSQL exigem instalação, configuração de usuário, senha, porta, serviço rodando em background. O H2 é só um .jar que o Maven baixa. Quando o programa Java roda, o banco sobe junto. Quando o programa fecha, o banco fecha junto.
Contuso, Gera um arquivo persistente. O banco fica salvo em condominio_db.mv.db na pasta do projeto e os dados não somem quando o servidor reinicia, que é o comportamento esperado de um sistema real. PODEMOS TROCAR SE QUISEREM!!
