# Magical Creature Arena — OOP Project

A turn-based grid combat game where Players (Warrior, Mage, Healer) fight
AI-controlled Creatures (Goblin, Ogre, Dragon, Wyvern, Boss). Built as
a deep-dive demonstration of ALL object-oriented programming principles in Java.

## How to run

### From the command line (Java 8 or newer)

```bash
# Compile
cd src
javac $(find . -name "*.java")

# Run (level1.txt is loaded from the classpath)
java it.unime.arena.MagicalCreatureArena
```

### From an IDE (IntelliJ / Eclipse)

1. Open the `src` folder as a Java sources root.
2. Mark the file `src/it/unime/arena/config/level1.txt` as a
   resource (or just add `src` itself as a resources root).
3. Run `it.unime.arena.MagicalCreatureArena`.

## Objective — Boss Hunt

**Win:** slay the Dragon (Smaug). The other creatures only stand in your way; you don't have to kill them.
**Lose:** all your heroes die before the Dragon falls.

A red boss-HP bar at the top of the window tracks the Dragon's HP at all times.

## Controls

| Key                | Action                  |
|--------------------|-------------------------|
| Arrow keys / WASD  | Move active player      |
| Space              | Attack selected creature|
| P                  | Use a health potion     |
| Tab                | Switch active player    |
| Enter              | End turn (creature AI)  |
| R                  | Restart                 |
| Click red disc     | Select target           |

### Cheat codes

Type these letters anywhere to toggle:

| Code      | Effect                                      |
|-----------|---------------------------------------------|
| `godmode` | Players take no damage; move 5 tiles/turn   |
| `kill`    | Any attack one-shot kills its target        |
| `speed`   | Players move 3 tiles per turn               |

## Project layout

```
it.unime.arena
├── MagicalCreatureArena       application entry point
│
├── interfaces/                contracts (program-to-an-interface)
│   ├── Attacker
│   ├── Damageable
│   ├── Healable
│   ├── Identifiable
│   ├── ManaUser               (replaces "instanceof Mage")
│   ├── Movable
│   └── Positionable
│
├── strategy/                  Strategy pattern: attack & movement algorithms
│   ├── AttackStrategy + Melee/Magic/Ranged/FireBreath
│   └── MovementStrategy + Standard/Fast
│
├── entities/                  game-board objects
│   ├── Entity (abstract)      <- base class, uses strategy via composition
│   ├── Player + Warrior/Mage/Healer
│   ├── Creature + Magical/Boss/Flying/Dragon
│   └── Obstacle + Destructible/Trap
│
├── items/                     pickups & equipment
│   ├── Item (abstract)
│   ├── Equipable contract  -> Weapon, Armor
│   ├── Consumable contract -> HealthPotion, ManaPotion
│   ├── Bonus + BonusType
│   └── Inventory<T extends Item>   <- generic, parametric polymorphism
│
├── engine/                    rules and state
│   ├── Position (immutable)
│   ├── Direction (enum)
│   ├── Arena (state container)
│   ├── CombatManager / MovementManager / AIController
│   ├── GameController (orchestrates turn flow)
│   ├── DamageMath          <- coercion polymorphism
│   └── AttackLog (functional interface)
│
├── factory/                   Factory pattern
│   ├── EntityFactory       <- overloaded createPlayer (ad-hoc polymorphism)
│   └── BonusFactory        <- overloaded createBonus
│
├── exceptions/                typed game-error hierarchy
│   ├── GameException (abstract root)
│   └── ConfigException, EntityDeadException, GameRuleException,
│       InsufficientResourceException, InvalidMoveException,
│       InvalidTargetException
│
├── cheat/                     Singleton pattern
│   ├── CheatCode (enum)
│   └── CheatManager
│
├── io/                        java.io / java.nio.file
│   ├── LevelLoader (BufferedReader-based)
│   └── SaveManager (object serialization)
│
├── config/level1.txt          default level
└── ui/                        Swing front-end
    ├── GamePanel              (rendering only)
    └── MagicalCreatureArenaGUI (controller-driven)
```

## OOP principles demonstrated

The full mapping (with file paths and rationales) is in
[`OOP_PRINCIPLES.md`](OOP_PRINCIPLES.md). The short list:

- **Encapsulation** + **information hiding** — every entity hides its
  state behind getters; mutators are limited to legal transitions.
- **Abstraction** — `Entity`, `Item`, and `GameException` are abstract
  bases; clients depend on interfaces (`Damageable`, `Attacker`, ...).
- **Inheritance** — three-level hierarchy (Dragon → MagicalCreature →
  Creature → Entity) plus the player and item trees.
- **Composition** + **delegation** — Entity HAS-A `AttackStrategy`
  and `MovementStrategy`; behaviour is delegated, not hard-coded.
- **Subtyping** — interfaces let unrelated classes share contracts.
- **Polymorphism — all four Cardelli–Wegner kinds**:
  - **Inclusion** (overriding via inheritance)
  - **Parametric** (generic `Inventory<T extends Item>`, generic method
    `Inventory.namesOf`)
  - **Ad-hoc / overloading** (`Healer.heal`, `EntityFactory.createPlayer`,
    `BonusFactory.createBonus`)
  - **Coercion** (implicit int↔double widening + explicit narrowing in
    `DamageMath` and `MagicAttackStrategy`)
- **Exception handling** — typed game exceptions, try-with-resources for
  every stream, no swallowed errors.
- **File I/O** — `BufferedReader` for the level file (per OOP-13 notes)
  and `ObjectOutputStream`/`ObjectInputStream` for save/load.
