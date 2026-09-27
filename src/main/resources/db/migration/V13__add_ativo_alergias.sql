-- Alergia é dado clínico: desmarcar "possui alergia" no prontuário apagava a linha
-- fisicamente. Passa a ser desativação, como nos demais documentos clínicos.
ALTER TABLE alergias ADD COLUMN ativo BOOLEAN DEFAULT TRUE;
