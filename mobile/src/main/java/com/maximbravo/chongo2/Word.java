package com.maximbravo.chongo2;

/**
 * Created by Maxim Bravo on 7/3/2017.
 */

public class Word {
    private String character;
    private String pinyin;
    private String definition;
    private int level;
    public Word(String character, String pinyin, String def, String level) {
        this.character = character;
        this.pinyin = pinyin;
        this.definition = def;
        this.level = Integer.parseInt(level);
    }

    public String getCharacter() {
        return character;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getDefinition() {
        return definition;
    }

    public int getLevel() {
        return level;
    }
}

