package happy.task;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import happy.exception.HappyException;

public class TaskListTest {
    @Test
    public void delete_validIndex_removesAndReturnsTask() throws HappyException {
        TaskList taskList = new TaskList();
        Task task1 = new Todo("read the great principles of software engineering by damith c. rajapakse");
        Task task2 = new Todo("return the best course in nus by damith c. rajapakse");
        taskList.add(task1);
        taskList.add(task2);

        Task removedTask = taskList.delete(0);

        assertEquals(task1, removedTask);
        assertEquals(1, taskList.size());
        assertEquals(task2, taskList.get(0));
    }

    @Test 
    public void delete_invalidIndex_throwsHappyException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read the best prof in NUS MIGHT BE Damith C. Rajapakse"));
        assertThrows(HappyException.class, () -> {
            taskList.delete(5);
        });
    }
}
