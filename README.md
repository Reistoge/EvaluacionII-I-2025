# Segunda Evaluación PSP - 2025
### Prof. Daniel San Martín <br> Fecha: 24-06-2025

---

## 🌍 Contexto del problema: Sistema de Monitoreo Ambiental Distribuido

Una empresa de gestión ambiental ha desplegado sensores en distintas estaciones remotas para medir variables críticas de temperatura y material particulado. Estos sensores transmiten datos en tiempo real a un servidor central, pero los ingenieros han identificado tres problemas recurrentes:

1. **Datos inconsistentes:** Llegan mediciones con errores de formato o valores fuera de rango, sin una validación clara.
2. **Alto acoplamiento:** Los componentes actuales están integrados de forma rígida, dificultando el mantenimiento y la evolución del sistema.
3. **Escalabilidad limitada:** En situaciones de carga alta, el sistema no responde adecuadamente y pierde datos.

Para resolver estas deficiencias, se ha solicitado desarrollar un prototipo basado en la **arquitectura Pipe–Filter**, 
que permita desacoplar las etapas del procesamiento y mejorar la mantenibilidad del sistema. Los datos procesados deberán 
almacenarse en una base de datos SQLite. La documentación de las clases se encuentra en la carpeta `documentation` del 
proyecto.

El patrón arquitectural Pipe and Filter se basa en la idea de procesar datos a través de una secuencia de etapas independientes, donde cada etapa realiza una transformación, validación o filtrado específico. En esta arquitectura, los datos fluyen de un componente a otro —los "filtros"— conectados mediante "tuberías" que transportan el resultado de una etapa hacia la siguiente.

Además, con miras a una futura migración a sistemas distribuidos, se requiere una segunda implementación del sistema bajo un modelo **reactivo** basado en **Vert.x y el patrón Pub/Sub**, donde cada filtro sea un `Verticle` conectado a través de canales del `EventBus`.

El sistema será utilizado como base para una red nacional de monitoreo ambiental, por lo que la validación automática de la arquitectura, las pruebas unitarias y la escalabilidad deben considerarse desde el diseño.

---

## 🎯 Objetivo

- Diseñar y aplicar una arquitectura **Pipe–Filter** desacoplada en Java EE.
- Utilizar **SQLite** como motor de persistencia de datos.
- Implementar **verificación arquitectural automática** con la herramienta que considere adecuada.
- Desarrollar pruebas con **JUnit 4**, incluyendo el uso de **Mockito**.
- Reescribir el sistema con **Vert.x** utilizando el patrón **Publish/Subscribe**.

---

## 📋 Tareas

### ✅ Tarea 1: Verificación de Conformidad Arquitectural

Reorganizar la arquitectura actual en capas para implementar pruebas automatizadas que verifiquen dos reglas:

- **Regla R1:** Ningún filtro (**excepto el último filtro aplicado**) debe acceder directamente a las clases que gestionan los datos en la base de datos.
- **Regla R2:** Toda clase de filtro debe implementar la interfaz `RawDataFilter`.

---

### ✅ Tarea 2: Pruebas Unitarias

Implementar al menos **5 casos de prueba** con **JUnit 4**. Uno de ellos debe usar **Mockito sin anotaciones** (`Mockito.mock(...)`) para simular dependencias.

Debe probar:
- Validación correcta de datos.
- Errores de formato o entradas inválidas.
- Filtros que detecten valores extremos.
- Inserción exitosa en la base de datos.
- Que los filtros no accedan a la base de datos directamente.

---

### ✅ Tarea 3: Migración a arquitectura reactiva con Vert.x (solo Verticles, sin BD)

Reimplementar el sistema utilizando **Vert.x** y el patrón **Publish/Subscribe**, modelando cada filtro como un **`Verticle` 
estándar** (sin `WorkerVerticle`). El sistema ha sido parcialmente implementado utilizando Vert.x y el patrón Publish/Subscribe. 
Ya se encuentran desarrollados los siguientes componentes:

- ReaderBDVerticle (worker): Recupera los datos crudos desde una base de datos mediante JPA.

- ProducerBDVerticle: Publica las lecturas obtenidas desde la base de datos en el canal "raw.data.incoming".

- FileStorageVerticle: Escucha el canal "validated.data" y guarda los datos válidos en un archivo llamado data.txt.

#### 🔄 Flujo de datos propuesto:

```text
ProductDatabaseProducerVerticle
        |
        v
"raw.data.incoming"  ← datos crudos
        |
        v
ValidatorFilterVerticle
        |
        v
"filter.validated"   ← datos validados
        |
        v
UnitNormalizerFilterVerticle
        |
        v
"filter.normalized"  ← unidades convertidas
        |
        v
ExtremeValueFilterVerticle
        |
        v
"validated.data"      ← datos listos para almacenar
        |
        +--> FileStorageVerticle (en archivo)

```

Cada verticle debe:

#### 🔧 Funcionamiento de cada Verticle

Cada `Verticle` debe:

- Suscribirse a un canal del `EventBus`:
    ```java
    eventBus.consumer("canal", message -> { ... });
    ```

- Procesar el mensaje recibido (sin operaciones bloqueantes)
- Publicar el resultado al canal siguiente
    ```java
    eventBus.publish("canal.siguiente", resultado);
    ```

#### ✅ Requisitos mínimos

- **Estructura modular:** cada `Verticle` debe estar definido en su propia clase.
- **Comunicación estricta a través del EventBus:** no se permite invocar métodos directamente entre verticles.

---

## 🧼 Filtros a Implementar para Temperatura y Material Particulado

Cada lectura sensorial corresponde a una **de dos variables ambientales**:

- **Temperatura** (`"temperature"`)
- **Material particulado (PM2.5 o PM10)** (`"mp"`)

El objeto `RawData` incluye los siguientes campos:
- `String type` → tipo de variable ("temperatura" o "mp")
- `LocalDateTime timestamp`
- `double measuredValue`

Los filtros deben operar en base al tipo de variable, ya que cada una tiene unidades y rangos distintos.

---

### 1. ✅ ValidatorFilter

**Objetivo:** Verificar que la lectura esté bien formada.

**Validaciones:**
- `type`, `timestamp` y `measuredValue` no deben ser `null`.
- `type` debe ser `"temperature"` o `"mp"`.

**Resultado esperado:**  
Lanza una excepción o rechaza si la lectura es incompleta o tiene tipo inválido.

---

### 2. 📏 UnitNormalizerFilter

**Objetivo:** Asegurar que el valor esté expresado en unidades estándar.

**Convenciones estándar:**
- Temperatura en grados Celsius (°C)
- Material particulado en microgramos por metro cúbico (μg/m³)

**Ejemplos:**
- Si la temperatura viene en Fahrenheit, convertirla a °C:  
  
  - C = (F - 32) / 1.8

- Si el material particulado viene en miligramos por metro cúbico (mg/m³), convertir a μg/m³:
  - μg/m³ = mg/m³ × 1000

---

### 3. ⚠️ ExtremeValueFilter

**Objetivo:** Detectar valores fuera de los rangos razonables para cada variable.

**Rangos aceptables:**

| Variable     | Rango permitido        |
|--------------|------------------------|
| Temperatura  | -50 °C a 70 °C         |
| Material particulado | 0 a 1000 μg/m³ |

**Resultado esperado:**  
Lanza una excepción si el valor está fuera del rango.

---
## ✅ Lista de cotejo — Evaluación (90 pts)

| Ítem a evaluar | Criterios mínimos de logro                                                                                                              | Puntos     |
|----------------|-----------------------------------------------------------------------------------------------------------------------------------------|------------|
| **Arquitectura Pipe–Filter (Java EE)** | Filtros concretos; Organización de clases en capas                                                                                      | **20 pts** |
| **RawDataRepository** | Implementación funcional del método `findAll()` utilizando JPA (`EntityManager`); retorno de datos válidos; manejo de recursos adecuado | **10 pts** |
| **Verificación arquitectural** | Pruebas automáticas que detectan R1 y R2; fallan si se viola la regla                                                                   | **10 pts** |
| **Pruebas unitarias** | ≥ 5 tests JUnit 4; ≥ 1 test con Mockito;                                                                                                | **30 pts** |
| **Migración Vert.x (Pub/Sub)** | Verticles estándar (sin Worker); filtros reactivos encadenados; comunicación solo EventBus; flujo completo probado                      | **20 pts** |

**Total: 90 pts**