<div align="center">

# 🏛️ Prison NEO

**The Ultimate Minecraft Prison Server Plugin**

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg?style=for-the-badge)](https://github.com/SparkyOfficial/Prison-NEO/releases)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.8+-green.svg?style=for-the-badge)](https://papermc.io/)
[![Java](https://img.shields.io/badge/java-21+-orange.svg?style=for-the-badge)](https://adoptium.net/)
[![License](https://img.shields.io/badge/license-MIT-red.svg?style=for-the-badge)](LICENSE)

[![Downloads](https://img.shields.io/github/downloads/SparkyOfficial/Prison-NEO/total?style=for-the-badge&color=blueviolet)](https://github.com/SparkyOfficial/Prison-NEO/releases)
[![Stars](https://img.shields.io/github/stars/SparkyOfficial/Prison-NEO?style=for-the-badge&color=yellow)](https://github.com/SparkyOfficial/Prison-NEO/stargazers)
[![Issues](https://img.shields.io/github/issues/SparkyOfficial/Prison-NEO?style=for-the-badge&color=red)](https://github.com/SparkyOfficial/Prison-NEO/issues)

[🚀 **Download**](#-installation) • [📖 **Documentation**](#-features) • [💬 **Discord**](#-support) • [🐛 **Report Bug**](#-support)

---

*Experience the most immersive and feature-rich prison server plugin for Minecraft with automatic world generation, advanced NPC systems, dynamic events, and realistic prison gameplay mechanics.*

</div>

## 🌟 Features

<div align="center">

**🏛️ 50+ Advanced Features • 🎮 15+ Mini-Games • 🤖 40+ Interactive NPCs • 💰 Complete Economy System**

</div>

<table>
<tr>
<td width="50%">

### 🏗️ **World Generation**
- **Custom Prison Complex** - Massive auto-generated world
- **Multiple Cell Blocks** - A, B, C, D with individual cells  
- **Four Unique Mines** - Coal, Iron, Gold, Diamond mines
- **Central Yard** - Basketball court & exercise areas
- **Guard Towers** - Security monitoring points
- **Escape-Proof Walls** - Advanced perimeter security

### 👥 **Player Management** 
- **Persistent Data** - YAML-based player storage
- **Sentence System** - Time-based imprisonment (72h default)
- **Reputation Tracking** - Dynamic reputation system
- **Auto Cell Assignment** - Rank-based allocation
- **Security Monitoring** - Advanced tracking systems

### 🏆 **Ranking System**
- **7 Progressive Ranks** - D → C → B → A → S → TRUSTEE → GUARD
- **Rank-Based Access** - Mine and cell block restrictions
- **Money Requirements** - Progressive upgrade costs
- **Special Privileges** - Higher rank benefits

### 💰 **Economy System**
- **Mining Income** - Break ores to earn money
- **Advanced Shops** - Multiple specialized vendors
- **Loan System** - Interest rates and penalties
- **Contraband Trading** - Black market economics
- **Rank Progression** - Money-based advancement

</td>
<td width="50%">

### ⛏️ **Mining System**
- **Mine A** - Coal (Rank D, $1.0/block)
- **Mine B** - Iron (Rank C, $2.5/block)  
- **Mine C** - Gold (Rank B, $5.0/block)
- **Mine D** - Diamond (Rank A, $10.0/block)
- **Auto Regeneration** - 10-minute reset cycles
- **Individual Respawn** - Block-by-block restoration

### 🤖 **NPC System**
- **40+ Interactive NPCs** - Guards, staff, prisoners
- **Job Managers** - Work assignment coordinators
- **Shop Vendors** - Specialized inventories
- **Special Services** - Warden, lawyer, doctor, informant
- **Dynamic Spawning** - Automatic NPC management

### 🎮 **Mini-Games & Activities**
- **Lockpicking** - Timed skill challenges
- **Blackjack** - Card game entertainment
- **Reaction Tests** - Quick reflex games
- **Memory Patterns** - Cognitive challenges
- **Workshop Crafting** - Item creation system
- **Library Reading** - Educational benefits

### 🏃 **Advanced Systems**
- **Escape Mechanics** - Tool-based breakouts
- **Gang Warfare** - Territory control & influence
- **Contraband Trading** - Risk/reward smuggling
- **Visitor System** - Family & lawyer visits
- **Mail System** - Inter-player communication
- **Event System** - Riots, lockdowns, inspections

</td>
</tr>
</table>

## 📥 Installation

<div align="center">

### 🚀 Quick Start Guide

</div>

### 📋 Requirements
- **Server**: Paper/Spigot 1.21.8+
- **Java**: Version 21 or higher
- **RAM**: Minimum 2GB recommended
- **Dependencies**: Citizens (optional for NPCs)

### ⚡ Installation Steps

```bash
# 1. Download the latest release
wget https://github.com/SparkyOfficial/Prison-NEO/releases/latest/download/PrisonNEO.jar

# 2. Place in plugins folder
mv PrisonNEO.jar /path/to/your/server/plugins/

# 3. Start your server
./start.sh

# 4. Create the prison world (in-game)
/prisonworld create
```

### 🔧 Optional Dependencies

| Plugin | Purpose | Download |
|--------|---------|----------|
| **Citizens** | NPC functionality | [Download](https://www.spigotmc.org/resources/citizens.13811/) |
| **Vault** | Economy integration | [Download](https://www.spigotmc.org/resources/vault.34315/) |

## 🎮 Commands & Usage

<details>
<summary><b>👤 Player Commands</b></summary>

| Command | Description | Aliases |
|---------|-------------|---------|
| `/prison info` | View your prison status and statistics | `/p info` |
| `/cell assign` | Get assigned to a prison cell | `/камера назначить` |
| `/cell tp` | Teleport to your assigned cell | `/камера тп` |
| `/rank up` | Upgrade your prison rank (costs money) | `/ранг повысить` |
| `/rank info` | View rank information and requirements | `/ранг инфо` |
| `/mine tp <A\|B\|C\|D>` | Teleport to specified mine | `/шахта тп` |
| `/mine list` | List all available mines and requirements | `/шахта список` |
| `/gang create <name>` | Create a new gang | `/банда создать` |
| `/gang join <name>` | Join an existing gang | `/банда войти` |
| `/job list` | View available prison jobs | `/работа список` |
| `/job work <type>` | Start working at a job | `/работа начать` |
| `/contraband` | Access black market trading | `/контрабанда` |
| `/minigame <type>` | Play prison mini-games | `/игры` |
| `/loan request <amount>` | Request a loan from prison bank | `/кредит запросить` |
| `/escape plan` | Plan and execute escape attempts | `/побег план` |
| `/visitor schedule` | Schedule visits with family/lawyers | `/посетители` |
| `/mail send <player> <message>` | Send mail to another player | `/почта отправить` |
| `/workshop` | Access crafting workshop | `/мастерская` |
| `/library` | Visit prison library | `/библиотека` |
| `/achievement` | View your achievements | `/достижения` |
| `/schedule` | View daily prison schedule | `/расписание` |

</details>

<details>
<summary><b>👮 Admin Commands</b></summary>

| Command | Description |
|---------|-------------|
| `/prisonworld create` | Generate the prison world |
| `/prisonworld reset` | Reset and regenerate world |
| `/prisonworld tp` | Teleport to prison spawn |
| `/prisonadmin riot start` | Start a prison riot event |
| `/prisonadmin riot stop` | End current riot |
| `/prisonadmin lockdown start` | Initiate prison lockdown |
| `/prisonadmin lockdown stop` | End lockdown |
| `/prisonadmin player <player> free` | Release player from prison |
| `/prisonadmin player <player> sentence <time>` | Set player sentence time |
| `/prisonadmin economy <player> add <amount>` | Add money to player |
| `/prisonadmin economy <player> remove <amount>` | Remove money from player |
| `/prisonadmin event trigger <type>` | Trigger random events |
| `/prisonadmin reload` | Reload plugin configuration |

</details>

## 🔐 Permissions

<details>
<summary><b>🎯 Player Permissions</b></summary>

| Permission | Description | Default |
|------------|-------------|---------|
| `prison.player` | Basic prison access | `true` |
| `prison.cell` | Cell management commands | `true` |
| `prison.mine` | Mine access and teleportation | `true` |
| `prison.rank` | Rank progression commands | `true` |
| `prison.job` | Job system access | `true` |
| `prison.gang` | Gang system participation | `true` |
| `prison.contraband` | Contraband trading access | `true` |
| `prison.minigame` | Mini-game participation | `true` |
| `prison.loan` | Banking and loan system | `true` |
| `prison.escape` | Escape planning and execution | `true` |
| `prison.visitor` | Visitor scheduling system | `true` |
| `prison.mail` | Mail system usage | `true` |
| `prison.workshop` | Workshop and crafting access | `true` |
| `prison.library` | Library system access | `true` |
| `prison.achievement` | Achievement viewing | `true` |
| `prison.schedule` | Schedule viewing | `true` |

</details>

<details>
<summary><b>👮 Admin Permissions</b></summary>

| Permission | Description |
|------------|-------------|
| `prison.admin` | Full administrative access |
| `prison.world` | World generation and management |
| `prison.bypass` | Bypass all restrictions |
| `prison.reload` | Plugin reload access |
| `prison.event` | Event management |
| `prison.economy.admin` | Economy management |
| `prison.player.admin` | Player data management |

</details>

## ⚙️ Configuration

<details>
<summary><b>📁 Configuration Files</b></summary>

| File | Purpose | Description |
|------|---------|-------------|
| `config.yml` | Main configuration | Core plugin settings and world parameters |
| `players.yml` | Player data | Persistent player information and statistics |
| `cells.yml` | Cell management | Cell assignments and configurations |
| `mines.yml` | Mine settings | Mine locations, ores, and reset timers |
| `gangs.yml` | Gang data | Gang information and territory data |
| `messages.yml` | Localization | Customizable messages in multiple languages |

</details>

<details>
<summary><b>🔧 Key Configuration Options</b></summary>

```yaml
# World Generation
world:
  name: "prison_world"
  auto-generate: true
  cell-blocks: 4
  mines: 4

# Economy Settings
economy:
  starting-money: 100.0
  rank-costs:
    C: 1000.0
    B: 5000.0
    A: 15000.0
    S: 50000.0

# Mine Configuration
mines:
  reset-interval: 600 # 10 minutes
  regeneration-delay: 30 # 30 seconds per block

# Player Settings
player:
  default-sentence: 4320 # 72 hours in minutes
  auto-cell-assign: true
  starting-rank: "D"
```

</details>

## 🛠️ Development

### Building from Source

```bash
# Clone the repository
git clone https://github.com/SparkyOfficial/Prison-NEO.git
cd Prison-NEO

# Build with Maven
mvn clean package

# JAR file will be in target/ directory
ls target/PrisonNEO-*.jar
```

### 🔌 API Usage

```java
// Get plugin instance
PrisonNEO plugin = PrisonNEO.getInstance();

// Access managers
PlayerManager playerManager = plugin.getPlayerManager();
EconomyManager economyManager = plugin.getEconomyManager();
RankManager rankManager = plugin.getRankManager();

// Work with prison players
PrisonPlayer prisonPlayer = playerManager.getPrisonPlayer(player);
prisonPlayer.addMoney(100.0);
prisonPlayer.setRank("C");

// Trigger events
plugin.getEventManager().startLockdown();
plugin.getRiotManager().startRiot();
```

---

## 🤝 Support & Community

<div align="center">

[![Discord](https://img.shields.io/discord/123456789?color=7289da&label=Discord&logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/prisonneo)
[![GitHub Issues](https://img.shields.io/github/issues/SparkyOfficial/Prison-NEO?style=for-the-badge&color=red)](https://github.com/SparkyOfficial/Prison-NEO/issues)
[![Documentation](https://img.shields.io/badge/docs-wiki-blue?style=for-the-badge)](https://github.com/SparkyOfficial/Prison-NEO/wiki)

</div>

### 🆘 Getting Help

- **🐛 Bug Reports**: [Create an issue](https://github.com/SparkyOfficial/Prison-NEO/issues/new?template=bug_report.md)
- **💡 Feature Requests**: [Request a feature](https://github.com/SparkyOfficial/Prison-NEO/issues/new?template=feature_request.md)
- **💬 Community Support**: Join our [Discord server](https://discord.gg/prisonneo)
- **📖 Documentation**: Check the [Wiki](https://github.com/SparkyOfficial/Prison-NEO/wiki)

### 🌟 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

---

## 📈 Statistics & Roadmap

<div align="center">

![GitHub stars](https://img.shields.io/github/stars/SparkyOfficial/Prison-NEO?style=social)
![GitHub forks](https://img.shields.io/github/forks/SparkyOfficial/Prison-NEO?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/SparkyOfficial/Prison-NEO?style=social)

**50+ Features • 20+ Commands • 15+ Managers • 40+ NPCs**

</div>

### 🎯 Roadmap

- [ ] **v1.1.0** - PvP Arena System & Combat Mechanics
- [ ] **v1.2.0** - Discord Integration & Webhooks
- [ ] **v1.3.0** - Quest System & Daily Challenges
- [ ] **v1.4.0** - Advanced GUI Overhaul
- [ ] **v2.0.0** - Multi-Prison Support & Network Features

---

## 🏆 Credits & Acknowledgments

<div align="center">

**Developed with ❤️ by the Prison NEO Team**

[![Contributors](https://contrib.rocks/image?repo=SparkyOfficial/Prison-NEO)](https://github.com/SparkyOfficial/Prison-NEO/graphs/contributors)

### Special Thanks

- **Citizens Team** - For the amazing NPC API
- **Paper Team** - For the excellent server software
- **Minecraft Community** - For inspiration and feedback

</div>

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**⭐ Star this repository if you find it useful!**

[![GitHub stars](https://img.shields.io/github/stars/SparkyOfficial/Prison-NEO?style=social)](https://github.com/SparkyOfficial/Prison-NEO/stargazers)

[🔝 Back to Top](#️-prison-neo)

---

*Prison NEO - The Ultimate Minecraft Prison Server Experience*

</div>
