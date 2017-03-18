import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        System.out.println("Auto Spelling Correction V1.3");
        AutoCorrect autoCorrect = new AutoCorrect("./eng-no-abvr-dict.txt", "./most-common-no-abvr.txt");
        while(true) {
            System.out.print("\nEnter Text: ");
            Scanner scan = new Scanner(System.in);
            String s = scan.nextLine();
            System.out.println(autoCorrect.checkSpelling(s));
        }
    }
}
