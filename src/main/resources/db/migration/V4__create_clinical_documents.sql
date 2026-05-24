-- Criação da tabela de Medicamentos
CREATE TABLE medicamentos (
                              id_medicamento SERIAL PRIMARY KEY,
                              nome_medicamento VARCHAR(50) NOT NULL,
                              laboratorio VARCHAR(50),
                              feedback TEXT
);

-- Criação da tabela de Diagnósticos (ligada ao agendamento)
CREATE TABLE diagnostico (
                             id_diagnostico SERIAL PRIMARY KEY,
                             id_agendamento INTEGER NOT NULL,
                             nome VARCHAR(50) NOT NULL,
                             cid VARCHAR(50),
                             descricao VARCHAR(200),
                             doenca_cronica BOOLEAN DEFAULT FALSE,
                             CONSTRAINT fk_diagnostico_agendamento FOREIGN KEY (id_agendamento) REFERENCES agendamentos(id) ON DELETE CASCADE
);

-- Criação da tabela de Exames (id_agendamento é NULLABLE)
CREATE TABLE exames (
                        id_exame SERIAL PRIMARY KEY,
                        id_agendamento INTEGER NULL,
                        nome_exame TEXT NOT NULL,
                        data_hora_exame DATE NOT NULL,
                        observacoes TEXT,
                        CONSTRAINT fk_exames_agendamento FOREIGN KEY (id_agendamento) REFERENCES agendamentos(id) ON DELETE SET NULL
);

-- Criação da tabela de Receitas (id_agendamento é NULLABLE)
CREATE TABLE receitas (
                          id_receita SERIAL PRIMARY KEY,
                          id_agendamento INTEGER NULL,
                          data_emissao TIMESTAMP NOT NULL,
                          orientacoes_gerais TEXT,
                          CONSTRAINT fk_receitas_agendamento FOREIGN KEY (id_agendamento) REFERENCES agendamentos(id) ON DELETE SET NULL
);

-- Criação da tabela de Itens da Receita (N-N entre Receita e Medicamento)
CREATE TABLE item_receita (
                              id_item_receita SERIAL PRIMARY KEY,
                              id_receita INTEGER NOT NULL,
                              id_medicamento INTEGER NOT NULL,
                              posologia TEXT NOT NULL,
                              uso_continuo BOOLEAN DEFAULT FALSE,
                              CONSTRAINT fk_item_receita_receita FOREIGN KEY (id_receita) REFERENCES receitas(id_receita) ON DELETE CASCADE,
                              CONSTRAINT fk_item_receita_medicamento FOREIGN KEY (id_medicamento) REFERENCES medicamentos(id_medicamento)
);

-- Criação da tabela de Uploads (Centraliza arquivos de Exames e Receitas)
CREATE TABLE uploads (
                         id_upload SERIAL PRIMARY KEY,
                         id_exame INTEGER NULL,
                         id_receita INTEGER NULL,
                         base_64 TEXT NOT NULL,
                         CONSTRAINT fk_uploads_exame FOREIGN KEY (id_exame) REFERENCES exames(id_exame) ON DELETE CASCADE,
                         CONSTRAINT fk_uploads_receita FOREIGN KEY (id_receita) REFERENCES receitas(id_receita) ON DELETE CASCADE,
                         CONSTRAINT chk_upload_destino CHECK (id_exame IS NOT NULL OR id_receita IS NOT NULL)
);