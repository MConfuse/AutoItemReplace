# Hotbar Item Replacer - A Fabric Mod

### Installation

#### Manual Installation

This mod requires the Fabric Loader 0.14.19 or newer. As of now only the Minecraft version 1.19.4 is supported. If you
haven't installed Fabric mods before, you can find a variety of community guides for doing
so [here](https://fabricmc.net/wiki/install).

### Releases

The latest version of HIR is published right [here](https://github.com/MConfuse/HotbarItemReplacer/releases) on GitHub.
They are suitable for use, but not guaranteed to be free of bugs and other issues.

### Features

When an item breaks/a stack is used up (e.g.: food/blocks), it will be automatically replaced with an item from your
inventory following these rules:

- New item has to be in your inventory, no cheating!
- The new item will be replaced by an equivalent block, or for tools and weapons an item that has the same material. If
  there are no items of the same material, it will search for from best to worst material (Vanilla material order:
  Netherite, Diamond, Iron, Stone, Gold/Wood), taking into account the efficiency/sharpness/silk touch enchantments,
  always equipping the best item available first.
- If a tool is enchanted with Silk Touch, only tools with silk touch will be equipped, even if there are non-silk touch
  tools available!
- When a stack of food is used up, it will first search for another stack of the same kind of food, if none are found,
  it will choose the most nutritious (most hunger bars filled) food first

#### Feature Tracker till full release

- [x] Replace used Block stacks with another stack of the same type
- [x] Replaced used food stacks with the "best" (most hunger bars filled by one item) available in the players inventory
- [x] Replace broken tools and weapons with items of the same material (Broken Item: Iron, Replacement: Iron)
- [x] Replace broken tools and weapons with items of another material (Broken Item: Iron, Replacement: No Iron tool
  found, but a Diamond tool)
- [x] Take basic tool enchantments into account when replacing the tool: No replacing of _Silk Touch_ tools when there
  are no other silk touch tools of the same, or another, material. Give the best (i.e. the highest _Sharpness_ or _
  Efficiency_ level) tool available in your inventory at the time of breaking your previous one
- [ ] Add a GUI/Chat commands for customizing (future) settings
- [ ] Expand the food logic to allow for selecting whether you want to get veggies before meat, the other way around, or
  don't care and then sort those items by their nutrition, or their saturation.
- [ ] Add an option to the food logic that allows avoiding meat or veggies completely
- [ ] Expand the enchantment logic to make the enchantment that is looked for customizable for each (tool) item group (
  i.e. wanting to get the highest _Unbreaking_ level instead of _Efficiency_ or _Sharpness_)
- [ ] Add options to toggle each feature separately

#### After release plans (as of now)

- [ ] Add more ways to customize the behaviour of the item replacements