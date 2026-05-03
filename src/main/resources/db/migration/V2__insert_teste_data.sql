
INSERT INTO usuarios (email, ativo, data_desativacao)
VALUES ('gabiviana.cunha@gmail.com', true, null);


INSERT INTO pacientes (id_usuario, nome, data_nascimento, codigo_emergencia, ficha_emergencial_ativa, tipo_sanguineo)
VALUES (1,  'Gabi', '2000-01-01', 'EMERG-999', true, 'O+');