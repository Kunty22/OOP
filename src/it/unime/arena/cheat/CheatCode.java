package it.unime.arena.cheat;

/** All available cheat codes. Each carries the string the user must type. */
public enum CheatCode {
    GODMODE   ("godmode"),
    ONEHITKILL("kill"),
    SPEEDMODE ("speed");

    private final String code;
    CheatCode(String code) { this.code = code; }
    public String getCode() { return code; }
}
