-- 1. Aseguramos que estamos en la base conectable correcta
ALTER SESSION SET CONTAINER = FREEPDB1;

-- 2. Damos cuota de espacio al dueño de las tablas (ya fue creado por Docker)
ALTER USER english_admin QUOTA UNLIMITED ON USERS;

-- 3. Creamos el usuario restringido que usará Spring Boot (Mínimo Privilegio)
CREATE USER english_app_user IDENTIFIED BY AppUserPass123;

-- 4. Solo le damos permiso para iniciar sesión (no puede crear tablas ni borrar nada)
GRANT CREATE SESSION TO english_app_user;