![SmartInvs Logo](http://minuskube.fr/img/smart-invs/smart_invs.png)

[![License](https://img.shields.io/github/license/minuskube/smartinvs.svg?style=flat-square)](https://github.com/MinusKube/SmartInvs/blob/master/LICENSE.md)
[![Javadocs](https://img.shields.io/maven-central/v/fr.minuskube.inv/smart-invs.svg?label=javadoc&style=flat-square)](https://javadoc.io/doc/fr.minuskube.inv/smart-invs)

# SmartInvs
Advanced Inventory API for your Minecraft Bukkit plugins.

*Tested Minecraft versions: 1.7, 1.8, 1.9, 1.10, 1.11, 1.12, 1.13, 1.14*  
**You can use this as a Plugin, or use it as a library** (see [the docs](https://minuskube.gitbooks.io/smartinvs/))

## Features
* Inventories of any type (workbench, chest, furnace, ...)
* Customizable size when possible (chest, ...)
* Custom titles
* Allows to prevent the player from closing its inventory
* Custom listeners for the event related to the inventory
* Iterator for inventory slots
* Page system
* Util methods to fill an inventory's row/column/borders/...
* Actions when player clicks on an item
* Update methods to edit the content of the inventory every tick

## Docs
[Click here to read the docs on Gitbooks](https://minuskube.gitbooks.io/smartinvs/)

## Usage
To use the SmartInvs API, either:
- Put it in the `plugins` folder of your server, add it to your dependencies in your plugin.yml (e.g. `depend: [SmartInvs]`) and add it to the dependencies in your IDE.
- Put it inside your plugin jar, initialize an `InventoryManager` in your plugin (don't forget to call the `init()` method), and add a `.manager(invManager)` to your SmartInventory Builders.

You can download the latest version on the [Releases page](https://github.com/MinusKube/SmartInvs/releases) on Github.

You can also use a build system:
### Gradle
```gradle
repositories {
    mavenCentral()
}

dependencies {
    compile 'fr.minuskube.inv:smart-invs:1.3.0'
}
```

### Maven
```xml
<dependency>
  <groupId>fr.minuskube.inv</groupId>
  <artifactId>smart-invs</artifactId>
  <version>1.3.0</version>
</dependency>
```

## TODO
* Add some Javadocs

## Issues
If you have a problem with the API, or you want to request a feature, make an issue [here](https://github.com/MinusKube/SmartInvs/issues).
