package main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TaskManagerApp extends Application
{
    private ImageView logoImageView;
    private ObservableList<Task> tasks;
    private TableView<Task> taskTableView;
    private TaskManagerController controller;
    private static final String TASKS_FILE_NAME = "tasks.json";
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task List");
        primaryStage.setOnCloseRequest(event -> saveTasksToFile(TASKS_FILE_NAME));
        
        loadTasksFromFile();
        initializeLogo(primaryStage);
        initializeTableColumns();
        initializeTextButtonsAndView(primaryStage);
        
        controller = new TaskManagerController(tasks, taskTableView);
        
        primaryStage.show();
    }
    
    private void initializeLogo(Stage primaryStage) {
        // Load image logo
        Image logoImage = new Image("file:src/main/resources/appImages/taskAppLogoImage.jpg");
        
        // Set the application icon
        primaryStage.getIcons().add(logoImage);
        
        // Create the ImageView
        logoImageView = new ImageView(logoImage);
        logoImageView.setFitHeight(50);
        logoImageView.setFitWidth(50);
    }
    
    @SuppressWarnings("unchecked")
    private void initializeTableColumns() {
        taskTableView = new TableView<>(tasks);

        TableColumn<Task, String> titleColumn = new TableColumn<>("Title");
        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        TableColumn<Task, Boolean> completedColumn = new TableColumn<>("Completed");

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        completedColumn.setCellValueFactory(new PropertyValueFactory<>("completed"));

        taskTableView.getColumns().addAll(titleColumn, descriptionColumn, completedColumn);

        // Set column resize policy
        taskTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Adjust column widths proportionally
        titleColumn.prefWidthProperty().bind(taskTableView.widthProperty().multiply(0.33));
        descriptionColumn.prefWidthProperty().bind(taskTableView.widthProperty().multiply(0.33));
        completedColumn.prefWidthProperty().bind(taskTableView.widthProperty().multiply(0.33));
    }
    
    private void initializeTextButtonsAndView(Stage primaryStage) {
        TextField titleTextField = new TextField();
        titleTextField.setPromptText("Task Title");
        
        TextField descriptionTextField = new TextField();
        descriptionTextField.setPromptText("Task Description");
        
        Button addButton = createButton("Add Task", e -> controller.addTask(titleTextField.getText(), descriptionTextField.getText(), titleTextField, descriptionTextField));
        Button deleteButton = createButton("Delete Task", e -> controller.deleteTask());
        Button markCompletedButton = createButton("Mark as Completed", e -> controller.markTaskAsCompleted());
        Button clearAllButton = createButton("Clear All Tasks", e -> controller.clearAllTasks());
        
        // Creating layouts for buttons and descriptions
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, deleteButton, markCompletedButton, clearAllButton);
        
        VBox layout = new VBox(10);
        layout.getChildren().addAll(taskTableView, titleTextField, descriptionTextField, buttonBox);
        
        // Set margin for the scene's root (StackPane in this case)
        StackPane root = new StackPane(layout);
        StackPane.setMargin(layout, new Insets(0, 10, 10, 10));

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        
        //Lock the window size to a set minimum if decreasing
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
    }
    
    private Button createButton(String text, EventHandler<ActionEvent> eventHandler) {
        Button button = new Button(text);
        button.setOnAction(eventHandler);
        return button;
    }
    
    private void saveTasksToFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadTasksFromFile() {
        Gson gson = new Gson();

        try {
            File file = new File(TASKS_FILE_NAME);

            if (!file.exists()) {
                // If the file doesn't exist, create a new one
                file.createNewFile();
                tasks = FXCollections.observableArrayList();
            } else {
                // If the file exists, load tasks from it
                try (FileReader reader = new FileReader(file)) {
                    Type listType = new TypeToken<List<Task>>() {}.getType(); // Change here
                    List<Task> loadedTasks = gson.fromJson(reader, listType);

                    if (loadedTasks == null) {
                        tasks = FXCollections.observableArrayList();
                    } else {
                        tasks = FXCollections.observableArrayList(loadedTasks); // Create ObservableList
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception (show an alert, log the error, etc.)
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (show an alert, log the error, etc.)
        }
    }


}
