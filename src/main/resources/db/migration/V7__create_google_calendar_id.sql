-- RF06: cada paciente sincroniza numa agenda secundária dedicada dentro da
-- conta Google do cuidador, em vez de misturar todos os pacientes na agenda
-- "primary" do usuário.

ALTER TABLE pacientes ADD COLUMN google_calendar_id VARCHAR(255);
