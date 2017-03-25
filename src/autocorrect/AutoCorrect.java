package autocorrect;

import java.util.*;

public class AutoCorrect {
    //Variable to store names of Dictionaries
    private GroupedDictionary dictionary;
    private GroupedDictionary commonDictionary;
    private PhoneticDictionary phoneticDictionary;
    private StringManipulator stringManipulator = new StringManipulator();
    int tolerance;
    //Create metaphone3 object
    private Metaphone3 metaphone3 = new Metaphone3();


    //Constructor to set dictionaries to use
    public AutoCorrect(String DICTIONARY_FILENAME, String AUTOCORRECT_DICTIONARY_FILENAME, int tolerance) {
        this.tolerance = tolerance;
        //Populate dictionaries
        dictionary = new GroupedDictionary(DICTIONARY_FILENAME);
        commonDictionary = new GroupedDictionary(AUTOCORRECT_DICTIONARY_FILENAME);
        phoneticDictionary = new PhoneticDictionary(DICTIONARY_FILENAME);
    }


    //Checks each word of the sentence entered and check if it is misspelled, perform the auto correct if possible
    public String correctSpelling(String words){
        //If word is not empty
        if(words.length() > 0) {
            //Create a hash map to store the original and suggested versions of the words
            HashMap<String, String> wordMap = new HashMap<>();
            String parts[] = words.split("[^A-Za-z]+");
            //Loop through each word in the sentence and see if it is misspelled
            for (String each : parts) {
                //Convert each word to lowercase for checking
                String lowerCase = each.toLowerCase();
                if (lowerCase.length() > 1 && !dictionary.isInDictionary(lowerCase)) {
                    //If word is not in the dictionary
                    String correctedWord = null;
                    correctedWord = autoCorrect(lowerCase, plausibleWords(lowerCase), 1);
                    //Add the original word and the suggested word
                    correctedWord = stringManipulator.checkCapitalization(each, correctedWord);
                    wordMap.put(each, correctedWord);
                }
            }
            //Save original version of the string
            String original = words;
            //Loop through and change the misspelled words if possible
            for (String each : wordMap.keySet()) {
                //If auto correct found a possible word
                if (wordMap.get(each) != null)
                    words = words.replace(each, wordMap.get(each));
            }
            //If the fixed is that same as the original then just return the original, else return the corrected
            return words.equals(original) ? words : "Did you mean: " + words;
        }
        return words;
    }


    //First call to recursive auto correct
    private String autoCorrect(String x,  String[] dict, int iterations){
        return autoCorrect(x, dict, iterations, 0);
    }
    //A terrible terrible auto suggest algorithm
    private String autoCorrect(String word, String[] dict, int iterations, int iteration){
        //Set current lowest value to a value above threshold, set the default auto correct to null
        String currentClosestString = null;
        while(iterations > iteration) {
            //To be considered a valid suggestion, the first word must differ by less than tolerance
            int currentClosest = tolerance;
            //Loop through each word in the list of most common words that start with the letter
            for (String each : dict) {
                //Get editable distance for each word
                int diff = stringManipulator.editableDif(word, each) + stringManipulator.editableDifNoDup(word, each);
                //If the editable distance is less then the current min
                //System.out.println(word + " -> " + each + " " + diff);
                if (diff < currentClosest) {
                    currentClosest = diff;
                    currentClosestString = each;
                }
            }
            if(dictionary.isInDictionary(word.substring(1)) && currentClosestString == null){
                //If the misspelled word is a correct word without the first letter, return the word - first char
                return word.substring(1);
            } else if (currentClosestString == null && word.length() > 1) {
                //Else if, run autocorrect again on the word - first char
                return autoCorrect(word.substring(1), dict, iterations, ++iteration);
            } else {
                //Else no suggestion can be made, just return null
                return currentClosestString;
            }
        }
        return currentClosestString;
    }


    //Get all words that would be plausible suggestions
    private String[] plausibleWords(String word){
        //Array list to hold list of plausible words
        ArrayList<String> combined = new ArrayList<>();
        //Get valid permutations of the given word if word is reasonable length
        if(word.length() <= 5)
            stringManipulator.permutation(word, dictionary, combined);
        //Check dictionary for a plurality mistake if the word ends in s
        String possiblePluralForm = stringManipulator.pluralityCheck(word, dictionary);
        if(possiblePluralForm != null) {
            combined.add(possiblePluralForm);
        }
        //Get list of common words starting with similar letters
        for(String each : commonDictionary.getRelevantWords(word, true)) combined.add(each);
        //Add phonetically similar words if such a list of words exist
        try {
            for (String each : phoneticDictionary.getRelevantWords(word)) combined.add(each);
        } catch(NullPointerException e){}
        //Turn plausible words back into a string array
        String[] plausibleWords = new String[combined.size()];
        plausibleWords = combined.toArray(plausibleWords);
        //Return plausible words
        return plausibleWords;
    }
}