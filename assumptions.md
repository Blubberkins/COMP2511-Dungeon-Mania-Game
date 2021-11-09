Assumptions 
------------------------------------------------------------------------------------------
all members contributed assumptions relevant to the parts they coded -- see commit history
------------------------------------------------------------------------------------------
Allies do not receive damage in combat for the player
All other entities continue movement as normal
passing in seed
**Zombies**
- zombies have 20hp and do 5 damage
- Chance of a zombie spawning is 10% 
- durability of the armour a zombie possesses is decremented 
**Weapons**
- Only Bows and Swords counts Weapons
**One Ring**
 - probability of OneRingDrop is 10%
 - The OneRing Fully resurrects the player with his items, and the player resumes combat
 **DungeonMania**
- can have multiple save states, as with most games
- Invisibility means that battle simply does not trigger
**sword**
- sword has durability 4 
- sword has +5 AD

**Consumables**
- invisibility and invincibility potions last 5 ticks
- Health Potions are a Full Restore

**mercenaries**
- Mercenaries reverse their direction pathing when invincibility potion occurs
- Mercenaries have a battle radius of 2 cardinally adjacent squares, same as Bribe radius
- all mercenaries need 1 gold to be bribed
**Character**
- Character has 30 health damage 10;
- health is 30 for spider, attack is 5

**Miscellaneous**
- player presumably has more health than the zombie can do in one hit
- interactable entities are  boulder, door,mercenary, all collectables 
- armour is localized to zombie and not listed in entities
- character starting health == 30;
- saves have the game state, map name and difficulty
- saves are in a file, in a folder called saves
- assume portals must be created in pairs of the same colour, two of one colour
- position of buildables is null
- zombie toast and spider do fixed amounts of damage
- spider spawn is totally random
- battle starts when you move onto the same square
- saves can be overwritten, but you should be able to access previous saves
- player attacks first
- buildable entities have null position
- zombie toast spawner is treated as a wall same as boulder
- enemy goal counts bribing all mercenaries as a win
- invincibility causes spiders to change direction as if there was a boulder in the way

Link to timeline and minutes: https://docs.google.com/document/d/1DuSlnb45k3kwA7Nsfz6sRFuLpjWA6yu70Jba2Ndr40c/edit?usp=sharing