![SmartInvs Logo](http://minuskube.fr/img/smart-invs/smart_invs.png)

# SmartInvs
Advanced Inventory API for your Minecraft Bukkit plugins.

*Tested Minecraft versions: 1.7, 1.8, 1.9, 1.10, 1.11, 1.12*  
**You must use this as a Plugin.**

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
To use the SmartInvs API, put it in the `plugins` folder of your server, add it to your dependencies in your plugin.yml (e.g. `depend: [SmartInvs]`) and add it to the dependencies in your IDE.

You can download the latest version on the [Releases page](https://github.com/MinusKube/SmartInvs/releases) on Github.

You can also use a build system:
### Gradle
```gradle
repositories {
    mavenCentral()
}

dependencies {
    compile 'fr.minuskube.inv:smart-invs:1.1.3'
}
```

### Maven
```xml
<dependency>
  <groupId>fr.minuskube.inv</groupId>
  <artifactId>smart-invs</artifactId>
  <version>1.1.3</version>
</dependency>
```

## TODO
* Add some Javadocs

## Issues
If you have a problem with the API, or you want to request a feature, make an issue [here](https://github.com/MinusKube/SmartInvs/issues).
