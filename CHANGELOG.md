[2.1.6]

**Added**
* Sleeping bags can be placed into tents
* Shift Right-Click a tent to pack it up, getting the tent and its sleeping bags back (if theres one inside)
* Large Backpack and Wanderer Backpack: Right-Click the rolled-up sleeping bag on the backpack to unroll it in front of it and sleep in it. Right-Click the top of the backpack again to roll the sleeping bag back up
* Sleeping in a backpack's sleeping bag does not set your spawn point
* The sleeping bag needs two free blocks with solid ground in front of the backpack, otherwise "Not Enough Space" is shown
* Breaking the unrolled sleeping bag rolls it back into the backpack, picking up or breaking the backpack rolls it up too
* Grill: Right-Click food on the grill to flip it, Shift Right-Click to take it off
* Configurable backpack sizes (rows for each backpack)
* New config options: grilled food multiplier, marshmallow roast duration, sleeping bag regeneration and spawn point, sleeping in tents near monsters, monster-free range around tents, monsters tearing down tents
* Multitool now has 175 durability, uses 1 per rotation and can be repaired with Iron Nuggets
* Mosquito Swarms: at night they hover over water in swamps, jungles and along rivers, swirling like a small tornado. They chase you when you stand near a light source and can bite you and make you Itchy (no damage, you scratch yourself now and then), then fly back to the water. Each hit kills one mosquito, frogs eat them. A lit campfire nearby, sleeping or Mosquito Repellent keeps them away (thanks to jason!)
* Mosquito Repellent: Right-Click to spray, keeps mosquitoes away for 5 minutes, soothes Itchy and scatters swarms right in front of you. 16 uses, crafted from a Glass Bottle, Sweet Berries and a Fern (thanks to jason!)

**Changed**
* Creative tab icon is now the Large Backpack
* Tents are now a single block per color instead of four separate blocks, with proper hitboxes and a BER
* Tents can only be placed on solid ground and break when their support is removed
* Reworked config on Fabric and NeoForge with grouped options, tooltips and identical values on both platforms
* Cloth Config is no longer bundled on Fabric and is now a required dependency
* Grilled food tooltip now shows the actual nutrition bonus
* Marshmallow on a stick tooltip colors its roasting stage

**Fixed**
* Multitool rotation particles never showing up

***

[2.1.5]

**Fixed**
* Backpacks in Curios slots (NeoForge) leaving a leftover PoseStack transform, misplacing other visible Curios items (e.g. lanterns, spellbooks) rendered afterward
* NeoForge loot injector not working, making backpacks unobtainable from loot (loot modifiers now live under `data/neoforge/` and use `neoforge:loot_table_id`)

**Changed**
* Updated ru_ru (thanks to Tefny)
* Converted mod images from PNG to WEBP

***

[2.1.4]

**Fixed**
* Tents now correctly break and drop when destroyed from the sealed back corner

**Changed**
* Updated ru_ru (tefnya)

***

[2.1.3]

**Fixed**
* Grilled food giving max nutrition and saturation instead of a 25% increase (thanks to voxu-git)
* Nutrition and saturation values displaying incorrectly in mods like AppleSkin (thanks to voxu-git)
* Incorrect bag drops on shift right-click: normal backpacks dropping nothing and ender bags/backpacks dropping twice (thanks to voxu-git)

**Changed**
* Backpacks and bags are no longer equipable in the chest armor slot

***

[2.1.2]

**Fixed**
* LargeBackpack not being craftable 
* GrilledFood not being consumed (thanks to voxu-git)
* Multitool causing a crash when rotating certain blocks

***

[2.1.1-neo]

**Fixed**
* Equipped backpacks losing their inventory when opened via keybinding (Curios slot)

***

[2.1.1]

**Fixed**
* Wanderer Backpack texture now displays correctly
* Backpacks can now be opened while equipped in the Trinket slot
* Backpacks render properly when worn in the Trinket slot

***

[2.1.0]

**Ported to 1.21.1**

***

[2.0.0]

This is a complete rewrite of Camping, executed by **jason13official**.  
It no longer relies on Architectury and now exists as nearly standalone Forge / Fabric mods.

***