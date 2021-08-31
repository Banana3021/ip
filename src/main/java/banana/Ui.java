package banana;

import java.util.Scanner;

/**
 * The Ui class handles
 * the user inputs.
 *
 * @author: Ravi Ananya
 **/

public class Ui {

    protected Scanner userInput;

    /**
     * Constructor for the Ui class.
     */
    public Ui() {
        userInput = new Scanner(System.in);
    }

    /**
     * Gets the user input.
     *
     * @return the user input.
     */
    public String getInput() {
        if (userInput.hasNext()) {
            return userInput.nextLine();
        }
        return "";
    }

    /**
     * Indicates if there is an error.
     *
     * @return the error message.
     */
    public String showLoadingError() {
        return "There was an error getting the input";
    }
    
}
