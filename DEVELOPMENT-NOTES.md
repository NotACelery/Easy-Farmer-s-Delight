# Development notes

## Compatibility boundary

Do not copy, port or subclass implementation code/assets from Easy Villagers.

Interop should prefer, in this order:

1. Minecraft/NeoForge public data components and registries.
2. Resource IDs/tags.
3. Public APIs/events where available.
4. Small runtime adapters only when necessary.

The upgrade recipes intentionally copy the center ItemStack's components and preserve its `BLOCK_ENTITY_DATA`. This keeps the Easy Villagers payload opaque to us until a feature actually needs to interpret a known field.

## Stable NBT owned by this addon

- `EfdcSchema`
- `EfdcBaseProgress`
- `EfdcRopeOneProgress`
- `EfdcRopeTwoProgress`
- `EfdcRopeCount`

Unknown fields must survive load/save.

## Planned crop rules

### Paddy Farmer
- Farmer's Delight Rice.
- Permanent shallow-water environment; no bucket interaction state.

### Rich Farmer
- Superset of normal Farmer crops.
- Tomato persistent vine; harvest fruits without uprooting.
- Up to two Rope infrastructure upgrades.
- Red/Brown Mushroom Colonies; harvest and reset colony growth without consuming a new mushroom.

### Rich Paddy Farmer
- Paddy Farmer + Rich Soil behavior.

## Optional integrations

### Argentum
Do not require the mod at runtime. Optional tag entries currently add:
- `argentum:yerba_semilla`
- `argentum:te_semilla`
- `argentum:batata`
- `argentum:membrillo_semilla`

### Jade
Provider will be registered only when Jade is present.
Tomato target display:
`Growth: Base 76% | Rope 1 53% | Rope 2 21%`
