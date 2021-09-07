package banana;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * The Duke class is the program's
 * fundamental class.
 *
 * @author: Ravi Ananya
 **/

public class Duke extends Application {

    private final Label HELLO_LABEL;
    private final String FILE_PATH = "/Users/ravi57004/ip/src/main/java/Tasks.txt";

    private Image user;
    private Image bot;
    private Storage storage;
    private TaskList tasks;

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    public Duke() {
        String helloText = "Hello I'm Hange! \n How can I help you?";
        HELLO_LABEL = new Label(helloText);
        try {
            user = new Image(new FileInputStream("levi.jpg"));
            bot = new Image(new FileInputStream("hange.jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries starting up the program and
     * loading all the information from the files.
     *
     * @param filePath the file to be accessed.
     */
    public void init(String filePath) {
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);
        userInput = new TextField();
        sendButton = new Button("Send");

        try {
            storage = new Storage(filePath);
            tasks = storage.load(
                    new File(storage.getFilePath()));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Starts the GUI program.
     *
     * @param stage the screen.
     */
    @Override
    public void start(Stage stage) {
        init(FILE_PATH);
        setStage(stage);
        setLayout();
        loadInformation();
    }

    /**
     * Sets the frame of the program.
     *
     * @param stage the stage to be set.
     */
    public void setStage(Stage stage) {
        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);
        scene = new Scene(mainLayout);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Duke");
        stage.setResizable(false);
        stage.setMinHeight(700.0);
        stage.setMinWidth(500.0);
        mainLayout.setPrefSize(500.0, 700.0);
    }

    /**
     * Sets the program layout.
     */
    public void setLayout() {
        scrollPane.setPrefSize(500, 635);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        userInput.setPrefWidth(440.0);
        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);
        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);
        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);
    }

    /**
     * Loads welcome information
     * and handles user input.
     */
    public void loadInformation() {
        dialogContainer.getChildren().addAll(
                DialogBox.getDukeDialog(HELLO_LABEL, new ImageView(bot)));

        sendButton.setOnMouseClicked((event) -> {
            addToScreen();
        });

        userInput.setOnAction((event) -> {
            addToScreen();
        });
    }

    /**
     * You should have your own function to generate a response to user input.
     * Replace this stub with your completed method.
     */
    protected static String getResponse(String input) {
        return "Duke heard: " + input;
    }

    /**
     * Adds user input/corresponding
     * output to screen.
     */
    public void addToScreen() {
        try {
            Label userText = new Label(userInput.getText());
            Parser p = new Parser(userInput.getText());
            String output = p.parseInput(tasks);
            Label dukeText = new Label(output);
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(userText, new ImageView(user)),
                    DialogBox.getDukeDialog(dukeText, new ImageView(bot))
            );
            if (userInput.getText().equals("bye")) {
                Platform.exit();
            } else {
                Parser.setPrevInput(userInput.getText());
                writeToFile();
            }
            userInput.clear();
        } catch (DukeException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
    }

    /**
     * Writes to the Tasks.txt file everytime
     * a change in tasks has occurred.
     *
     * @throws IOException if not able to write to the file.
     */
    public void writeToFile() throws IOException {
        String text = "";
        FileWriter fw = new FileWriter(FILE_PATH, false);
        for (int i = 0; i < tasks.getSize(); i++) {
            String doneStr = "No";
            Task currentTask = tasks.getTask(i);
            assert currentTask != null;
            if (currentTask.getIsDone().equals("[X]")) {
                doneStr = "Yes";
            }
            if (currentTask instanceof ToDo) {
                text += "T ~ " + doneStr + " ~ "
                        + currentTask.getDescription() + "\n";
            } else if (currentTask instanceof Deadline) {
                Deadline dl = (Deadline) currentTask;
                text += "D ~ " + doneStr + " ~ "
                        + currentTask.getDescription() + " ~ "
                        + dl.getDeadLine() + "\n";
            } else if (currentTask instanceof Event) {
                Event ev = (Event) currentTask;
                text += "E ~ " + doneStr + " ~ "
                        + currentTask.getDescription() + " ~ "
                        + ev.getEvent() + "\n";
            } else {
                text += doneStr + " ~ "
                        + currentTask.getDescription() + "\n";
            }
        }
        fw.write(text);
        fw.close();
    }

}

/**
 * The DukeException class
 * throws specialised Duke exceptions.
 *
 * @author: Ravi Ananya
 **/
class DukeException extends Exception {

    private String errorMessage;

    /**
     * Constructor for DukeException.
     *
     * @param errorMessage user input.
     */
    public DukeException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the error message.
     *
     * @return the error message.
     */
    @Override
    public String getMessage() {
        return errorMessage;
    }

}














