-- Suporte a UC01 (login Google/OAuth2) e RF01/RF02 (inativação lógica)

ALTER TABLE usuarios ADD COLUMN google_access_token TEXT;
ALTER TABLE usuarios ADD COLUMN google_refresh_token TEXT;
ALTER TABLE usuarios ADD COLUMN google_token_expiration TIMESTAMP;

ALTER TABLE pacientes ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;
