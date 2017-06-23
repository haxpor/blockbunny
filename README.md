<a href="https://github.com/haxpor/donate"><img src="https://img.shields.io/badge/$-donate-ff69b4.svg?maxAge=2592000&amp;style=flat" alt="donate"></a>

# blockbunny

Libgdx-based game for Android, iOS, and PC following the tutorial by [ForeignGuyMike](https://www.youtube.com/channel/UC_IV37n-uBpRp64hQIwywWQ) on youtube channel starting at [this](https://www.youtube.com/watch?v=85A1w1iD2oA) but port to Kotlin with improvements, controller support, and mobile ready (iOS via MOE).

# Overview

This project follows along with tutorial but code written in Kotlin `1.1.2-3` on Android Studio `2.3.2` with Gradle `2.14.1`.

The assets and idea follows the original project which written in Java on Eclipse; originally based on desktop only, this project will make it buildable and runnable on Android, iOS (via multi-OS engine), and PC.

# Changes

* Ported to Kotlin; initially closely following tutorial but latter slightly diverse
* Added controller (mainly tested with Xbox360 controller) support across the game
* Added 4-directional selection for level in level selection screen
* Added save file to keep track of player's progress in JSON format (intentionally without encryption)
* iOS buildable and runnable on simulator and real device with Multi-OS engine version `1.3.6`.
* Different level from original
* Support wide-screen in gameplay session
* Optimized creation process for tiles, only a single layer and checking against tile's ID

# What It Looks Like

![Blockbunny in action 1](http://i.imgur.com/05P8lh8.gif)

![Blockbunny in action 2](http://i.imgur.com/k98jwnl.gif)

![Blockbunny with controller](http://i.imgur.com/tJYqnam.gif)

# What's Next?

* Add remaining level of 13-15 (as of now it's just a copied of level 12 to prevent crash if select on such levels)

# Button Control

## Desktop (without controller)

* `Z` for jumping or enter
* `X` for switch block
* `Left`, `Right`, `Up`, `Down` for selecting which level to play in level selection screen

## Desktop with controller

This is Xbox360 layout, currently the game has no configuration screen to map button to other controllers' layout.

* `A` for jumping or enter
* `B` for switching block
* `D-Pad` for selecting which level to play in level selection screen

## Mobile (Android and iOS)

### Gameplay screen

* Touch on left side of the screen to jump
* Touch on right side of the screen to switch block

### Other screens

* Touch to enter
* Touch to select which level to play in level selection screen

# Credits

Big shout out to *ForeignGuyMike* for making a great tutorial video that this project builds upon on top of that.

# License

[MIT](https://github.com/haxpor/blockbunny/blob/master/LICENSE), Wasin Thonkaew
