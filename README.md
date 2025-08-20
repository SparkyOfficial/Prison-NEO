# Prison NEO - Modern Minecraft Prison Plugin

A comprehensive prison server plugin for Paper 1.21+ with automatic world generation, ranking system, economy, and immersive prison gameplay.

## Features

### 🏗️ **Automatic World Generation**
- Custom world generator creates a massive prison complex
- Multiple cell blocks (A, B, C, D) with individual cells
- Four different mines with varying ore types and payouts
- Central yard with basketball court and exercise area
- Administrative buildings and guard towers
- Perimeter walls with escape prevention

### 👥 **Player Management**
- Automatic player data persistence
- Sentence system (default 72 hours)
- Play time tracking
- Automatic cell assignment based on rank

### 🏆 **Ranking System**
- 6 ranks: D → C → B → A → S → TRUSTEE
- Rank-based mine access and cell block assignment
- Progressive money requirements for ranking up
- Special GUARD rank for staff

### 💰 **Economy System**
- Mining-based income (break ores to earn money)
- Rank-up costs and progression
- Shop system for buying/selling items
- Different payout rates per mine

### ⛏️ **Mining System**
- 4 mines with different ore types:
  - Mine A: Coal (Rank D required, $1.0/block)
  - Mine B: Iron (Rank C required, $2.5/block)
  - Mine C: Gold (Rank B required, $5.0/block)
  - Mine D: Diamond (Rank A required, $10.0/block)
- Automatic mine regeneration every 10 minutes
- Individual block regeneration after mining

### 🏠 **Cell System**
- Automatic cell assignment based on rank
- Cell blocks correspond to rank levels
- Private cells with beds and basic amenities

## Installation

1. Download the latest release
2. Place the JAR file in your `plugins/` folder
3. Start your server
4. Run `/prisonworld create` to generate the prison world
5. Players will automatically be teleported to prison on join

## Commands

### World Management (Admin)
- `/prisonworld create` - Generate the prison world
- `/prisonworld reset` - Reset and regenerate the world
- `/prisonworld tp` - Teleport to prison

### Player Commands
- `/prison info` - View your prison status
- `/prison stats` - View server statistics
- `/cell assign` - Get assigned a cell
- `/cell tp` - Teleport to your cell
- `/mine tp <A|B|C|D>` - Teleport to a mine
- `/mine list` - View available mines
- `/rank up` - Rank up if you have enough money
- `/rank info` - View rank information

## Permissions

- `prison.*` - All permissions
- `prison.admin` - Admin commands
- `prison.world` - World management
- `prison.cell` - Cell commands
- `prison.rank` - Rank commands
- `prison.mine` - Mine commands

## Configuration

The plugin creates a `config.yml` file with customizable settings for:
- World generation parameters
- Economy values and prices
- Rank costs and requirements
- Mine reset intervals
- Player sentence lengths

## Building from Source

```bash
git clone <repository>
cd prison-neo
mvn clean package
```

The compiled JAR will be in the `target/` directory.

## Requirements

- Paper 1.21+ (recommended)
- Java 21+
- Minecraft server with sufficient RAM for world generation

## Support

For issues, feature requests, or support, please create an issue on the GitHub repository.

---

**Prison NEO** - Experience the ultimate prison server gameplay!
