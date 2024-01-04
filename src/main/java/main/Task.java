package main;

public class Task
{
    private String title;
    private String description;
    private boolean completed;
    
    // Constructor
    public Task(String title, String description) {
        this.setTitle(title);
        this.setDescription(description);
        this.setCompleted(false); // Defaults to false since a task is not completed upon creating
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public boolean isCompleted ()
    {
        return completed;
    }

    public void setCompleted (boolean completed)
    {
        this.completed = completed;
    }
    
}
