package autocorrect;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        System.out.println("Auto Spelling Correction V1.3");
        //Auto correct is using a standard dictionary to compare if a given word is misspelled
        // and a list of about 18k most common words to compare the most probable words
        // and has a tolerance setting of 5. The greater the tolerance, the more it will try
        // to map a given word to words that are less likely but the user may have meant. Tolerance
        // set at 5 is recommended.
        //Get current instance of main in order to get current class path
        System.out.println("[Building Dictionaries...]");
        AutoCorrect autoCorrect = new AutoCorrect("eng-no-abvr-dict.txt", "most-common-no-abvr.txt", 6);
        //Loop for user input
        while(true) {
            System.out.print("\nEnter Text: ");
            Scanner scan = new Scanner(System.in);
            String s = scan.nextLine();
            //Start timer
            long startTime = System.currentTimeMillis();
            //Run autoCorrect
            String corrected = autoCorrect.correctSpelling(s);
            //Stop timer
            long endTime = System.currentTimeMillis();
            //Get difference
            long duration = (endTime - startTime);
            //Print result
            System.out.println(corrected + "    <- " + duration + " ms");
        }
    }
}