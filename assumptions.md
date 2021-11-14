Assumptions 
------------------------------------------------------------------------------------------
all members contributed assumptions relevant to the parts they coded -- see commit history
------------------------------------------------------------------------------------------
Allies do not receive damage in combat for the player
All other entities continue movement as normal
passing in seed
**Zombies**
- zombies have 20hp and do 5 damage
- Chance of a zombie spawning with armour is 10% 
- Durability of the armour a zombie possesses is decremented 

**Weapons**
- Only Bows and Swords count as weapons

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

**Mercenaries**
- Mercenaries reverse their direction pathing when invincibility potion occurs
- Mercenaries have a battle radius of 2 cardinally adjacent squares, same as Bribe radius
- all mercenaries need 1 gold to be bribed
- Mercenaries periodically spawn at the entry location every 30 ticks.

**Character**
- Character has 100 health damage 10;
- Health is 30 for spider, attack is 5
- Player presumably has more health than the zombie can do in one hit

**Miscellaneous**
- Midnight armour and sceptre has no durability
- Swamp tile causes enemies to be stuck.
- Interactable entities are boulder, door, mercenary, all collectables 
- Hydra only spawns on an empty square
- armour is localized to zombie and not listed in entities
- Saves have the game state, map name and difficulty
- Saves are in a file, in a folder called saves
- Assume portals must be created in pairs of the same colour, two of one colour
- Zombie toast and spider do fixed amounts of damage
- Spider spawn is random
- Battle starts when you move onto the same square and battle ends on the same tick
- Saves can be overwritten, but you should be able to access previous saves
- Player attacks first
- Buildable entities have null position
- Zombie toast spawner is treated as a wall same as boulder
- Enemy goal counts bribing all mercenaries as a win
- Invincibility causes spiders to change direction as if there was a boulder in the way
- If bomb is not cardinally adjacent to an activated switch, an illegal action error is thrown
- The game either accepts movement input or item input not both

Link to timeline and minutes: https://docs.google.com/document/d/1DuSlnb45k3kwA7Nsfz6sRFuLpjWA6yu70Jba2Ndr40c/edit?usp=sharing