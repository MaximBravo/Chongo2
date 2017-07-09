package com.maximbravo.chongo2;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Maxim Bravo on 7/3/2017.
 */
public class Utils {
    public static ArrayList<Word> hsk1;
    public static ArrayList<Word> hsk2;
    public static ArrayList<Word> hsk3;
    public static int frequency = 1;
    public static String title = "么";
    public static int level = 1;
    public static String pinyin = "me";
    public static String definition = "what; particle for questions; question particle";
    public static Context context;

    public static int selectedRadio;
    public static boolean hsk1pref = false;
    public static boolean hsk2pref = false;
    public static boolean hsk3pref = false;
    public static void print(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void extractAll(){
        hsk1 = new ArrayList<>();
        hsk2 = new ArrayList<>();
        hsk3 = new ArrayList<>();
        InputStream is = context.getResources().openRawResource(R.raw.hsksmall);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");
                Word word = new Word(rowData[0], rowData[1], rowData[2], rowData[3]);
                if(rowData[3].equals("1")) {
                    hsk1.add(word);
                } else if (rowData[3].equals("2")) {
                    hsk2.add(word);
                } else {
                    hsk3.add(word);
                }
            }
        }
        catch (IOException ex) {
            // handle exception
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                // handle exception
            }
        }
    }

    public static void updateVariables() {
        ArrayList<Word> pool = new ArrayList<>();
        if(hsk1pref) {
            pool.add(getRandom(hsk1));
        }
        if (hsk2pref) {
            pool.add(getRandom(hsk2));
        }
        if(hsk3pref) {
            pool.add(getRandom(hsk3));
        }
        if(pool.size() != 0) {
            Word randomWord = getRandom(pool);
            title = randomWord.getCharacter();
            pinyin = randomWord.getPinyin();
            definition = randomWord.getDefinition();
            level = randomWord.getLevel();
        }
    }

    public static Word getRandom(ArrayList<Word> list) {
        if(list != null && list.size() != 0) {
            Random rand = new Random();

            int n = rand.nextInt(list.size()-1);
            return list.get(n);
        }
        return new Word("Something", "Went", "wrong in getRandom method of Utils class", "21");
    }
}
