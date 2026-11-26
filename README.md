# ppai-observer-sismologia
Gestión del cierre de órdenes de inspección de sismógrafos en Java. Patrón Observer, mappers y SQLite. TP Diseño de Sistemas, UTN FRC 2025.

# Gestión de cierre de órdenes de inspección de sismógrafos

Aplicación en Java para registrar el cierre de órdenes de inspección de sismógrafos y notificar a los interesados. TP integrador de Diseño de Sistemas, UTN FRC (2025). Trabajo grupal de [X] integrantes.

## Qué hace
- Permite seleccionar una orden de inspección y registrar su cierre con observaciones y motivos.
- Actualiza el estado de la orden y del sismógrafo.
- Notifica el cierre por mail y en un monitor.

## Diseño
- **Patrón Observer:** `GestorInspeccion` es el sujeto; `InterfazMail` e `InterfazMonitor` son los observadores que se suscriben y reciben la notificación.
- **Mappers:** separan el acceso a la base de datos del modelo.
- **Conexión única** a la base mediante la clase `ConexionBD`.
- **Persistencia** en SQLite.
- Interfaz de consola y gráfica (Swing).

## Tecnologías
Java · Swing · SQLite (JDBC)

## Cómo ejecutarlo
1. Abrir la carpeta en IntelliJ IDEA.
2. Agregar `lib/sqlite-jdbc-3.43.0.0.jar` como librería (clic derecho → *Add as Library*).
3. Ejecutar `src/App/Main.java`. La base de datos `sismologia.db` se crea automáticamente.
