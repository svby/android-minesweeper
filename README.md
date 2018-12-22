# android-minesweeper

Minesweeper clone written with Kotlin 1.3 for Android.

## Features

 - Move stack allows undoing moves
 - Custom view allows efficient rendering and scrolling, compared to `GridView`
   - Board view supports scroll and fling gestures in any direction
 - "Safe first click" option
   - Inspired by classic Minesweeper, when this option is active you cannot lose before the second move
 - Configurability (custom board sizes, mine counts)
 
## Implementation

 - Uses API level 18 and Android Jetpack extensions
 - Makes use of the AndroidX support library wherever possible
   - Uses the navigation architecture component to allow easy navigation between fragments
 - Minefield and game state are represented as `byte[]`s to save memory with large minefields.
 - `Field` represents only a minefield while a `Board` holds a `Field` and additionally stores the current game state.
 It is resettable and restartable.
 - Highly extensible
   - Custom moves can be defined
     - Custom board generators can be defined
       - The default places mines randomly, which is not guaranteed to be solvable without guessing
     - Board stores moves in a stack

## Controls

 - Tap a field to reveal it (and potentially lose)
 - Long press on a field to flag it
   - Flagged fields cannot be revealed
 - Double-tap on a revealed field to reveal all adjacent fields

If you reveal a mine, you lose.

When all safe fields are revealed, you win!

## Screenshots

![](https://i.imgur.com/wKYUtmQ.png)
![](https://i.imgur.com/EXRAVLV.png)
![](https://i.imgur.com/xoa8KP8.png)
![](https://i.imgur.com/4kUnLjP.png)