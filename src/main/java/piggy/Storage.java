package piggy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import piggy.task.Deadline;
import piggy.task.Event;
import piggy.task.Task;
import piggy.task.TaskWithDate;
import piggy.task.Todo;

class Storage {
    File file;

    Storage(String path) {
        file = new File(path);
    }

    List<Task> readTasks() {
        List<Task> tasks = new ArrayList<>();
        Scanner sc = null;
        try {
            sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String[] data = sc.nextLine().split(",");

                Task task;
                boolean done = Boolean.parseBoolean(data[1]);
                String description = data[2];

                if (data[0].equals("T")) {
                    task = new Todo(description);
                } else if (data[0].equals("D")) {
                    task = new Deadline(description,
                            LocalDateTime.parse(data[3]).format(TaskWithDate.inDateTimeFormatter));
                } else if (data[0].equals("E")) {
                    task = new Event(description,
                            LocalDateTime.parse(data[3]).format(TaskWithDate.inDateTimeFormatter));
                } else {
                    task = new Task(description);
                }

                if (done) {
                    task.markDone();
                }

                tasks.add(task);
            }
        } catch (FileNotFoundException e) {
            try {
                file.createNewFile();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }
            return new ArrayList<>();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }

        return tasks;
    }

    void writeTasks(TaskList tasks) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            for (Task task : tasks) {
                List<String> line = new ArrayList<>();
                if (task instanceof Todo) {
                    line.add("T");
                } else if (task instanceof Deadline) {
                    line.add("D");
                } else if (task instanceof Event) {
                    line.add("E");
                } else {
                    continue;
                }

                line.add(Boolean.toString(task.isDone()));
                line.add(task.getDescription());

                if (task instanceof TaskWithDate) {
                    line.add(((TaskWithDate) task).getLocalDateTime().toString());
                }
                writer.write(String.join(",", line) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}