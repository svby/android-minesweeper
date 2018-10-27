# android-minesweeper

Minesweeper clone written with Kotlin 1.3 for Android.

## Features

 - Matrices are represented internally as `byte[]`s (bitsets) to save memory with large mine fields.
 - "Safe first click" option
   - Inspired by classic Minesweeper, when this option is active you cannot lose before the second move
 - Configurability (custom board sizes, mine counts)
 - Uses API level 16, supported by 99.5% of Android devices
 - Extensibility
   - Custom moves can be defined
   - Custom board generators can be defined
     - The default places mines randomly, which is not guaranteed to be solvable without guessing
   - `Board` supports undo operations as it stores moves in a stack

## Controls

 - Tap a field to reveal it (and potentially lose)
 - Long press on a field to flag it
   - Flagged fields cannot be revealed
   
If you reveal a mine, you lose.

When all safe fields are revealed, you win!

## Screenshots

![](https://i.imgur.com/XfctH4s.png)
![](https://i.imgur.com/1kpiwnS.png)