package com.kodilla.stream.portfolio;

import com.kodilla.stream.protfolio.Board;
import com.kodilla.stream.protfolio.Task;
import com.kodilla.stream.protfolio.TaskList;
import com.kodilla.stream.protfolio.User;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class BoardTestSuite {


    public Board prepareTestData() {

        //users
        User user1 = new User("developer1", "John Smith");
        User user2 = new User("projectmanager1", "Nina White");
        User user3 = new User("developer2", "Emilia Stephanson");
        User user4 = new User("developer3", "Konrad Bridge");
        //tasks
        Task task1 = new Task("Microservice for taking temperature",
                "Write and test the microservice taking\n" +
                        "the temperaure from external service",
                user1,
                user2,
                LocalDate.now().minusDays(20),
                LocalDate.now().plusDays(30));
        Task task2 = new Task("HQLs for analysis",
                "Prepare some HQL queries for analysis",
                user1,
                user2,
                LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(5));
        Task task3 = new Task("Temperatures entity",
                "Prepare entity for temperatures",
                user3,
                user2,
                LocalDate.now().minusDays(20),
                LocalDate.now().plusDays(15));
        Task task4 = new Task("Own logger",
                "Refactor company logger to meet our needs",
                user3,
                user2,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(25));
        Task task5 = new Task("Optimize searching",
                "Archive data searching has to be optimized",
                user4,
                user2,
                LocalDate.now(),
                LocalDate.now().plusDays(5));
        Task task6 = new Task("Use Streams",
                "use Streams rather than for-loops in predictions",
                user4,
                user2,
                LocalDate.now().minusDays(15),
                LocalDate.now().minusDays(2));
        //taskLists
        TaskList taskListToDo = new TaskList("To do");
        taskListToDo.addTask(task1);
        taskListToDo.addTask(task3);
        TaskList taskListInProgress = new TaskList("In progress");
        taskListInProgress.addTask(task5);
        taskListInProgress.addTask(task4);
        taskListInProgress.addTask(task2);
        TaskList taskListDone = new TaskList("Done");
        taskListDone.addTask(task6);
        //board
        Board project = new Board("Project Weather Prediction");
        project.addTaskList(taskListToDo);
        project.addTaskList(taskListInProgress);
        project.addTaskList(taskListDone);
        return project;
    }


    @Test
    public void testAddTaskList(){
        Board project = prepareTestData();

        Assert.assertEquals(3,project.getTaskLists().size());
    }

    //wyszukiwanie zadań konkretnego użytkownika
    @Test
    public void testAddTaskListFindUsersTasks(){

        //WHEN
        Board project = prepareTestData();
        //GIVEN
        User user = new User("developer1","John");

        List<Task> tasks = project.getTaskLists().stream()
                //uruchamiam strumień na kolekcji getTaskLists obiektu Board
                        .flatMap(l-> l.getTasks().stream())
                //spłaszczam strumień bo na wejściu jest lista zadań a chcemy strumień zadań
                        .filter(t -> t.getAssignedUser().equals(user))
                //filtrujemy lambdą który zwraca true/false w zależnośći czy użytkownicy są sobie równi
                //na wyjściu zadania przypisane do użytkownika "developer1"
                        .collect(toList());
                //kolekcja wynikowa
        //THEN
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals(user, tasks.get(0).getAssignedUser());
        Assert.assertEquals(user, tasks.get(1).getAssignedUser());
    }



    //wyszukiwanie przeterminowanych zadań
    @Test
    public void testAddTaskListFindOutDatedTasks(){
        //GIVEN
        Board project = prepareTestData();

        //WHEN
        List<TaskList> undoneTask = new ArrayList<>();
        //robocza lista z 2 pustymi listami zadań niewykonanymi jeszcze
        undoneTask.add(new TaskList("To do"));
        undoneTask.add(new TaskList("In progress"));


        List<Task> tasks =project.getTaskLists().stream()
                //uruchamiamy strumień na kolekcji getTaskLists() obiektu Board
                .filter(undoneTask::contains)
                //filtrujemy tylko listy zadań, które zawierają niewykonane zadania
                //wskazujmey więc referencję do metody undoneTasks::contains
                //na strumień wyjściowy trafią te listy,które zapisane są w roboczej liście undoneTasks
                .flatMap(tl->tl.getTasks().stream())
                //spłaszczenie strumienia ; do wyjścia trafiają tylko konkretne zadania z list zadań
                //zamiast obiektów reprezentujących te listy
                .filter(t-> t.getDeadline().isBefore(LocalDate.now()))
                //dla każdego zadania sprawdzamy czy jego data jest wcześniejsza niż bieżąca data systemowa
                //na wyjściu strumienia tylko zadania z datami przeterminiwanymi
                .collect(toList());

        //THEN
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals("HQLs for analysis", tasks.get(0).getTitle());
    }

    //Obliczanie ilości zadań wykonywanych conajmniej od 10 dni
    @Test
    public void testAddTaskListFindLongTask(){

        //GIVEN
        Board project = prepareTestData();

        //WHEN
        List<TaskList> inProgressTasks = new ArrayList<>();
        //robocza lista zadań w trakcie realizacji
        inProgressTasks.add(new TaskList("In progress"));

        long longTasks = project.getTaskLists().stream()
                //uruchamiamy strumień na kolekcji getTaskLists obiektu Board
                //wynik będzie przypisany do skalarnej zmiennej longTask typu long
                .filter(inProgressTasks::contains)
                //filtruje bo chce tylko liste zadań w trakcie realizacji
                .flatMap(tl -> tl.getTasks().stream())
                //spłaszczenie - z list zadań przechodzimy do konretnych zadań
                .map(t -> t.getCreated())
                //wykonujemy transformację strumienia
                // wejście = zadania >>>> wyjście = daty typu LocalDate
                .filter(d -> d.compareTo(LocalDate.now().minusDays(10)) <=0)
                //filtrujemy daty  - różnica między datą systemową a utworzenia zadania pomniejszoną
                // o 10 jest (= 0 / <0 / >0)
                //metoda compareTo zwraca 0 gdy daty są równe , -1 gdy data jest mniejsza 1 gdy data jest większa
                //na wyjściu tylko daty, które są starsze lub równe niż 10 dni wcześniej przed bieżącą datą
                .count();
        //zliczamy liczbę elementów w strumieniu przy pomocy kolektora skalarnego count()

        //THEN
        Assert.assertEquals(2,longTasks);
    }


    //średnia ilośc dni realizacji zadania
    @Test
    public void testAddTaskListAverageWorkingOnTask(){

        //GIVEN
        Board project = prepareTestData();
        //WHEN
        List<TaskList> inProgress = new ArrayList<>();
        inProgress.add(new TaskList("In progress"));

        //liczba zadań
        long longTasks = project.getTaskLists().stream()
                .filter(inProgress::contains)
                .flatMap(tl -> tl.getTasks().stream())
                .map(t -> t.getCreated())
                .count();


     //suma dni realizacji
       long totalDays = project.getTaskLists().stream()
              .filter(inProgress::contains)
                .flatMap(tl -> tl.getTasks().stream())
                .map(t -> t.getCreated().until(t.getDeadline(), ChronoUnit.DAYS))
                .reduce(0,(sum,current) -> sum= sum.add(current));

        double mean = (double) totalDays / longTasks;

        //THEN
        Assert.assertEquals(3,longTasks);
        Assert.assertEquals(7,mean,0.01);



    }

}
