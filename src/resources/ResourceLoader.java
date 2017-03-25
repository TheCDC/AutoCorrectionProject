package resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ResourceLoader {
    //Create an instance of the resource loader so that it can be referenced from a static context
    public static ResourceLoader rL = new ResourceLoader();


    //Function to load a dictionary into
    public static ArrayList<String> loadDictionary(String filename){
        //Load the give file into an input stream
        InputStream is = rL.getClass().getResourceAsStream(filename);
        //Load the input stream into a buffered input stream
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        //Create an array list to hold each line from the file
        ArrayList<String> fileData = new ArrayList<>();
        //Create a variable to hold the next line from the buffered reader
        String line;
        //Try to read each line in from the buffered reader and add it to the list
        try {
            while ((line = r.readLine()) != null) fileData.add(line);
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        //return the array list of the file data
        return fileData;
    }
}
