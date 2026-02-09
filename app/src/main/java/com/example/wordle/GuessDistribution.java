package com.example.wordle;

/**
 * to hold guess distribution query results.
 * Maps the result of: SELECT guessIndex, COUNT(*) as count ... GROUP BY guessIndex
 */
public class GuessDistribution {
    public int guessIndex;  // The row the game was won on (1-6)
    public int count;       // How many wins on that row
}
