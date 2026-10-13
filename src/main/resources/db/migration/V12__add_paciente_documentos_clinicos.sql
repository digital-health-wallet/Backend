ALTER TABLE exames ADD COLUMN id_paciente BIGINT;
ALTER TABLE receitas ADD COLUMN id_paciente BIGINT;
ALTER TABLE diagnostico ADD COLUMN id_paciente BIGINT;

UPDATE exames SET id_paciente =
    (SELECT a.id_paciente FROM agendamentos a WHERE a.id = exames.id_agendamento)
    WHERE id_agendamento IS NOT NULL;

UPDATE receitas SET id_paciente =
    (SELECT a.id_paciente FROM agendamentos a WHERE a.id = receitas.id_agendamento)
    WHERE id_agendamento IS NOT NULL;

UPDATE diagnostico SET id_paciente =
    (SELECT a.id_paciente FROM agendamentos a WHERE a.id = diagnostico.id_agendamento)
    WHERE id_agendamento IS NOT NULL;
