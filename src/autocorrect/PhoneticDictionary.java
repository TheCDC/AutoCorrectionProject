package autocorrect;

import resources.ResourceLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PhoneticDictionary {
    //Object for words to be stored and organized according to phonetic code
    private LinkedHashMap<String, String[]> phoneticDictionary = new LinkedHashMap<>();
    //Create metaphone3 object
    private Metaphone3 metaphone3 = new Metaphone3();


    //Compiles the dictionary from the given file name
    public PhoneticDictionary(String filename){
        this.phoneticDictionary = compileDictionary(filename);
    }


    //Returns the phonetic dictionary
    public HashMap<String, String[]> getDictionary() {
        return phoneticDictionary;
    }


    //Get the portion of the dictionary needed to be checked as an String[]
    public String[] getRelevantWords(String word){
        metaphone3.SetWord(word);
        metaphone3.Encode();
        return phoneticDictionary.get(metaphone3.GetMetaph());
    }


    //Get phonetic dictionary
    private LinkedHashMap<String, String[]> compileDictionary(String filename){
        //Create list from given dictionary file
        List<String> fileData = ResourceLoader.loadDictionary(filename);
        //Create a new map in for grouping algorithm
        LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<>();
        //Loop over each word and group in with similar sounding words
        for(String each : fileData){
            //Set the given word needed to be encoded
            metaphone3.SetWord(each);
            metaphone3.Encode();
            //Get phonetic sound for each words
            String phoneticCode = metaphone3.GetMetaph();
            //Create new array list if not already present
            map.putIfAbsent(phoneticCode, new ArrayList<>());
            //Get array list at given phonetic code
            ArrayList<String> phoneticGroup = map.get(phoneticCode);
            //Add the word to the array list at given phonetic key produced by metaphone
            phoneticGroup.add(each);
            //Add group back into array list
            map.put(phoneticCode, phoneticGroup);
        }
        //Create object to be returned
        LinkedHashMap<String, String[]> returnMap = new LinkedHashMap<>();
        //Convert each ArrayList to String[] for faster processing when dealing with large amounts of words
        for(String key : map.keySet()){
            ArrayList<String> wordSet = map.get(key);
            String[] wordSetArray = new String[wordSet.size()];
            wordSetArray = wordSet.toArray(wordSetArray);
            returnMap.put(key, wordSetArray);
        }
        //Return newly formed phonetic dictionary
        return returnMap;
    }
}
