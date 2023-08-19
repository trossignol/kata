package fr.rossi.belote.utils;

public enum TextColor {

    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    BLUE("\u001B[34m"),
    YELLOW("\u001B[33m");

    private final String ansiCode;

    TextColor(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    public static String colorEmoji(String text) {
        return String.format("%s%s", text, "️\uFE0F");
    }

    public String to(String text) {
        return String.format("%s%s%s", this.ansiCode, text, RESET.ansiCode);
    }
}
