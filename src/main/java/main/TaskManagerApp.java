package main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
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
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task List");
        primaryStage.setOnCloseRequest(event -> saveTasksToFile("tasks.json"));
        
        loadTasksFromFile();
        initializeLogo(primaryStage);
        initializeTableColumns();
        initializeTextButtonsAndView(primaryStage);
        
        
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
        //tasks = FXCollections.observableArrayList();
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
        
        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> addTask(titleTextField.getText(), descriptionTextField.getText()));

        Button deleteButton = new Button("Delete Task");
        deleteButton.setOnAction(e -> deleteTask());
        
        Button markCompletedButton = new Button("Mark as Completed");
        markCompletedButton.setOnAction(e -> markTaskAsCompleted());
        
        Button clearAllButton = new Button("Clear All Tasks");
        clearAllButton.setOnAction(e -> clearAllTasks());
        
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
    
    public void addTask(String title, String description) {
        // First check if either the title or description given is blank
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            showWarning("Title or description cannot be blank.");
            return;
        }
        
        // Check if a task already exists with the title
        boolean titleExists = tasks.stream()
                .anyMatch(task -> task.getTitle().equalsIgnoreCase(title)
                        && task.getDescription().equalsIgnoreCase(description));

        
        if (titleExists) {
            // Prompt user to confirm if they do/don't want to add another task
            boolean addAnother = promptToAddAnotherTask();
            
            if (!addAnother) {
                // User has chosen to NOT add a task
                return;
            }
        }
        // User had decided to add another task with the same name or no task with that name exists
        Task task = new Task(title, description);
        tasks.add(task);
    }
    
    public void markTaskAsCompleted() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setCompleted(true);
            taskTableView.refresh();
        }
    }
    
    public void deleteTask() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            tasks.remove(selectedTask);
        }
    }
    
    public void clearAllTasks() {
        if (!tasks.isEmpty()) {
            boolean clearAll = promptToClearAllTasks();
            if (clearAll) {
                tasks.removeAll(tasks);
            } else {
                return;
            }
        }
    }
    
    private boolean promptToAddAnotherTask() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Task Exists!");
        alert.setHeaderText("A task with the same title already exists.");
        alert.setContentText("Do you want to add another task with the same title?");
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    private boolean promptToClearAllTasks() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear All Tasks");
        alert.setHeaderText("You are about to delete all tasks listed.");
        alert.setContentText("Do you want to delete all tasks?");

        // Customize button labels
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            File file = new File("tasks.json");

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
