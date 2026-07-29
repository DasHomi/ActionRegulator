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

Each rule targets either **Blocks** or **Entities**, and can additionally filter on the item in your hand.
You can also select Item/Block/Entity tags to select multiple items/blocks/entities at once (e.g., all types of wood).

> [!NOTE]
> A **Use** rule that has no target blocks or entities listed also blocks consuming items (food, potions, ...)

You can toggle the behavior of these lists between **Default** and **Inverted**:

| Mode         | Empty List                                   | List with Entries                                                   |
|:-------------|:---------------------------------------------|:--------------------------------------------------------------------|
| **Default**  | Activates for **all** items/blocks/entities. | Activates **only** for the listed items/blocks/entities.            |
| **Inverted** | Activates for **no** items/blocks/entities.  | Activates for all items/blocks/entities **except** the listed ones. |

### Subconditions
To make your rules even more precise, registry lists support subconditions.

**Hand Item Subconditions:**
- **Hand:** Choose between Both, Mainhand, or Offhand.
- **Durability:** Chose above or below a certain threshold of *remaining* durability. Items that cannot take
  damage are not affected by this condition.
- **Custom Name Mode:** Match by **Filter**, **Regex Filter**, **Custom Name**, or **Default Name**.
- **Custom Name Filter** 
  - **Mode → Filter:** Specify text the item name must contain.
  - **Mode → Regex Filter:** Specify a regular expression the item name must match.
- **Enchantments:** A list of enchantments the hand item must carry. It follows the
  same **Default**/**Inverted** table as the other registry lists, matching when the item has **any** of the
  listed enchantments.
    - **Enchanted:** Restrict the rule based on whether the item is enchanted at all, with three modes:
        - **Ignored:** Enchantment presence does not affect the rule.
        - **Required:** The rule only applies while the item has at least one enchantment.
        - **Forbidden:** The rule only applies while the item has no enchantments.

  > [!NOTE]
  > The enchantment list can only be browsed while you are in a world, because enchantments are provided by
  > the server's data packs. Entries you already picked stay selected everywhere.
  > Enchanted books obtained normally are **not** matched — they merely *store* their enchantments rather
  > than having them applied.

**Target Entity Subconditions:**
- **Custom Name Mode:** Match by **Filter**, **Regex Filter**, **Custom Name**, or **Default Name**.
- **Custom Name Filter**
    - **Mode → Filter:** Specify text the entity name must contain.
    - **Mode → Regex Filter:** Specify a regular expression the entity name must match.

**Target Block Subconditions:**
- **Waterlogged:** Restrict the rule based on whether the target block is waterlogged, with three modes:
    - **Ignored:** The block state does not affect the rule.
    - **Required:** The rule only applies while the target block is waterlogged.
    - **Forbidden:** The rule only applies while the target block is not waterlogged.

### Additional Rule Options
- **Dimension Selector:** Restrict rules to specific dimensions (e.g. only active in the Nether). A rule with no
  dimension selected never applies anywhere.
- **Player Conditions:** Restrict rules to the player's current state:
    - **Elytra Flying** and **Swimming**, each with three modes:
        - **Ignored:** The state does not affect the rule.
        - **Required:** The rule only applies while the state is active (e.g. block attacks *only* while flying).
        - **Forbidden:** The rule only applies while the state is inactive.
    - **Game Mode:** Select which game modes (Survival, Creative, Adventure, Spectator) the rule applies in.
      The rule only applies while in one of the selected modes.
    - **Health** (in hearts) and **Hunger** (food points), each with three modes:
        - **Ignored:** The value does not affect the rule.
        - **Above:** The rule only applies while at or above the threshold.
        - **Below:** The rule only applies while at or below the threshold.
- **Notifications:** Get told when a rule blocked something — either by a **Sound**, a red **Symbol** above the
  hotbar, or your own **Text** in that same spot. Of course it can also stay **Off**.

### Additional Options
- **Global Override Hotkey:** Hold down the override hotkey (Default: `Right Alt`) to temporarily deactivate all Action Regulator rules.

## Sharing & Exporting
Rules can be individually exported and imported as JSON files. This makes it incredibly easy to share your custom configurations with others or sync them across different modpacks.

*Note: If you import a rule created in an older version of the mod, Action Regulator will attempt to auto-migrate it to the current version. However, keeping the mod updated is highly recommended to ensure maximum compatibility.*

## Future Plans
Action Regulator is still in very early development and there are many features and improvements already planned for future versions. Here are some of the most important ones which may or may not come in the future:
- More player conditions (e.g., Status Effects).
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
- [Mod Menu](https://github.com/TerraformersMC/ModMenu) needed to configure rules in the ui