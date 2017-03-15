import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhoneticDictionary {
    private HashMap<String, String[]> phoneticDictionary = new HashMap<>();

    public PhoneticDictionary(String filename){
        this.phoneticDictionary = compileDictionary(filename);
    }


    public HashMap<String, String[]> getDictionary() {
        return phoneticDictionary;
    }


    //Get phonetic dictionary
    public HashMap<String, String[]> compileDictionary(String filename){
        try {
            //Create list from given dictionary file
            List<String> fileData = Files.readAllLines(Paths.get(filename));
            //Create a new map in for grouping algorithm
            HashMap<String, ArrayList<String>> map = new HashMap<>();
            //Loop over each word and group in with similar sounding words
            for(String each : fileData){
                //Get phonetic sound for each words
                String phoneticCode = Soundex.getGode(each);
                //Create new array list if not already present
                map.putIfAbsent(phoneticCode, new ArrayList<>());
                //Get array list at given phonetic code
                ArrayList<String> phoneticGroup = map.get(phoneticCode);
                //Add the word to the array list at given phonetic key produced by soundex
                phoneticGroup.add(each);
                //Add group back into array list
                map.put(phoneticCode, phoneticGroup);
            }
            //Create object to be returned
            HashMap<String, String[]> returnMap = new HashMap<>();
            //Convert each ArrayList to String[] for faster processing
            for(String key : map.keySet()){
                ArrayList<String> wordSet = map.get(key);
                String[] wordSetArray = new String[wordSet.size()];
                wordSetArray = wordSet.toArray(wordSetArray);
                returnMap.put(key, wordSetArray);
            }
            //Return newly formed phonetic dictionary
            return returnMap;
        } catch (java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }


    //Get the portion of the dictionary needed to be checked as an String[]
    public String[] getReleventWords(String word){
        return phoneticDictionary.get(Soundex.getGode(word));
    }
}
