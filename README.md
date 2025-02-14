!Hola Estimado Usuario¡
Para poder usar correctamente este sistema
dejo a tu sipodcioón las siguientes instrucciones 
Debes modificar la siguiente
Clase: DatabaseConnection
    private static final String URL = "jdbc:postgresql://localhost:5432/"tu_base";
    private static final String USER = "tu_usuario";
    private static final String PASSWORD = "tu_constraseña";
Debes modificare esta sección dependinedo de la base de datos que vayas ausar
===============================================================================
USA ESTE CODIGO PARA CREAR LAS TABLAS (VARIADO EN CADA BASE DE DATOS)
===============================================================================
-- Módulo: TALENTO HUMANO v3.0
-- 2024/12/09 v3.9
-- PROMPT para chatgpt:
-- Generar el script SQL para CREATE las TABLES. 
-- Respetar los nombres de las tablas como la de los atributos. 
-- Considerar que las tablas BONxEMP y DESxEMP aumentan como clave primaria el id_Pago. 
-- Para el motor Postgresql.
-- --------------------------------------------------------------

-- Tabla DEPARTAMENTOS
CREATE TABLE Departamentos (
    id_Departamento CHAR(7) PRIMARY KEY,
    dep_Nombre CHAR(30) NOT NULL,
    dep_Telefono CHAR(12),
    dep_Mail CHAR(60),
    ESTADO_DEP CHAR(3)
);

-- Tabla ROLES
CREATE TABLE Roles (
    id_Rol CHAR(7) PRIMARY KEY,
    rol_Descripcion CHAR(40) NOT NULL,
    ESTADO_ROL CHAR(3)
);

-- Tabla EMPLEADOS
CREATE TABLE Empleados (
    id_Empleado CHAR(7) PRIMARY KEY,
    emp_Cedula CHAR(10) UNIQUE NOT NULL,
    emp_Apellido1 CHAR(30) NOT NULL,
    emp_Apellido2 CHAR(30),
    emp_Nombre1 CHAR(30) NOT NULL,
    emp_Nombre2 CHAR(30),
    emp_Sexo CHAR(1),
    emp_FechaNacimiento DATE,
    emp_Sueldo NUMERIC(7, 2),
    emp_Mail CHAR(40),
    ESTADO_EMP CHAR(3),
    id_Departamento CHAR(7) REFERENCES Departamentos(id_Departamento),
    id_Rol CHAR(7) REFERENCES Roles(id_Rol)
);

-- Tabla CARGAS
CREATE TABLE Cargas (
    id_Carga CHAR(7) PRIMARY KEY,
    car_Cedula CHAR(10) UNIQUE,
    car_Apellido1 CHAR(30) NOT NULL,
    car_Apellido2 CHAR(30),
    car_Nombre1 CHAR(30) NOT NULL,
    car_Nombre2 CHAR(30),
    car_Sexo CHAR(1),
    car_FechaNacimiento DATE,
    ESTADO_CAR CHAR(3),
    id_Empleado CHAR(7) REFERENCES Empleados(id_Empleado)
);

-- Tabla PAGOS
CREATE TABLE Pagos (
    id_Pago CHAR(7) PRIMARY KEY,
    pag_Descripcion CHAR(40),
    pag_Fecha_Inicio DATE,
    pag_Fecha_Fin DATE,
    ESTADO_PAG CHAR(3)
);

-- Tabla PAGxEMP
CREATE TABLE PagxEmp (
    id_Pago CHAR(7),
    id_Empleado CHAR(7),
    emp_Sueldo NUMERIC(7, 2),
    emp_Bonificaciones NUMERIC(7, 2),
    emp_Descuentos NUMERIC(7, 2),
    emp_Valor_Neto NUMERIC(7, 2),
    ESTADO_PxE CHAR(3),
    PRIMARY KEY (id_Pago, id_Empleado),
    FOREIGN KEY (id_Pago) REFERENCES Pagos(id_Pago),
    FOREIGN KEY (id_Empleado) REFERENCES Empleados(id_Empleado)
);


-- Tabla BONIFICACIONES
CREATE TABLE Bonificaciones (
    id_Bonificacion CHAR(7) PRIMARY KEY,
    bon_Descripcion CHAR(40) NOT NULL,
    bon_Valor NUMERIC(7, 2),
    ESTADO_BON CHAR(3)
);

-- Tabla BONxEMPxPAG
CREATE TABLE BonxEmpxPag (
    id_Bonificacion CHAR(7),
    id_Empleado CHAR(7),
    id_Pago CHAR(7),
    bxe_Fecha DATE,
    bxe_Valor NUMERIC(7, 2),
    ESTADO_BXE CHAR(3),
    PRIMARY KEY (id_Bonificacion, id_Empleado, id_Pago),
    FOREIGN KEY (id_Bonificacion) REFERENCES Bonificaciones(id_Bonificacion),
    FOREIGN KEY (id_Empleado) REFERENCES Empleados(id_Empleado),
    FOREIGN KEY (id_Pago) REFERENCES Pagos(id_Pago)
);

-- Tabla DESCUENTOS
CREATE TABLE Descuentos (
    id_Descuento CHAR(7) PRIMARY KEY,
    des_Descripcion CHAR(40) NOT NULL,
    des_Valor NUMERIC(7, 2),
    ESTADO_DES CHAR(3)
);

-- Tabla DESxEMPxPAG
CREATE TABLE DesxEmpxPag (
    id_Descuento CHAR(7),
    id_Empleado CHAR(7),
    id_Pago CHAR(7),
    dxe_Fecha DATE,
    dxe_Valor NUMERIC(7, 2),
    ESTADO_DXE CHAR(3),
    PRIMARY KEY (id_Descuento, id_Empleado, id_Pago),
    FOREIGN KEY (id_Descuento) REFERENCES Descuentos(id_Descuento),
    FOREIGN KEY (id_Empleado) REFERENCES Empleados(id_Empleado),
    FOREIGN KEY (id_Pago) REFERENCES Pagos(id_Pago)
);

    
