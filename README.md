# AvAI

AvAI is a Fabric mod that completely replaces Minecraft's default entity goal/brain system with a **custom behavior tree** implementation.  
It aims to create smarter, more tactical mob behavior — including flanking maneuvers, better pathing awareness, trapdoor avoidance, sun danger detection for undead, reaction to being hit, and more.

## Core Features

- Replaces vanilla goal selectors and brain tasks with a flexible **behavior tree** system
- Custom pathfinding calculations to enable flanking, better obstacle avoidance, and trapdoor bug fixes
- Specialized logic for villagers (routines, reactions to threats, profession-aware decisions)
- Applied via mixins to most mobs (zombies, skeletons, creepers, bats, animals, etc.)
- Multi-threaded worker system for performance (4 threads by default)

## Compatibility Notes

### Lithium
Lithium (performance mod from CaffeineMC) heavily optimizes entity AI ticking and pathfinding.  
AvAI uses deep mixins into `MobEntity`, `GoalSelector`, `Brain`, and pathfinder classes — **this almost certainly conflicts with Lithium**.  

- Expected result: crashes, broken AI, or severe lag/stuttering.
- Recommendation: **do not use Lithium together with AvAI** until someone makes a compatibility patch or fork.

Other performance mods (Phosphor, Sodium, etc.) are usually fine, as they don't touch entity AI.

## Repository Structure & Branching Rules

Every supported Minecraft version has its **own dedicated branch**.

- **main** → never commit code here (only docs, README, etc.)
- **1.21.11** → all development for Minecraft 1.21.11 happens here
- **1.21.10**, **1.20.1**, etc. → same logic for other versions

**Rule (very important):**
> Do **NOT** commit any code changes to `main`.  
> Always work and commit on the branch that matches the Minecraft version you're targeting.

This keeps versions cleanly separated and makes backporting/forward-porting much easier.

## Current Status (as of March 2026)

- Working on **1.21.11** (Fabric + Mojang mappings or Yarn final build)
- Behavior tree initializes and applies to most mobs without crashing
- Villager-specific routines and conditions are in progress
- Still needs: better debugging tools, config options, performance tuning

Feel free to open issues or PRs — but remember to target the correct branch!