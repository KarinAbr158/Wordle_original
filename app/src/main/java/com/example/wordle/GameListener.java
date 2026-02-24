package com.example.wordle;

/**
 * Callback interface for game events.
 * Separates game logic from UI — GameLogic calls these methods,
 * and the Activity handles how they are displayed.
 */
public interface GameListener {
    void onLetterAdded(int row, int col, String letter);
    void onLetterDeleted(int row, int col);
    void onTileResult(int row, int col, int colorType);
    void onKeyColorUpdate(char letter, int colorType);
    void onGameWon();
    void onGameLost(String secretWord);
    void onInvalidWord();
}
