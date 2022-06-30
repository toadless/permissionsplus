![Logo](https://i.imgur.com/Hx26798.png)

# Permissions Plus
[![build](https://github.com/toadless/permissionsplus/actions/workflows/build.yml/badge.svg)](https://github.com/toadless/permissionsplus/actions/workflows/build.yml)
[![Discord](https://img.shields.io/discord/954456505334263828.svg?label=discord&logo=discord)](https://discord.gg/8Nkgxg25Xw)

PermissionsPlus is an PostgreSQL based permissions plugin built for [PaperMC](https://papermc.io/) servers.
It is built around groups, where you can control what permissions each group has and assign
them to players.

The latest released of PermissionsPlus can be found [here](https://github.com/toadless/permissionsplus/releases)!

# Building From Source
PermissionsPlus uses Gradle to handle dependencies and building! Make sure you have Java 8 and Git, and then run:

```
git clone https://github.com/toadless/permissionsplus.git
cd permissionsplus/
./gradlew shadowJar
```

**Note: You will need to create the gradle.properties file to compile!**

# Configuration
Configuration is pretty straight forward, just read the comments in the example config file that will
be automatically generated.

# Database
PermissionsPlus required a PostgreSQL database and currently does not support local storage. If
you cant run a PostgreSQL database then please dont use this plugin.

# License
PermissionsPlus is licensed under the MIT license. View [`LICENSE`](https://github.com/toadless/permissionsplus/blob/main/LICENSE) for more information.
