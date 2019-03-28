# Procjkpmento de deploy sv_jkp

## Sequência de Instalação	
```mermaid
graph LR;
    Nid --> MySQL;
    MySQL --> AS;
    AS --> PostgreSQL;
    PostgreSQL --> ESB;
```

## ODIN
	- Configurar os Recursos JKP (RX, TX e NOT) conforme na Homologação, os IDs desses recursos serão utilizados para a criação do arquivo de environment
		+ environment_JKP_PRODUCAO.sql
	- Criar os templates de envios de notificação de falha do JKP (já estao disponíveis em homologação em: Nid>Billing>Settings>Notifications>Notification Templates)
		+ jkp_import_failed
		+ jkp_export_failed
	- Criar e/ou Configurar o usuário/subscription que enviará as notificações e qual o usuário receberá as notificações por email, os parâmetros de configuração, após serem criados no ODIN devem ser passados para o Keepass (Environment.kdbx), os parâmtros são:
		+ NOTIF_EXPORT_FAILED_SUB: SubscriptionID reponsável pelo envio de notificações (O endereço de email para onde serão enviadas as notificações está configurado no template no ODIN)
		+ NOTIF_EXPORT_FAILED_USR: UserID para o qual o email será enviado ( somente modifica o Nome de exibição, pois o email vai ser préconfigurado no template do ODIN)
		+ NOTIF_EXPORT_FAILED_TPL: Template de Falha de importação de Arquivos JKP
		
		+ NOTIF_IMPORT_FAILED_SUB: SubscriptionID reponsável pelo envio de notificações (O endereço de email para onde serão enviadas as notificações está configurado no template no ODIN)
		+ NOTIF_IMPORT_FAILED_USR: UserID para o qual o email será enviado ( somente modifica o Nome de exibição, pois o email vai ser préconfigurado no template do ODIN)
		+ NOTIF_IMPORT_FAILED_TPL: Template de Fala de exportação de dados JKP para o ODIN

		
## Servidor MySQL 172.20.230.240 (Servidor bcm-emb-stg-reports)
	- Criar o database eletronic_data_interchange usando o arquivo: CREATE_DATABASE_eletronic_data_interchange.sql em: 
	http://172.20.230.153/telcom-dev-team/sv_jkp/tree/master/src/main/resource/database
	- Criar o usuário "jkp" utilizado o script CREATE_USER_jkp.sql disponível em: http://172.20.230.153/telcom-dev-team/sv_jkp/tree/master/src/main/resource/database
	

## Servidor AS
	(estão disponíveis no servidor de homologação)
	- Criar uma JNDI no AS com os seguintes parâmetros:
		+ Datasource Type: RSBMS
		+ Name: JKP_DATABASE
		+ Description: banco de dados com informações sobre o consumo do JKP
		Datasource Provider: default
		Driver: com.mysql.jdbc.Driver
		URL: jdbc:mysql://172.20.230.240/eletronic_data_interchange?zeroDateTimeBehavior=convertToNull
		User Name: jkp
		Password: Password criado na produção para o usuário "jkp" utilizando o script CREATE_USER_jkp.sql (executado anteriormente)
		+ Expose as a JNDI Datasource, Name: java:comp/env/jdbc/jkp

		
	- Instalar em /usr/local/wso2/as/lib/runtimes/ext as seguintes bibliotecas com as seguintes permissões:
		-rw-rw-r--. 1 root root 31217 Dec 13 09:45 opencsv-3.3.jar
		-rw-rw-r--. 1 root root 154095 Jan 17 16:58 as_bus_clients.jar
	- Instalar/Atualizar o arquivo as_utils_1.0.0.war na interface WEB do AS
	- Instalar/Atualizar o arquivo odin_ba_1.0.0.war na interface WEB do AS
	- Ajustar as variáveis do arquivo JKP_IMPORT_PRODUCAO.kdbx (arquivo disponível na tarefa 1951) com as informações do FTP de produção:
		+ SERVER_ADDRESS: IP do servidor FTP onde os arquivos JKP estarão
		+ SERVER_USERNAME: Username
		+ SERVER_PASSWORD: Password
		+ REMOTEPATH: Pasta remota (caminho relativo) onde os arquivos JKP serão salvos, Exemplo: /folder/
	- Verificar se a conexão FTP entre o servidor AS e o servidor FTP estão acontecendo normalmente utilizando telnet ou um client ftp no AS de produção
	- Atualizar o Keepass do AS (Environment.kdbx) Utilizando o arquivo JKP_IMPORT_PRODUCAO.kdbx 
	- Instalar/Atualizar o arquivo sv_jkp_1.0.0.war na interface WEB do AS

	
## Servidor integ-app01-st (D-1) PostgreSQL
	- Instalar a tabela de auditoria "jkp" no schema audit utilizando o seguinte script SQL:
	audit_jkp_create.sql (Cria a sequence audit.jkp_id_seq e a tabela audit.jkp)
	(arquivo disponível em: http://172.20.230.153/telcom-dev-team/esb_jkp/blob/master/JKP/resources/audit_jkp_create.sql )
	- Executar o arquivo: environment_JKP_PRODUCAO.sql para gerar os IDs nas tabelas de environment (Disponibilizado em http://172.20.230.153/telcom-dev-team/sv_jkp/tree/master/src/main/resource)
	
	

## Servidor ESB
	- Instalar/Atualizar o arquivo JKPCompositeApplication_1.0.0.car na interface WEB do ESB (disponível em homologação)
	


	