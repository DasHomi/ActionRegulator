# Action Regulator

Action Regulator is a Minecraft mod that allows you to block actions in the game. 
It offers a simple and intuitive interface to configure which actions you want to block.

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

The action will only be blocked if the hand item and the target entity/block conditions are met.

In addition, each Rule has a selector for active dimensions and a selector for notification types.
