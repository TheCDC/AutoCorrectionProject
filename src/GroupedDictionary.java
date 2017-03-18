import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

public class GroupedDictionary {
    //Object for the words to be organized by first character
    private LinkedHashMap<Character, LinkedHashMap<Integer, String>> dictionary;


    //Compiles this cictionary from given filename
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
    public  LinkedHashMap<Character, LinkedHashMap<Integer, String>> compileDictionary(String filename){
        try {
            //Create list from given dictionary file
            List<String> fileData = Files.readAllLines(Paths.get(filename));
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
        } catch (java.io.IOException e){
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }


    //Get the portion of the dictionary needed to be checked as an String[]
    public String[] getReleventWords(String word){
        LinkedHashMap<Integer, String> releventDict = dictionary.get(word.charAt(0));
        String[] releventWords = new String[releventDict.size()];
        releventWords = releventDict.values().toArray(releventWords);
        return releventWords;
    }
}
