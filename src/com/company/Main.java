package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

public class Main {

    private static Set<String> words_dict = new HashSet<String>();

    /**
     * Creating names automatically
     * The word will consist of the probability of each letter given the letter that comes before it
     * For the first letter, we select the letter with the highest probability of all the first letters of all the names in the map
     */
    public static void GenerateName(){
        Map<String, Integer> dict = word2dic(2, true);
        int lengthWord = 10;
        StringBuilder returnWord = new StringBuilder();
        Map<String, Integer> grams;
        for(int i=0;i<lengthWord; i++){
            if(i==0) {
                grams = new HashMap<String, Integer>();
                for (String str : words_dict) {
                    String letter = str.charAt(0) + "";
                    if (grams.containsKey(letter)) {
                        grams.put(letter, grams.get(letter) + 1);
                    } else {
                        grams.put(letter, 1);
                    }
                }
                returnWord.append(returnStringWithMaxValue(grams).get(0));
            }else{
                grams = new HashMap<String, Integer>();
                for(String nGram:dict.keySet()){
                    if(nGram.startsWith(returnWord.charAt(returnWord.length()-1)+"")){
                        grams.put(nGram, dict.get(nGram));
                    }
                }
                List<String> maxNGrams = returnStringWithMaxValue(grams);
                returnWord.append(maxNGrams.get(0).charAt(1));
            }
        }
        System.out.println(returnWord);
    }

    /**
     * Print all the strings from dictionary which are substring of the given string
     * @param checkWord - Given string
     */
    public static void AllIncludesString(String checkWord){
        for(String str: words_dict){
            if(checkWord.toLowerCase().contains(str.toLowerCase())){
                System.out.println(str);
            }
        }
    }

    /**
     * Print n-grams with the maximum appearances
     * @param length n-grams length
     */
    public static void CountMaxString(int length){
        List<String>listMaxWords = returnStringWithMaxValue(word2dic(length, false));
        for (String maxWords: listMaxWords) {
            System.out.println(maxWords);
        }
    }

    /**
     * Print all n-grams with the given length and the number of appearances
     * @param length n-grams length
     */
    public static void CountAllStrings(int length){
        Map<String, Integer> dict = word2dic(length, true);
        for(String word : dict.keySet()){
            System.out.println(word+":"+dict.get(word));
        }
    }

    /**
     * Create Map with n-grams and count of appearances
     * @param length n-grams length
     * @param caseSensitive should ignore uppercase and lowercase
     * @return map with n-grams and values
     */
    private static Map<String, Integer> word2dic(int length, boolean caseSensitive){
        Map<String, Integer> gramsDict = new HashMap<String,Integer>();
        for(String str: words_dict){
            for(int i=0;i+length-1<str.length();i++){
                StringBuilder gram = new StringBuilder();
                int counter =0;
                while (counter<length && counter+i <str.length()){
                    gram.append(str.charAt(i + counter));
                    counter++;
                }
                String checkWord = caseSensitive ? gram.toString() : gram.toString().toLowerCase();
                if(gramsDict.containsKey(checkWord)){
                    gramsDict.put(checkWord, gramsDict.get(checkWord)+1);
                }else{
                    gramsDict.put(checkWord, 1);
                }
            }
        }
        return gramsDict;
    }

    /**
     * Print amount of string which contain the given string (count just 1 each string)
     * Counts and print the occurrences of a particular string in all names
     * @param checkWord - given string
     */
    public static void CountSpecificString(String checkWord){
        int contains = 0;
        for(String str: words_dict){
            if(str.contains(checkWord)){
                contains++;
            }
        }
        System.out.println(contains);
    }

    /**
     * Read and parse all the words_dict from the website
     * include only words_dict with english character
     */
    public static void readWords(){
        String url = "https://www.behindthename.com/names/usage/english/";
        for(int i=1; i<=14; i++){
            try{
                Document doc = Jsoup.connect(url+i).get();
                Elements allWordsPerPage = doc.select("span.listname");
                if (allWordsPerPage.size() == 0) break;
                for (Element word:allWordsPerPage) {
                    String name = word.text();
                    // remove all the substring "(i)" from words_dict if exists
                    if(name.contains("(")){
                        name = name.substring(0,name.indexOf('(')-1);
                    }
                    // include only words_dict with character a to z and A to Z and space
                    if(name.matches("[ a-zA-Z]+")) {
                        name = name.toLowerCase();
                        // UpperCase the first character only
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                        if(name.contains(" ")){
                            String[] wordsWithSpace = name.split(" ");
                            // UpperCase the first character from the second word
                            wordsWithSpace[1] = wordsWithSpace[1].substring(0, 1).toUpperCase() + wordsWithSpace[1].substring(1);
                            name = wordsWithSpace[0] +" "+ wordsWithSpace[1];
                        }
                        words_dict.add(name);
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Search and count the string in the map with the maximum value
     * @param map - given map
     * @return List of strings
     */
    private static List<String> returnStringWithMaxValue(Map<String,Integer>map){
        List<String>maxWord = new ArrayList<String>();
        int maxCounter = 0;
        for (String word:map.keySet() ) {
            if(map.get(word) > maxCounter){
                maxWord = new ArrayList<String>();
                maxWord.add(word);
                maxCounter = map.get(word);
            }else if(map.get(word) == maxCounter){
                maxWord.add(word);
            }
        }
        return maxWord;
    }

    public static void main(String[] args) {
        readWords();
        if (args[0].equals("CountSpecificString")) {
            CountSpecificString(args[1]);
        }
        if (args[0].equals("CountAllStrings")){
            CountAllStrings(Integer.parseInt(args[1]));
        }
        if(args[0].equals("CountMaxString")){
            CountMaxString(Integer.parseInt(args[1]));
        }
        if(args[0].equals("AllIncludesString")){
            AllIncludesString(args[1]);
        }
        if(args[0].equals("GenerateName")){
            GenerateName();
        }
    }
}