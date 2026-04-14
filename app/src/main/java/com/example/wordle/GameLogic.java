package com.example.wordle;

import android.content.SharedPreferences;
import android.content.Context;

import java.util.HashMap;

/**
 * Handles all Wordle game logic: letter input, guess validation,
 * color-checking algorithm, and statistics persistence.
 * Does NOT touch any UI elements — communicates via GameListener.
 */
public class GameLogic {

    public static final int COLOR_GREEN = 0;
    public static final int COLOR_YELLOW = 1;
    public static final int COLOR_GRAY = 2;

    private Context context;
    private GameListener listener;
    private HashMap<Character, Integer> keyColors = new HashMap<>();
    private String[][] grid;
    private String[] savedGuess, allWordsPossible;

    private int currentRow = 0,
            currentCol = 0;
    private final int maxRow = 6,
            maxCol = 5;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private String secretWord;
    private int maxStreak,
            currStreak,
            gamesPlayed,
            totalWins;

    /**
     * פעולה בונה למחלקת GameLogic. מאתחלת את הלוח, המשתנים והמבנים הנדרשים.
     * 
     * טענת כניסה: הקשר (Context), מאזין לאירועים (listener), מילה סודית (secretWord) ומערך מילים אפשריות.
     * טענת יציאה: נוצר אובייקט חדש, הלוח ואתחלו המונים ורשימות צבעי המקשים.
     */
    public GameLogic(Context context, GameListener listener,
                     String secretWord, String[] allWords) {
        this.context = context;
        this.listener = listener;
        this.secretWord = secretWord;
        this.allWordsPossible = allWords;

        this.grid = new String[maxRow][maxCol];
        for (int r = 0; r < maxRow; r++)
            for (int c = 0; c < maxCol; c++)
                grid[r][c] = "";

        this.savedGuess = new String[6];
        for(int i = 0; i < this.savedGuess.length; i++){
            this.savedGuess[i] = "";
        }
        this.maxStreak = 0;
        this.currStreak = 0;
        this.gamesPlayed = 0;
        this.totalWins = 0;
    }

    /**
     * מוסיפה אות ללוח המשחק בשורה הנוכחית.
     * 
     * טענת כניסה: מחרוזת המייצגת את האות שנוספה (letter).
     * טענת יציאה: האות מתווספת למטריצת הלוח במיקום המתאים, והמאזין מקבל הודעה על הוספת האות.
     */
    public void addLetter(String letter){
        if(gameOver) return;
        if(currentCol < maxCol){
            String upper = letter.toUpperCase();
            grid[currentRow][currentCol] = upper;
            listener.onLetterAdded(currentRow, currentCol, upper);
            currentCol++;
        }
    }

    /**
     * מוחקת את האות האחרונה שהוזנה בשורה הנוכחית.
     * 
     * טענת כניסה: אין (מתבצעת בדיקה אם יש אותיות למחוק).
     * טענת יציאה: האות האחרונה בשורה נמחקה מהמטריצה והמאזין מקבל הודעה על המחיקה.
     */
    public void deleteLetter(){
        if(gameOver) return;
        if(currentCol > 0){
            currentCol--;
            grid[currentRow][currentCol] = "";
            listener.onLetterDeleted(currentRow, currentCol);
        }
    }

    /**
     * בודקת האם מילה מסוימת קיימת במאגר המילים המותרות.
     * 
     * טענת כניסה: מחרוזת המייצגת את הניחוש (guess).
     * טענת יציאה: אמת (true) אם המילה קיימת במאגר, שקר (false) אחרת.
     */
    public boolean isInArray(String guess){
        for(int i = 0; i < this.allWordsPossible.length; i++){
            if(this.allWordsPossible[i].equalsIgnoreCase(guess)){
                return true;
            }
        }
        return false;
    }

    /**
     * מבצעת שליחה של המילה הנוכחית לבדיקה כנגד המילה הסודית.
     * 
     * טענת כניסה: אין (הפעולה בודקת אם הוזנה מילה מלאה).
     * טענת יציאה: אם המילה מלאה ותקנית, היא נבדקת. המשחק עשוי להסתיים (ניצחון/הפסד) או לעבור לשורה הבאה.
     */
    public void submitWord(){
        if(gameOver) return;
        if(currentCol < maxCol) return;

        StringBuilder guessBuilder = new StringBuilder();
        for(int i = 0; i < maxCol; i++){
            guessBuilder.append(grid[currentRow][i]);
        }
        String guess = guessBuilder.toString().toUpperCase();

        if(isInArray(guess)) {
            this.savedGuess[this.currentRow] = guess;
            checkGuess(guess);

            if(guess.equals(secretWord)) {
                gameOver = true;
                gameWon = true;
                handleGameEnd();
                listener.onGameWon();
            } else if(currentRow == 5) {
                gameOver = true;
                gameWon = false;
                handleGameEnd();
                listener.onGameLost(secretWord);
            } else {
                currentRow++;
                currentCol = 0;
            }
        }
        else{
            listener.onInvalidWord();
        }
    }

    /**
     * מעדכנת את הסטטיסטיקות בבסיס הנתונים בסיום המשחק.
     * 
     * טענת כניסה: אין (מסתמכת על מצב המשחק הנוכחי).
     * טענת יציאה: נתוני המשתמש (רצפים, אחוזי ניצחון וכו') עודכנו ב-Room Database וב-SharedPreferences.
     */
    private void handleGameEnd(){
        GuessDatabase guessDatabase = GuessDatabase.getInstance(context);
        GuessDAO guessDAO = guessDatabase.guessDao();
        //get the specific preferences file
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String username = prefs.getString("current_user", "");

        // Load existing stats so we accumulate instead of resetting
        Guess existing = guessDAO.getStats(username);
        if (existing != null) {
            this.gamesPlayed = existing.getGamesPlayed();
            this.totalWins = existing.getTotalWins();
            this.currStreak = existing.getCurrentStreak();
            this.maxStreak = existing.getMaxStreak();
        }

        // Apply the result of this game
        this.gamesPlayed++;
        if (gameWon) {
            this.totalWins++;
            this.currStreak++;
            if (this.maxStreak < this.currStreak) {
                this.maxStreak = this.currStreak;
            }
        } else {
            this.currStreak = 0;
        }

        Guess updatedStats = new Guess(username,
                this.currStreak,
                this.maxStreak,
                this.gamesPlayed,
                this.totalWins);

        if (existing != null) {
            guessDAO.update(updatedStats);
        } else {
            guessDAO.insert(updatedStats);
        }

        //remove the secret word so a new game can start
        editor.remove("secret_word");
        editor.apply();
    }


    /**
     * Checks the player's guess against the secret word.
     * Determines GREEN (correct position), YELLOW (wrong position),
     * or GRAY (not in word) for each letter, then notifies the listener.
     */
    /**
     * בודקת את הניחוש מול המילה הסודית וקובעת צבעים לכל אות.
     * 
     * טענת כניסה: מחרוזת של המילה המנוחשת (guess).
     * טענת יציאה: נקבעו צבעים (ירוק, צהוב, אפור) לכל אות, עודכנו צבעי המקלדת, והמאזין קיבל עדכון על התוצאות.
     */
    private void checkGuess(String guess){
        boolean[] used = new boolean[maxCol];
        int[] results = new int[maxCol];
        for (int i = 0; i < maxCol; i++) results[i] = -1;

        // Green letters — correct letter in correct position
        for(int i = 0; i < maxCol; i++){
            if(guess.charAt(i) == secretWord.charAt(i)){
                results[i] = COLOR_GREEN;
                updateKeyColor(guess.charAt(i), COLOR_GREEN);
                used[i] = true;
            }
        }

        // Yellow / Gray letters
        for(int i = 0; i < maxCol; i++){
            if(results[i] != COLOR_GREEN){
                boolean found = false;
                for(int j = 0; j < maxCol; j++){
                    if(!used[j] && guess.charAt(i) == secretWord.charAt(j)){
                        found = true;
                        used[j] = true;
                        break;
                    }
                }

                if(found){
                    results[i] = COLOR_YELLOW;
                    updateKeyColor(guess.charAt(i), COLOR_YELLOW);
                } else {
                    results[i] = COLOR_GRAY;
                    updateKeyColor(guess.charAt(i), COLOR_GRAY);
                }
            }
        }

        // Notify listener of all tile results
        for (int i = 0; i < maxCol; i++) {
            listener.onTileResult(currentRow, i, results[i]);
        }
    }

    /**
     * Updates the keyboard color for a letter, respecting priority:
     * GREEN > YELLOW > GRAY (a key never downgrades).
     */
    /**
     * מעדכנת את הצבע של מקש מסוים במקלדת הווירטואלית.
     * 
     * טענת כניסה: תו המייצג את האות (letter) וסוג הצבע (colorType).
     * טענת יציאה: הצבע של המקש עודכן במפה הפנימית (אם העדיפות גבוהה יותר) והמאזין קיבל עדכון.
     */
    private void updateKeyColor(char letter, int colorType) {
        letter = Character.toUpperCase(letter);
        //GREEN > YELLOW > GRAY
        if(!this.keyColors.containsKey(letter)){
            this.keyColors.put(letter, colorType);
        }
        else{
            int oldColor = this.keyColors.get(letter);
            if (oldColor == COLOR_GREEN) return;
            if (oldColor == COLOR_YELLOW && colorType == COLOR_GRAY) return;
            this.keyColors.put(letter, colorType);
        }
        listener.onKeyColorUpdate(letter, colorType);
    }

    /**
     * מחזירה את מספר השורה הנוכחית בלוח המשחק.
     * 
     * טענת כניסה: אין.
     * טענת יציאה: מספר השורה הנוכחית (בין 0 ל-5).
     */
    public int getCurrentRow() {
        return this.currentRow;
    }

    /**
     * בודקת האם המשחק הסתיים.
     * 
     * טענת כניסה: אין.
     * טענת יציאה: אמת (true) אם המשחק הסתיים, שקר (false) אחרת.
     */
    public boolean isGameOver(){
        return gameOver;
    }
}
