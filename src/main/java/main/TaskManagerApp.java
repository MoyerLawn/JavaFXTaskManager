package main;

import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TaskManagerApp extends Application
{
    private ObservableList<Task> tasks;
    private ListView<Task> taskListView;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Manager");
        
        tasks = FXCollections.observableArrayList();
        taskListView = new ListView<>(tasks);
        
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
        
        // Creating layouts for buttons and descriptions
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, deleteButton, markCompletedButton);
        
        VBox layout = new VBox(10);
        layout.getChildren().addAll(titleTextField, descriptionTextField, buttonBox, taskListView);
        
        // Set margin for the scene's root (StackPane in this case)
        StackPane root = new StackPane(layout);
        StackPane.setMargin(layout, new Insets(0, 10, 10, 10));

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void addTask(String title, String description) {
        // Check if a task already exists with the title
        boolean titleExists = tasks.stream().anyMatch(task -> task.getTitle().equalsIgnoreCase(title));
        
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
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setCompleted(true);
            taskListView.refresh();
        }
    }
    
    public void deleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            tasks.remove(selectedTask);
            taskListView.refresh();
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
}
