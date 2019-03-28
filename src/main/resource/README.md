# Procjkpmento de deploy sv_jkp

## Sequ�ncia de Instala��o	
```mermaid
graph LR;
    Nid --> MySQL;
    MySQL --> AS;
    AS --> PostgreSQL;
    PostgreSQL --> ESB;
```

## ODIN
	- Configurar os Recursos JKP (RX, TX e NOT) conforme na Homologa��o, os IDs desses recursos ser�o utilizados para a cria��o do arquivo de environment
		+ environment_JKP_PRODUCAO.sql
	- Criar os templates de envios de notifica��o de falha do JKP (j� estao dispon�veis em homologa��o em: Nid>Billing>Settings>Notifications>Notification Templates)
		+ jkp_import_failed
		+ jkp_export_failed
	- Criar e/ou Configurar o usu�rio/subscription que enviar� as notifica��es e qual o usu�rio receber� as notifica��es por email, os par�metros de configura��o, ap�s serem criados no ODIN devem ser passados para o Keepass (Environment.kdbx), os par�mtros s�o:
		+ NOTIF_EXPORT_FAILED_SUB: SubscriptionID repons�vel pelo envio de notifica��es (O endere�o de email para onde ser�o enviadas as notifica��es est� configurado no template no ODIN)
		+ NOTIF_EXPORT_FAILED_USR: UserID para o qual o email ser� enviado ( somente modifica o Nome de exibi��o, pois o email vai ser pr�configurado no template do ODIN)
		+ NOTIF_EXPORT_FAILED_TPL: Template de Falha de importa��o de Arquivos JKP
		
		+ NOTIF_IMPORT_FAILED_SUB: SubscriptionID repons�vel pelo envio de notifica��es (O endere�o de email para onde ser�o enviadas as notifica��es est� configurado no template no ODIN)
		+ NOTIF_IMPORT_FAILED_USR: UserID para o qual o email ser� enviado ( somente modifica o Nome de exibi��o, pois o email vai ser pr�configurado no template do ODIN)
		+ NOTIF_IMPORT_FAILED_TPL: Template de Fala de exporta��o de dados JKP para o ODIN

		
## Servidor MySQL 172.20.230.240 (Servidor bcm-emb-stg-reports)
	- Criar o database eletronic_data_interchange usando o arquivo: CREATE_DATABASE_eletronic_data_interchange.sql em: 
	http://172.20.230.153/telcom-dev-team/sv_jkp/tree/master/src/main/resource/database
	- Criar o usu�rio "jkp" utilizado o script CREATE_USER_jkp.sql dispon�vel em: http://172.20.230.153/telcom-dev-team/sv_jkp/tree/master/src/main/resource/database
	

## Servidor AS
	(est�o dispon�veis no servidor de homologa��o)
	- Criar uma JNDI no AS com os seguintes par�metros:
		+ Datasource Type: RSBMS
		+ Name: JKP_DATABASE
		+ Description: banco de dados com informa��es sobre o consumo do JKP
		Datasource Provider: default
		Driver: com.mysql.jdbc.Driver
		URL: jdbc:mysql://172.20.230.240/eletronic_data_interchange?zeroDateTimeBehavior=convertToNull
		User Name: jkp
		Password: Password criado na produ��o para o usu�rio "jkp" utilizando o script CREATE_USER_jkp.sql (executado anteriormente)
		+ Expose as a JNDI Datasource, Name: java:comp/env/jdbc/jkp

		
	- Instalar em /usr/local/wso2/as/lib/runtimes/ext as seguintes bibliotecas com as seguintes permiss�es:
		-rw-rw-r--. 1 root root 31217 Dec 13 09:45 opencsv-3.3.jar
		-rw-rw-r--. 1 root root 154095 Jan 17 16:58 as_bus_clients.jar
	- Instalar/Atualizar o arquivo as_utils_1.0.0.war na interface WEB do AS
	- Instalar/Atualizar o arquivo odin_ba_1.0.0.war na interface WEB do AS
	- Ajustar as vari�veis do arquivo JKP_IMPORT_PRODUCAO.kdbx (arquivo dispon�vel na tarefa 1951) com as informa��es do FTP de produ��o:
		+ SERVER_ADDRESS: IP do servidor FTP onde os arquivos JKP estar�o
		+ SERVER_USERNAME: Username
		+ SERVER_PASSWORD: Password
		+ REMOTEPATH: Pasta remota (caminho relativo) onde os arquivos JKP ser�o salvos, Exemplo: /folder/
	- Verificar se a conex�o FTP entre o servidor AS e o servidor FTP est�o acontecendo normalmente utilizando telnet ou um client ftp no AS de produ��o
	- Atualizar o Keepass do AS (Environment.kdbx) Utilizando o arquivo JKP_IMPORT_PRODUCAO.kdbx 
	- Instalar/Atualizar o arquivo sv_jkp_1.0.0.war na interface WEB do AS

	
## Servidor integ-app01-st (D-1) PostgreSQL
	- Instalar a tabela de auditoria "jkp" no schema audit utilizando o seguinte script SQL:
	audit_jkp_create.sql (Cria a sequence audit.jkp_id_seq e a tabela audit.jkp)
	(arquivo dispon�vel em: http://172.20.230.153/telcom-dev-team/esb_jkp/blob/master/JKP/resources/audit_jkp_create.sql )
	- Executar o arquivo: environment_JKP_PRODUCAO.sql para gerar os IDs nas tabelas de environment (Disponibilizado em http://172.20.230.153/telcom-dev-team/sv_jkp/tree/master/src/main/resource)
	
	

## Servidor ESB
	- Instalar/Atualizar o arquivo JKPCompositeApplication_1.0.0.car na interface WEB do ESB (dispon�vel em homologa��o)
	


	