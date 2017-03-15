import java.util.*;

public class AutoCorrect {
    //Variable to store names of Dictionaries
    private final String DICTIONARY_FILENAME;
    private final String AUTOCORRECT_DICTIONARY_FILENAME;
    private GroupedDictionary dictionary;
    private GroupedDictionary commonDictionary;
    private PhoneticDictionary phoneticDictionary;


    //Constructor to set dictionaries to use
    public AutoCorrect(String DICTIONARY_FILENAME, String AUTOCORRECT_DICTIONARY_FILENAME) {
        this.DICTIONARY_FILENAME = DICTIONARY_FILENAME;
        this.AUTOCORRECT_DICTIONARY_FILENAME = AUTOCORRECT_DICTIONARY_FILENAME;
        //Populate dictionaries
        dictionary = new GroupedDictionary(DICTIONARY_FILENAME);
        commonDictionary = new GroupedDictionary(AUTOCORRECT_DICTIONARY_FILENAME);
        phoneticDictionary = new PhoneticDictionary(DICTIONARY_FILENAME);
    }


    //Check to see if word is contained in dictionary
    public boolean isInDictionary(String word){
        return dictionary.getDictionary().get(word.charAt(0)).containsValue(word);
    }


    //Get all words that would be plausible suggestions
    public String[] plausibleWords(String word){
        //Array list to hold list of plausible words
        ArrayList<String> combined = new ArrayList<>();
        //Get list of plausible words
        for(String each : commonDictionary.getReleventWords(word)) combined.add(each);
        for(String each : phoneticDictionary.getReleventWords(word)) combined.add(each);
        //Turn plausible words back into a string array
        String[] plausibleWords = new String[combined.size()];
        plausibleWords = combined.toArray(plausibleWords);
        //Return plausible words
        return plausibleWords;
    }


    //Checks each word of the sentence entered and check if it is misspelled, perform the auto correct if possible
    public String checkSpelling(String words){
        //If word is not empty
        if(words.length() > 0) {
            //Create a hash map to store the original and suggested versions of the words
            HashMap<String, String> wordMap = new HashMap<>();
            String parts[] = words.split("[^\\w']+");
            //Loop through each word in the sentence and see if it is misspelled
            for (String each : parts) {
                if (!isInDictionary(each)) {
                    //If word is not in the dictionary
                    String correctedWord = null;
                    correctedWord = autoCorrect(each, plausibleWords(each), 5, 1);
                    //If not match was selected then do a last effort search
                    if(correctedWord == null)
                        correctedWord = autoCorrect(each, dictionary.getReleventWords(each), 5, 1);
                    //Add the original word and the suggested word
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


    //Get the portion of the dictionary needed to be checked as an array list
    public ArrayList<String> getReleventWords(String word, LinkedHashMap<Character, LinkedHashMap<Integer, String>> dict){
        return new ArrayList<String>(dict.get(word.charAt(0)).values());
    }


    //First call to recursive auto correct
    public String autoCorrect(String x,  String[] dict, int tolerance, int iterations){
        return autoCorrect(x, dict, tolerance, iterations, 0);
    }
    //A terrible terrible auto suggest algorithm
    public String autoCorrect(String word, String[] dict, int tolerance, int iterations, int iteration){
        //Set current lowest value to a value above threshold, set the default auto correct to null
        String currentClosestString = null;
        while(iterations > iteration) {
            int currentClosest = tolerance;
            //Loop through each word in the list of most common words that start with the letter of the word being corrected
            for (String each : dict) {
                //Get editable distance for each word
                int currentCheck = editableDif(word, each) + editableDifNoDup(word, each);
                //If the editable distance is withing 3 and less then the current min, then make that the most likely word
                if (currentCheck <= tolerance && currentCheck < currentClosest) {
                    currentClosest = currentCheck;
                    currentClosestString = each;
                }
            }
            //Return the most likely candidate or null if none were found
            if(isInDictionary(word.substring(1)) && currentClosestString == null){
                return word.substring(1);
            } else if (currentClosestString == null && word.substring(1).length() > 0) {
                return autoCorrect(word.substring(1), dict, iterations, ++iteration);
            } else {
                return currentClosestString;
            }
        }
        return currentClosestString;
    }

    
    // Adapted from: https://github.com/crwohlfeil/damerau-levenshtein/blob/master/src/main/java/com/codeweasel/DamerauLevenshtein.java
    // I am using this version in order to count a letter swap as just one edit rather than two
    public static int editableDif(CharSequence source, CharSequence target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
        int sourceLength = source.length();
        int targetLength = target.length();
        if (sourceLength == 0) return targetLength;
        if (targetLength == 0) return sourceLength;
        int[][] dist = new int[sourceLength + 1][targetLength + 1];
        for (int i = 0; i < sourceLength + 1; i++) {
            dist[i][0] = i;
        }
        for (int j = 0; j < targetLength + 1; j++) {
            dist[0][j] = j;
        }
        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                int cost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
                dist[i][j] = Math.min(Math.min(dist[i - 1][j] + 1, dist[i][j - 1] + 1), dist[i - 1][j - 1] + cost);
                if (i > 1 &&
                        j > 1 &&
                        source.charAt(i - 1) == target.charAt(j - 2) &&
                        source.charAt(i - 2) == target.charAt(j - 1)) {
                    dist[i][j] = Math.min(dist[i][j], dist[i - 2][j - 2] + cost);
                }
            }
        }
        return dist[sourceLength][targetLength];
    }


    //Checks to see if the candidate word contains only the same letters as the given word
    private int editableDifNoDup(String firstStr, String secondStr) {
        //Convert each strings into an array
        char[] original = firstStr.toCharArray();
        char[] suggested = secondStr.toCharArray();
        //Create a set to account for duplicates
        Set<Character> originalSet = new LinkedHashSet<>();
        for(char each : original) originalSet.add(each);
        Set<Character> suggestedSet = new LinkedHashSet<>();
        for(char each : suggested) suggestedSet.add(each);
        //Reconstruct string object for editable difference comparison
        StringBuilder originalNoDup = new StringBuilder();
        for (Character character : originalSet) {originalNoDup.append(character);}
        StringBuilder suggestedNoDup = new StringBuilder();
        for (Character character : suggestedSet) {suggestedNoDup.append(character);}
        //Return editable difference between string without duplicates
        return editableDif(originalNoDup.toString(), suggestedNoDup.toString());
    }
}
