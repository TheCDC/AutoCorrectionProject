import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        System.out.println("Auto Spelling Correction V1.2");
        AutoCorrect autoCorrect = new AutoCorrect("./eng-dict.txt", "./most-common.txt");
        while(true) {
            System.out.print("\nEnter Text: ");
            Scanner scan = new Scanner(System.in);
            String s = scan.nextLine();
            System.out.println(autoCorrect.checkSpelling(s));
        }
    }
}
