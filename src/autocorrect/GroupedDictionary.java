package autocorrect;

import resources.ResourceLoader;
import java.util.*;

public class GroupedDictionary {
    //Object for the words to be organized by first character
    private LinkedHashMap<Character, LinkedHashMap<Integer, String>> dictionary;
    //Create metaphone3 object
    private Metaphone3 metaphone3 = new Metaphone3();


    //Compiles this dictionary from given filename
    public GroupedDictionary(String filename){
        this.dictionary = compileDictionary(filename);
    }


    //Gets the grouped dictionary linked map
    public LinkedHashMap<Character, LinkedHashMap<Integer, String>> getDictionary() {
        return dictionary;
    }


    //Check to see if word is contained in dictionary
    public boolean isInDictionary(String word){
        return dictionary.get(word.charAt(0)).containsValue(word);
    }


    //Populate dictionaries with words in groupings
    private LinkedHashMap<Character, LinkedHashMap<Integer, String>> compileDictionary(String filename){
            //Create list from given dictionary file
            List<String> fileData = ResourceLoader.loadDictionary(filename);
            //Create hash map object that will be returned
            LinkedHashMap<Character, LinkedHashMap<Integer, String>> map = new LinkedHashMap<>();
            //Loop through each word and add it within its given group
            for (String each : fileData) {
                try {
                    //Try to get the array list at the first letter of the given word and add the given word to it
                    map.get(each.charAt(0)).put(each.hashCode(), each);
                } catch (NullPointerException e) {
                    //If a map at the first letter of the given word does not exist, create it
                    LinkedHashMap<Integer, String> mapForCharacter = new LinkedHashMap<>();
                    mapForCharacter.put(each.hashCode(), each);
                    map.put(each.charAt(0), mapForCharacter);
                }
            }
            //Return map of strings
            return map;
    }


    //Get the portion of the dictionary needed to be checked as an String[]
    public String[] getRelevantWords(String word, boolean isPhoneticallyWeighted){
        LinkedHashMap<Integer, String> releventDict = dictionary.get(word.charAt(0));
        //Get the list of relevant words
        ArrayList<String> relevantWords = new ArrayList(releventDict.values());
        //Put phonetically similar words first if true
        if(isPhoneticallyWeighted) {
            ArrayList<String> similarSoundingWords = new ArrayList<>();
            //Place those that sound the most similar at the top
            for (int i = relevantWords.size() - 1; i >= 0; i--) {
                //Get the metaphone3 code for the given word
                metaphone3.SetWord(relevantWords.get(i));
                metaphone3.Encode();
                String relMeta = metaphone3.GetMetaph();
                //Get the metaphone3 code for the misspelled word
                metaphone3.SetWord(word);
                metaphone3.Encode();
                String wordMeta = metaphone3.GetMetaph();
                //Check if metaphone3 codes are equal
                if (relMeta.equals(wordMeta)) {
                    String similarWord = relevantWords.get(i);
                    similarSoundingWords.add(relevantWords.get(i));
                }
            }
            //Loop through similar sounding words, remove them from the relevant words and add them to the front
            for (String each : similarSoundingWords) {
                relevantWords.remove(each);
                relevantWords.add(0, each);
            }
        }
        //Turn back into String[] and return values
        String[] words = new String[relevantWords.size()];
        words = relevantWords.toArray(words);
        return words;
    }
}
