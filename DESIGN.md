# Easy Farmer's Delight Compat

## 1. Objetivo del mod

Crear un addon para **Minecraft 1.21.1 + NeoForge** que conecte **Easy Villagers** con **Farmer's Delight**, permitiendo automatizar correctamente cultivos que el Farmer de Easy Villagers actualmente no soporta.

El mod tendrá bloques, modelos, texturas y lógica propios.

### Dependencias obligatorias

- Easy Villagers
- Farmer's Delight
- NeoForge

Si falta Easy Villagers o Farmer's Delight, Minecraft debe mostrar un error de dependencia faltante al iniciar.

### Integraciones opcionales

- Jade
- Argentum
- Posibles addons futuros de Farmer's Delight

---

# 2. Filosofía del mod

No modificar ni redistribuir Easy Villagers.

No copiar:

- código de Easy Villagers;
- texturas;
- modelos;
- otros assets.

Nuestros bloques pueden inspirarse en el lenguaje visual de Easy Villagers, pero todos los assets se crearán desde cero.

Farmer's Delight puede utilizarse como dependencia directa y sus bloques/items pueden ser referenciados.

---

# 3. Familia de Farmers

El mod tendrá cuatro niveles/variantes principales.

```text
                  Farmer
                 /      \
                /        \
               ↓          ↓
        Rich Farmer    Paddy Farmer
                           |
                           ↓
                    Rich Paddy Farmer
```

---

# 4. Farmer normal de Easy Villagers

El Farmer original no será reemplazado.

Continuará manejando todos los cultivos vanilla compatibles.

## Farmer's Delight

Actualmente ya funcionan:

- Cabbage
- Onion

No necesitamos alterar ese comportamiento.

## Argentum

Si Argentum está instalado, agregaremos automáticamente al Farmer normal:

- Yerba Mate
- Té
- Batata
- Membrillo

Estos cultivos ya utilizan `CropBlock` con una propiedad `age`, por lo que Easy Villagers puede procesarlos normalmente.

El único problema actual es que sus planting items no pertenecen al tag:

```text
#minecraft:villager_plantable_seeds
```

Nuestra compatibilidad agregará:

```text
argentum:yerba_semilla
argentum:te_semilla
argentum:batata
argentum:membrillo_semilla
```

sin modificar Argentum.

Argentum será una dependencia **opcional**.

---

# 5. Paddy Farmer

Variante especializada en cultivos acuáticos.

Inicialmente soportará:

- Rice de Farmer's Delight.

No soportará Kelp ni otros cultivos acuáticos vanilla salvo que exista una razón clara para agregarlos posteriormente.

## Receta

```text
G G G
G F G
I W I
```

Donde:

- `G` = Glass Pane
- `F` = Farmer de Easy Villagers
- `I` = Iron Ingot
- `W` = Water Bucket

El Water Bucket devuelve el Bucket vacío.

## Aspecto

Diseño propio inspirado conceptualmente en una pequeña granja inundada:

- aldeano en una plataforma elevada;
- tierra debajo;
- capa de agua poco profunda;
- arroz creciendo dentro del agua.

El agua será parte del sistema interno/render del bloque.

No requerirá:

- waterlogging real;
- colocar agua manualmente;
- retirar agua con un balde.

El Paddy Farmer siempre será un sistema acuático.

---

# 6. Rich Farmer

Versión avanzada del Farmer terrestre.

Tendrá todas las capacidades del Farmer normal y agregará soporte para cultivos especiales.

## Receta

```text
G G G
G F G
B R B
```

Donde:

- `G` = Glass Pane
- `F` = Farmer
- `B` = Iron Block
- `R` = Rich Soil

Costo total adicional:

- 5 Glass Panes
- 2 Iron Blocks
- 1 Rich Soil

El hierro funciona como límite natural para evitar automatización industrial excesivamente barata.

## Aspecto

La tierra visible dentro del Farmer debe convertirse visualmente en:

```text
farmersdelight:rich_soil
```

Idealmente nuestro modelo referenciará directamente la textura de Farmer's Delight en runtime, evitando incluir una copia de esa textura dentro del addon.

---

# 7. Rich Paddy Farmer

Versión avanzada del Paddy Farmer.

## Receta

Usará exactamente la misma estructura que el Rich Farmer:

```text
G G G
G P G
B R B
```

Donde:

- `G` = Glass Pane
- `P` = Paddy Farmer
- `B` = Iron Block
- `R` = Rich Soil

Esto establece una regla simple:

> Cualquier Farmer compatible puede transformarse en su versión Rich usando 5 Glass Panes, 2 Iron Blocks y 1 Rich Soil.

## Aspecto

El suelo inferior cambia a Rich Soil.

Mantiene:

- agua;
- arrozal;
- estructura acuática;
- aldeano elevado.

---

# 8. Conservación de contenidos durante upgrades

Transformar:

```text
Farmer → Rich Farmer
```

o:

```text
Paddy Farmer → Rich Paddy Farmer
```

debe conservar todo el contenido del bloque.

Esto incluye:

- aldeano almacenado;
- profesión;
- nivel;
- experiencia;
- inventario;
- output;
- cultivo seleccionado;
- progreso del cultivo;
- upgrades;
- configuración interna.

Ejemplo:

```text
Paddy Farmer

Rice
Growth: 73%
Output: 32 Rice

        ↓

Rich Paddy Farmer

Rice
Growth: 73%
Output: 32 Rice
Rich Soil: Active
```

Nada debe resetearse.

---

# 9. Mecánica del Rich Soil

No inventaremos un multiplicador arbitrario.

Las variantes Rich intentarán reproducir la propiedad real de Farmer's Delight.

Rich Soil tiene una posibilidad configurable de acelerar plantas mediante un efecto equivalente a Bone Meal.

Nuestros Farmers Rich deberán respetar el valor configurado por Farmer's Delight.

Por ejemplo:

```text
richSoilBoostChance = 0.20
```

significa que nuestras variantes Rich utilizarán también ese 20%.

Si el usuario cambia la configuración de Farmer's Delight, nuestro mod se adapta automáticamente.

El Rich Paddy Farmer se considera permanentemente hidratado.

---

# 10. Tomates

Los tomates serán una de las características principales del Rich Farmer.

No estarán disponibles en el Farmer normal.

## Plantación

El Rich Farmer aceptará:

```text
Tomato Seeds
```

y manejará una planta persistente.

## Cosecha

Los Tomatoes funcionan como berries:

- la planta no se destruye;
- se recolectan los tomates;
- la planta permanece;
- vuelve a producir frutos.

Por tanto:

```text
Tomato con frutos
       ↓
cosecha
       ↓
Tomatoes al output
       ↓
misma planta sin frutos
       ↓
nuevo crecimiento
```

Nunca se debe volver a plantar una semilla después de cada cosecha.

---

# 11. Rope para Tomatoes

El Rich Farmer tendrá soporte para Rope.

Máximo:

```text
2 Rope
```

permitiendo:

```text
Base
Rope 1
Rope 2
```

Es decir, hasta tres niveles productivos.

Cada Rope es infraestructura permanente y no se consume durante las cosechas.

Idealmente podrá retirarse nuevamente.

## Progreso independiente

Cada sección tendrá su propio ciclo.

Ejemplo:

```text
Base:   100%
Rope 1:  63%
Rope 2: 100%
```

Al cosechar:

```text
Base:     0%
Rope 1:  63%
Rope 2:   0%
```

Cada sección madura y se cosecha independientemente.

## Render

Cuando una sección alcanza madurez:

- aparece con tomates.

Después de cosechar:

- permanece la planta;
- desaparecen los frutos;
- comienza nuevamente el ciclo.

Agregar Rope debe modificar visualmente el Farmer.

Sin Rope:

```text
Base
```

Con una:

```text
Base
Rope 1
```

Con dos:

```text
Base
Rope 1
Rope 2
```

---

# 12. Mushroom Colonies

El Rich Farmer soportará:

- Red Mushroom
- Brown Mushroom

Estos deben utilizar conceptualmente la mecánica de Mushroom Colonies de Farmer's Delight.

## Crecimiento

Las colonias tendrán crecimiento progresivo hasta su estado máximo.

Cuando llegan a madurez:

```text
Mushroom Colony
Age máximo
      ↓
cosecha
      ↓
Mushrooms al output
      ↓
Colony vuelve al inicio
      ↓
vuelve a crecer
```

No se destruye permanentemente la colonia.

No se debe consumir otro mushroom después de cada cosecha.

Conceptualmente el Farmer cosecha y mantiene/replanta inmediatamente la misma colonia.

---

# 13. Jade

Jade será una integración **opcional**.

Si Jade no está instalado:

- el mod funciona normalmente.

Si Jade está instalado:

- se muestra información detallada al mirar nuestros Farmers.

## Farmer normal

Ejemplo:

```text
Farmer
Yerba Mate
Growth: 62%
```

## Rich Farmer

Ejemplo:

```text
Rich Farmer
Tomato
Growth: Base 76% | Rope 1 53% | Rope 2 21%
Rich Soil: Active
```

La información de Tomato se muestra en **una sola línea**, no tres.

Con una sola Rope:

```text
Growth: Base 76% | Rope 1 53%
```

Sin Rope:

```text
Growth: Base 76%
```

## Paddy

```text
Paddy Farmer
Rice
Growth: 43%
```

## Rich Paddy

```text
Rich Paddy Farmer
Rice
Growth: 43%
Rich Soil: Active
```

También podríamos mostrar información futura como:

```text
Ropes: 2/2
```

si resulta útil sin saturar el HUD.

---

# 14. UI interna de los Farmers

Cada bloque necesita como mínimo permitir:

- insertar aldeano;
- seleccionar/insertar cultivo;
- visualizar output;
- retirar producción;
- visualizar upgrades instalados.

Rich Farmer necesita además:

- slot o mecanismo para Rope;
- máximo 2 Rope.

Paddy Farmer no necesitará:

- Water Bucket slot;
- control para llenar/vaciar agua.

---

# 15. Cultivos y compatibilidad inicial

## Farmer

```text
Vanilla
Cabbage
Onion

Argentum opcional:
Yerba Mate
Té
Batata
Membrillo
```

## Rich Farmer

```text
Todo lo del Farmer
Tomato
Red Mushroom
Brown Mushroom
```

## Paddy Farmer

```text
Rice
```

## Rich Paddy Farmer

```text
Rice + Rich Soil acceleration
```

---

# 16. Assets

Todos los assets propios serán creados desde cero:

- texturas;
- modelos;
- blockstates;
- GUI;
- iconos;
- renders.

No se copiarán assets de Easy Villagers.

Sí podremos referenciar recursos externos instalados cuando corresponda, como:

```text
farmersdelight:block/rich_soil
```

para que los resource packs también se reflejen automáticamente.

---

# 17. Compatibilidad y arquitectura

El código debería separarse aproximadamente así:

```text
easyfarmersdelight
│
├── core
│   ├── blocks
│   ├── blockentities
│   ├── inventory
│   ├── recipes
│   ├── rendering
│   └── crop logic
│
├── farmersdelight
│   ├── tomato
│   ├── mushrooms
│   ├── rice
│   └── rich soil
│
├── compat
│   ├── jade
│   └── argentum
│
└── client
    ├── models
    ├── gui
    └── renderers
```

La compatibilidad opcional nunca debe provocar crashes si el mod correspondiente no está instalado.

---

# 18. Prioridad de desarrollo

## Fase 1 — Base técnica

- Crear proyecto NeoForge 1.21.1.
- Agregar dependencias obligatorias.
- Registrar bloques/items.
- Implementar almacenamiento del Villager.
- Implementar inventarios.
- Implementar conservación de datos mediante crafting upgrades.

## Fase 2 — Paddy Farmer

- Crear Paddy Farmer.
- Implementar receta.
- Implementar Rice.
- Implementar render de agua y cultivo.
- Implementar crecimiento/cosecha.

## Fase 3 — Rich Farmer

- Crear Rich Farmer.
- Implementar receta.
- Implementar Rich Soil boost.
- Implementar textura/render Rich Soil.

## Fase 4 — Tomato

- Tomato base.
- Cosecha sin destruir planta.
- Rope 1.
- Rope 2.
- Progreso individual.
- Render de frutos.
- Output.

## Fase 5 — Mushroom Colonies

- Red Mushroom.
- Brown Mushroom.
- Crecimiento.
- Cosecha.
- Regeneración de colonia.

## Fase 6 — Rich Paddy Farmer

- Upgrade desde Paddy Farmer.
- Conservación completa de contenidos.
- Aplicación del Rich Soil boost.

## Fase 7 — Argentum

Agregar automáticamente:

- Yerba Mate
- Té
- Batata
- Membrillo

al soporte del Farmer base cuando Argentum esté instalado.

## Fase 8 — Jade

Mostrar:

- cultivo;
- porcentaje;
- Rich Soil;
- Rope;
- progresos individuales de Tomato.

## Fase 9 — Pulido

- sonidos;
- partículas;
- tooltips;
- traducciones;
- recipes/JEI-style visibility;
- configs;
- balance;
- documentación;
- icono;
- pruebas multiplayer;
- pruebas dedicated server.

---

# 19. Configuración futura

Sería bueno permitir posteriormente configurar:

```text
Paddy Farmer speed
Rich Soil behavior
Maximum tomato ropes
Crop output multiplier
Enable Argentum compatibility
Enable individual crops
```

Pero la primera versión debería intentar respetar al máximo las configuraciones originales de Easy Villagers y Farmer's Delight antes de inventar parámetros propios.

---

# 20. Publicación

El mod debería presentarse explícitamente como:

> Unofficial compatibility addon for Easy Villagers and Farmer's Delight.

No debe insinuar afiliación oficial con sus autores.

Proyecto independiente:

```text
Easy Farmer's Delight Compat
```

o algún nombre equivalente que decidamos posteriormente.

La primera versión pública ya tendría suficiente contenido para justificar plenamente que sea un mod independiente:

- Paddy Farmer;
- Rich Farmer;
- Rich Paddy Farmer;
- Rice automation;
- Tomato + Rope system;
- Mushroom Colonies;
- Rich Soil integration;
- Jade integration;
- Argentum compatibility.

---

# 1.2.0 implementation snapshot

This section is authoritative for the 1.2.0 implementation where older design notes above describe the original 1.0/1.1 plan.

## Harvest tools

Rich Farmer / Rich Paddy use a generalized protected Harvest Tool slot accepting Knife, Hoe and Axe. Tool behavior remains crop-specific: Hoe is optional for compatible normal/Tomato Fortune loot, Knife is optional for Rice and required for mature Mushroom Colonies, and Axe is required for ready Melon/Pumpkin fruit.

The empty-slot tooltip is icon-only and rotates actual registered tools from the relevant tags.

## Paddy extensions

Paddy and Rich Paddy support Rice plus a Sand-installed virtual Sugar Cane mode. Sugar Cane has virtual height and internal age, does not receive Rich Soil acceleration, and can be dismantled losslessly with sneak-use.

The villager support and Sugar Cane Sand are submerged at the same visible waterline.

## Melon / Pumpkin

Rich Farmer models a virtual stem (0..7) and separate fruit-generation phase. Rich Soil only boosts the stem phase. Fruit requires Axe, uses real block loot and only damages the Axe after a successful committed harvest.

Renderer layout is 1/3 stem + 2/3 fruit with vanilla attached-stem state when ready.

## Cutter standby

The Cutter performs non-RNG processability inspection before progress starts. Missing/wrong tool leaves progress at zero instead of repeatedly running failed work cycles.

## Villager Noise Switch

Client-local persistent Villager mute control presented as a physical EasyFD machine. Lever/Redstone state is visual only and has no server redstone behavior.

## Jade / JEI / EMI

Jade displays live machine blockers/phases without rolling recipe RNG.

JEI and EMI share one viewer-neutral dataset:
- 3 Harvest Tool + 2 Paddy + 5 Rich Farmer documentation entries;
- 9 Block Guide pages.

Both viewers are presentation adapters only. Gameplay remains authoritative.
