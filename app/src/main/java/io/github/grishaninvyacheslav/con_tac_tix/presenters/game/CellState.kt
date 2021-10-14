package io.github.grishaninvyacheslav.con_tac_tix.presenters.game

enum class CellState {
    FREE,                       // 0
    NOT_ROOTED_FIRST_PLAYER,    // 1
    NOT_ROOTED_SECOND_PLAYER,   // 2
    BOTTOM_ROOTED_FIRST_PLAYER, // 3
    TOP_ROOTED_FIRST_PLAYER,    // 4
    LEFT_ROOTED_SECOND_PLAYER,  // 5
    RIGHT_ROOTED_SECOND_PLAYER, // 6
}