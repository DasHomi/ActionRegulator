# Action Regulator

Action Regulator is a versatile Minecraft mod that gives you the power to block specific in-game actions. 
Through a clean and intuitive interface, you can easily configure precise rules to restrict exactly what you want.

> [!CAUTION]
> Action Regulator is currently in early development and may contain bugs. Please do not rely on it for critical action-blocking without prior testing. If you encounter any bugs, please report them via the [Issue Tracker](https://github.com/DasHomi/ActionRegulator/issues)

> [!NOTE]
> If you experience any difficulties using or understanding the user interface, feel free to open a UI-improvement issue with your suggestions!

## Features
Action Regulator lets you create and manage custom rules. Each rule consists of a primary trigger and multiple registry lists and subconditions that allow you to precisely define the circumstances under which an action should be blocked.
**For an action to be blocked, all configured conditions, registry lists, and subconditions must be met.**

Currently, there are two primary **Triggers**:
- **Attack** (Left-clicking)
- **Use** (Right-clicking)

Each trigger allows you to configure registry lists for your **Hand Items** and either **Target Entities** or **Target Blocks**.

You can toggle the behavior of these lists between **Default** and **Inverted**:

| Mode         | Empty List                                   | List with Entries                                                   |
|:-------------|:---------------------------------------------|:--------------------------------------------------------------------|
| **Default**  | Activates for **all** items/blocks/entities. | Activates **only** for the listed items/blocks/entities.            |
| **Inverted** | Activates for **no** items/blocks/entities.  | Activates for all items/blocks/entities **except** the listed ones. |

### Subconditions
To make your rules even more precise, registry lists support subconditions.

**Hand Item Subconditions:**
- **Hand Selection:** Choose between Any, Mainhand, or Offhand.
- **Min Durability:** The rule only triggers if the item's durability falls below this threshold.
- **Custom Name Mode:** Match by **Filter**, **Custom Name**, or **Default Name**.
- **Custom Name Filter:** Specify an exact name the item must match (only active if Custom Name Mode is set to Filter).

**Target Entity Subconditions:**
- **Custom Name Mode:** Match by **Filter**, **Custom Name**, or **Default Name**.
- **Custom Name Filter:** Specify an exact name the entity must match (only active if Custom Name Mode is set to Filter).

### Additional Rule Options
- **Dimension Selector:** Restrict rules to specific dimensions (e.g. only active in the Nether).
- **Notifications:** Customize how and if you want to be notified when an action is blocked.

### Additional Options
- **Global Override Hotkey:** Hold down the override hotkey (Default: `Right Alt`) to temporarily deactivate all Action Regulator rules.

## Sharing & Exporting
Rules can be individually exported and imported as JSON files. This makes it incredibly easy to share your custom configurations with others or sync them across different modpacks.

*Note: If you import a rule created in an older version of the mod, Action Regulator will attempt to auto-migrate it to the current version. However, keeping the mod updated is highly recommended to ensure maximum compatibility.*

## Future Plans
Action Regulator is still in very early development and there are many features and improvements already planned for future versions. Here are some of the most important ones which may or may not come in the future:
- Support for Item/Block/Entity Tags to select multiple items/blocks/entities at once (e.g., all types of wood).
- More subconditions for even more precise rules (e.g., item enchantments, target block state, etc.).
- Player conditions (e.g., Health, Hunger, Status Effects, Swimming, etc.).
- Improved custom name matching (e.g., RegEx support).
- Improved notification system (e.g., templates for block names, Minecraft text formatting, etc.).

If you have any suggestions for features or improvements, feel free to open a feature request issue!

## Compatibility
Action Regulator is built for the latest version of Minecraft  

Mod Compatibility:
- Compatible with Preventer
- Compatible with most mods that add items/blocks/entities. As long as they follow the normal way for adding these they should be selectable in the registry lists.

I currently have no plans to add support for other mod loaders like Forge or active support for versions other than the latest Minecraft version.

## Dependencies
- [Fabric API](https://github.com/FabricMC/fabric)
- [Owo Lib](https://github.com/wisp-forest/owo-lib)