package main;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TaskManagerController
{
    private ObservableList<Task> tasks;
    private TableView<Task> taskTableView;
    
    public TaskManagerController(ObservableList<Task> tasks, TableView<Task> taskTableView) {
        this.tasks = tasks;
        this.taskTableView = taskTableView;
    }

    public void addTask(String title, String description, TextField titleTextField, TextField descriptionTextField) {
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
        
        // Clear out the title and description fields after entry has been made
        titleTextField.clear();
        descriptionTextField.clear();
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
        return showConfirmationAlert("Task Exists!", "A task with the same title already exists.",
                "Do you want to add another task with the same title?");
    }

    private boolean promptToClearAllTasks() {
        return showConfirmationAlert("Clear All Tasks", "You are about to delete all tasks listed.",
                "Do you want to delete all tasks?");
    }
    
    private boolean showConfirmationAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

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
}
