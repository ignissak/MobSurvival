# MobSurvival

MobSurvival is Minecraft mob wave minigame. After certain amount of players join, they are spawned into arena with 4 spawning areas. Each wave spawns certain amount and types of mobs. This project is in progress.

## Waves

There are 9 mobs supported in this minigame. Every mob type starts spawning at different wave. This can be viewed in [this class](https://github.com/ignissak/MobSurvival/blob/main/src/main/kotlin/sk/ignissak/mobsurvival/game/wave/Mob.kt).

### Boss waves

A boss spawns every 5th wave. In older version, this was just another type of mob with larger health pool and bigger base damage but nowadays, there are custom mobs that custom mechanics.

#### Enderman

Enderman will periodically teleport to a different player and spawn endermites at his previous location. These endemites will target the previous player. Maximum number of endermites equals to number of playing players.

#### Golem

Throwing player away stuns (blind and slowness) them for a short period of time. Target will be changed from stunned player. This has a short cooldown.

#### Ravager (WIP)

Ravager will periodically spawn low-hp vindicators on its side. Only 3 vindicators can be spawned at a time.

#### Wither Skeleton (WIP)

Wither Skeleton can heal for the portion of damage dealt to the players. Killing a player gives him back 50% of his health.
