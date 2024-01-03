package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
        
        Button markCompletedButton = new Button("Mark as Completed");
        markCompletedButton.setOnAction(e -> markTaskAsCompleted());
        
        // Creating layouts for buttons and descriptions
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, markCompletedButton);
        
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
}
