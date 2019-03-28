WITH NewParameter AS (
  INSERT INTO environment.parameter_code(
	code, description)
	VALUES ('JKP.RESOURCE.ID.TX', 'JKP Resource ID TX')
	ON CONFLICT (code)	DO NOTHING
	RETURNING parameter_code_id
)
  INSERT INTO environment.parameter_value(
	parameter_code_id, "value", description)
		SELECT parameter_code_id, 100433, 'JKP RESOURCE_ID TX: Transmissão' FROM NewParameter;


WITH NewParameter AS (
  INSERT INTO environment.parameter_code(
	code, description)
	VALUES ('JKP.RESOURCE.ID.NOT', 'JKP Resource ID NOT')
	ON CONFLICT (code)	DO NOTHING
	RETURNING parameter_code_id
)
  INSERT INTO environment.parameter_value(
	parameter_code_id, "value", description)
		SELECT parameter_code_id, 100435, 'JKP RESOURCE_ID NOT: Notificação' FROM NewParameter;
		
		
WITH NewParameter AS (
  INSERT INTO environment.parameter_code(
	code, description)
	VALUES ('JKP.RESOURCE.ID.RX', 'JKP Resource ID RX')
	ON CONFLICT (code)	DO NOTHING
	RETURNING parameter_code_id
)
  INSERT INTO environment.parameter_value(
	parameter_code_id, "value", description)
		SELECT parameter_code_id, 100434, 'JKP RESOURCE_ID RX: Recepção' FROM NewParameter;
