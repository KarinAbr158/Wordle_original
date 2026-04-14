package com.example.wordle;

/**
 * Callback interface for game events.
 * Separates game logic from UI — GameLogic calls these methods,
 * and the Activity handles how they are displayed.
 */
/**
 * ממשק (Interface) המשמש כ-Callback לאירועים במשחק.
 * מפריד בין לוגיקת המשחק לבין ממשק המשתמש.
 */
public interface GameListener {
    /**
     * נקראת כאשר אות מתווספת ללוח.
     * טענת כניסה: מספר שורה (row), מספר עמודה (col) והאות שנוספה (letter).
     * טענת יציאה: המאזין מעודכן לגבי האות החדשה בלוח.
     */
    void onLetterAdded(int row, int col, String letter);

    /**
     * נקראת כאשר אות נמחקת מהלוח.
     * טענת כניסה: מספר שורה (row) ומספר עמודה (col).
     * טענת יציאה: המאזין מעודכן לגבי מחיקת האות מהמיקום שצוין.
     */
    void onLetterDeleted(int row, int col);

    /**
     * נקראת לאחר בדיקת ניחוש, כדי לעדכן את צבע המשבצת.
     * טענת כניסה: מספר שורה (row), מספר עמודה (col) וסוג הצבע שנקבע (colorType).
     * טענת יציאה: המאזין מעודכן לגבי התוצאה של אות מסוימת בניחוש.
     */
    void onTileResult(int row, int col, int colorType);

    /**
     * נקראת כדי לעדכן את צבע המקשים במקלדת הווירטואלית.
     * טענת כניסה: התו של האות (letter) וסוג הצבע (colorType).
     * טענת יציאה: המאזין מעודכן לגבי הצבע החדש של המקש במקלדת.
     */
    void onKeyColorUpdate(char letter, int colorType);

    /**
     * נקראת כאשר המשתמש מנצח במשחק.
     * טענת כניסה: אין.
     * טענת יציאה: הודעה על ניצחון מועברת למאזין.
     */
    void onGameWon();

    /**
     * נקראת כאשר המשתמש מפסיד במשחק.
     * טענת כניסה: המילה הסודית (secretWord).
     * טענת יציאה: הודעה על הפסד וחשיפת המילה הסודית מועברות למאזין.
     */
    void onGameLost(String secretWord);

    /**
     * נקראת כאשר המשתמש מזין מילה שאינה קיימת במילון.
     * טענת כניסה: אין.
     * טענת יציאה: הודעה על מילה לא תקינה מועברת למאזין.
     */
    void onInvalidWord();
}
