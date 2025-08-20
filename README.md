# Prison NEO - Modern Minecraft Prison Plugin

A comprehensive prison server plugin for Paper 1.21+ with automatic world generation, ranking system, economy, and immersive prison gameplay featuring advanced systems like NPCs, mini-games, escape mechanics, and dynamic events.

## Features

### 🏗️ **Automatic World Generation**
- Custom world generator creates a massive prison complex
- Multiple cell blocks (A, B, C, D) with individual cells
- Four different mines with varying ore types and payouts
- Central yard with basketball court and exercise area
- Administrative buildings and guard towers
- Perimeter walls with escape prevention
- Automatic NPC spawning with 40+ interactive characters

### 👥 **Player Management**
- Automatic player data persistence with YAML storage
- Sentence system (default 72 hours) with time reduction mechanics
- Play time tracking and reputation system
- Automatic cell assignment based on rank
- Advanced security levels and monitoring

### 🏆 **Ranking System**
- 7 ranks: D → C → B → A → S → TRUSTEE → GUARD
- Rank-based mine access and cell block assignment
- Progressive money requirements for ranking up
- Special privileges and access for higher ranks

### 💰 **Economy System**
- Mining-based income (break ores to earn money)
- Rank-up costs and progression
- Advanced shop system with multiple vendors
- Loan system with interest rates and penalties
- Contraband black market trading

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

### 🤖 **NPC System (Citizens Integration)**
- 40+ interactive NPCs including guards, staff, and prisoners
- Job managers for various prison work assignments
- Shop vendors with specialized inventories
- Special service NPCs (warden, lawyer, doctor, informant)
- Dynamic NPC spawning and management

### 🎮 **Mini-Games**
- Lockpicking challenges with timed mechanics
- Blackjack card games for entertainment
- Reaction-based skill tests
- Memory pattern games
- Rewards include money and reputation bonuses

### 🏃 **Advanced Escape System**
- Tool-based escape mechanics requiring specific items
- Multiple escape routes: tunnel, wall, gate, disguise
- Hidden tool spawning throughout the prison
- Success/failure consequences with manhunt system
- Reputation and rank affects escape chances

### 👔 **Job System**
- 5 different jobs: Kitchen, Laundry, Library, Janitor, Guard Assistant
- Timed work sessions with automatic pay
- Rank requirements for certain positions
- Job progression and skill development

### 👥 **Gang System**
- Player-created gangs with territories
- Gang warfare and influence mechanics
- Shared gang resources and money
- Territory control and expansion

### 📦 **Contraband System**
- Hidden contraband items and black market
- Suspicion levels and random searches
- Smuggling mechanics and penalties
- Risk/reward trading system

### 🎭 **Special Services**
- **Warden**: Appeals, transfers, complaints
- **Lawyer**: Sentence reduction, legal advice, parole
- **Doctor**: Medical services, performance enhancements
- **Informant**: Information trading and gang intelligence

### 🏛️ **Visitor System**
- Scheduled visits with family, lawyers, friends, business partners
- Visit benefits including money, reputation, and sentence reduction
- Visitor day events with special bonuses

### 📧 **Mail System**
- Send and receive letters between players
- System-generated mail from NPCs and administration
- Random mail delivery for immersion

### 🚨 **Security & Events**
- Advanced security system with cameras and metal detectors
- Dynamic prison events: lockdowns, riots, inspections
- Riot participation system with consequences
- Security level tracking and escalating punishments

### 📊 **Reputation System**
- Player reputation affects interactions and opportunities
- Reputation bonuses/penalties for various actions
- Reputation levels from "Enemy" to "Legend"

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
- `/gang` - Gang management menu
- `/job list` - Available jobs
- `/contraband` - Black market access
- `/minigame <type>` - Play mini-games
- `/loan` - Loan system
- `/escape plan` - Plan escape attempts
- `/visitor` - Schedule visits
- `/mail` - Mail system

### Admin Commands
- `/prisonadmin riot <start|end|status>` - Riot management
- `/prisonadmin event <type>` - Trigger events
- `/prisonadmin player <player> <action>` - Player management
- `/prisonadmin economy <player> <action> <amount>` - Economy management

## Permissions

- `prison.*` - All permissions
- `prison.admin` - Admin commands
- `prison.world` - World management
- `prison.cell` - Cell commands
- `prison.rank` - Rank commands
- `prison.mine` - Mine commands
- `prison.gang` - Gang system
- `prison.job` - Job system
- `prison.contraband` - Contraband trading
- `prison.minigame` - Mini-games
- `prison.loan` - Loan system
- `prison.escape` - Escape system
- `prison.visitor` - Visitor system
- `prison.mail` - Mail system

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
- Citizens plugin (for NPC functionality)
- Minecraft server with sufficient RAM for world generation

## Dependencies

This plugin requires the following dependencies:
- **Citizens** - For NPC creation and management
- **Paper API** - Core server functionality
- **Adventure API** - Modern text components and messaging

## Support

For issues, feature requests, or support, please create an issue on the GitHub repository.

---

**Prison NEO** - Experience the ultimate prison server gameplay!
