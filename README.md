# Action Regulator

Action Regulator is a Minecraft mod that allows you to block actions in the game. 
It offers a simple and intuitive interface to configure which actions you want to block.

> [!CAUTION]
> Action Regulator is still in early development and may contain bugs. Please do not rely on it blocking actions without prior testing. If you find any bugs, please report them on the GitHub issue tracker.

> [!NOTE]
> If you find any problems in using or understanding the Options UI, please feel free to open an issue on how it could be improved.

## Features
Currently, there are 2 Triggers:
- **Attack** (left-clicking)
- **Use** (right-clicking)

Each trigger has registry-lists that can be configured:
- **Hand Items**
- Either **Target Entities** or **Target Blocks**  

The registry selection can be inverted.  
Default:
- List empty → Activates for all items/blocks/entities
- 1 or more entries → Activates only for the listed items/blocks/entities  

Inverted:
- List empty → Activates for no items/blocks/entities
- 1 or more entries → Activates for all items/blocks/entities except the listed ones

The registry lists have additional subconditions to improve matching.
- **Hand Item Subconditions**: 
  - Hand selection (both, mainhand, offhand)
  - Min durability (durability below which the rule will match)
  - Custom name mode (any, named, default name)
  - Custom name filter (name which the item has to match, only active if custom name mode is on any or named)
- **Target Entity Subconditions**:
  - Custom name mode (any, named, default name)
  - Custom name filter (name which the item has to match, only active if custom name mode is on any or named)

The action will only be blocked if the hand item and the target entity/block conditions, with subconditions, are met.

In addition, each Rule has a selector for active dimensions and a selector for notification types.

## Sharing
Rules can be individually exported and imported as JSON files. This allows you to share your configurations with others or transfer them between different instances of the game. If the exported rule is of an older version than the one that imports it, Action Regulator will try to apply migrations to update the rule to the latest version. However, it is recommended to keep Action Regulator updated to avoid any compatibility issues with shared rules.

## Compatibility
Action Regulator is currently compatible with the latest version of Minecraft and Fabric.
Compatibility with other mods:
- Compatible with Preventer
- Compatible with most mods that add items/blocks/entities. As long as they follow the normal way for adding these they should be selectable in the registry lists.

## Dependencies
- [Fabric API](https://github.com/FabricMC/fabric)
- [Owo Lib](https://github.com/wisp-forest/owo-lib)