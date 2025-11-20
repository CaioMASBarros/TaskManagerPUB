INSERT INTO tb_roles (role_id, role_name) VALUES (1, 'admin')
    ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);
INSERT INTO tb_roles (role_id, role_name) VALUES (2, 'basic')
    ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);