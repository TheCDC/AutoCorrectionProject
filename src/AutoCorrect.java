import java.util.*;

public class AutoCorrect {
    //Variable to store names of Dictionaries
    private final String DICTIONARY_FILENAME;
    private final String AUTOCORRECT_DICTIONARY_FILENAME;
    private GroupedDictionary dictionary;
    private GroupedDictionary commonDictionary;
    private PhoneticDictionary phoneticDictionary;
    private StringManipulator stringManipulator = new StringManipulator();


    //Constructor to set dictionaries to use
    public AutoCorrect(String DICTIONARY_FILENAME, String AUTOCORRECT_DICTIONARY_FILENAME) {
        this.DICTIONARY_FILENAME = DICTIONARY_FILENAME;
        this.AUTOCORRECT_DICTIONARY_FILENAME = AUTOCORRECT_DICTIONARY_FILENAME;
        //Populate dictionaries
        dictionary = new GroupedDictionary(DICTIONARY_FILENAME);
        commonDictionary = new GroupedDictionary(AUTOCORRECT_DICTIONARY_FILENAME);
        phoneticDictionary = new PhoneticDictionary(DICTIONARY_FILENAME);
    }


    //Checks each word of the sentence entered and check if it is misspelled, perform the auto correct if possible
    public String checkSpelling(String words){
        //If word is not empty
        if(words.length() > 0) {
            //Create a hash map to store the original and suggested versions of the words
            HashMap<String, String> wordMap = new HashMap<>();
            String parts[] = words.split("[^A-Za-z]+");
            //Loop through each word in the sentence and see if it is misspelled
            for (String each : parts) {
                //Convert each word to lowercase for checking
                String lowerCase = each.toLowerCase();
                if (!dictionary.isInDictionary(lowerCase)) {
                    //If word is not in the dictionary
                    String correctedWord = null;
                    correctedWord = autoCorrect(lowerCase, plausibleWords(lowerCase), 5, 1);
                    //If not match was selected then do a last effort search
                    if(correctedWord == null)
                        correctedWord = autoCorrect(lowerCase, dictionary.getReleventWords(lowerCase), 5, 1);
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
    private String autoCorrect(String x,  String[] dict, int tolerance, int iterations){
        return autoCorrect(x, dict, tolerance, iterations, 0);
    }
    //A terrible terrible auto suggest algorithm
    private String autoCorrect(String word, String[] dict, int tolerance, int iterations, int iteration){
        //Set current lowest value to a value above threshold, set the default auto correct to null
        String currentClosestString = null;
        while(iterations > iteration) {
            int currentClosest = tolerance;
            //Loop through each word in the list of most common words that start with the letter
            for (String each : dict) {
                //Get editable distance for each word
                int diff = stringManipulator.editableDif(word, each) + stringManipulator.editableDifNoDup(word, each);
                //System.out.println(word + " : " + each + " - > " + currentCheck);
                //If the editable distance is within tolerance and less then the current min
                if (diff <= tolerance && diff < currentClosest) {
                    currentClosest = diff;
                    currentClosestString = each;
                }
            }
            //Return the most likely candidate or null if none were found
            if(dictionary.isInDictionary(word.substring(1)) && currentClosestString == null){
                return word.substring(1);
            } else if (currentClosestString == null && word.substring(1).length() > 0) {
                return autoCorrect(word.substring(1), dict, tolerance, iterations, ++iteration);
            } else {
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
        //Get list of common words starting with similar letters
        for(String each : commonDictionary.getReleventWords(word)) combined.add(each);
        //Add phonetically similar words if such a list of words exist
        try {
            for (String each : phoneticDictionary.getReleventWords(word)) combined.add(each);
        } catch(NullPointerException e){}
        //Turn plausible words back into a string array
        String[] plausibleWords = new String[combined.size()];
        plausibleWords = combined.toArray(plausibleWords);
        //Return plausible words
        return plausibleWords;
    }
}
