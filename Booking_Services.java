package com.example.demo.TrainTicketBooking;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.FileReader;
// import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
//import java.time.chrono.IsoChronology;
import java.util.*;
//import java.util.concurrent.ExecutionException;

//import javax.xml.crypto.Data;

//import Recursion.re1;

class DataBase {
    private final Gson gson;
    private final File fileUser;
    private final File fileTrain;

    Map<String, User> users = new HashMap<>();
    Map<String, Train> trains = new HashMap<>();

    DataBase(Gson gson, File fileUser, File fileTrain) {
        this.gson = gson;
        this.fileUser = fileUser;
        this.fileTrain = fileTrain;
    }

    public void loadData() throws IOException {
        if (!fileUser.exists())
            fileUser.createNewFile();
        if (!fileTrain.exists())
            fileTrain.createNewFile();

        if (fileUser.length() > 0) {
            Type userType = new TypeToken<Map<String, User>>() {
            }.getType();
            users = gson.fromJson(Files.readString(fileUser.toPath()), userType);
            if (users == null)
                users = new HashMap<>();
        }

        if (fileTrain.length() > 0) {
            Type trainType = new TypeToken<Map<String, Train>>() {
            }.getType();
            trains = gson.fromJson(Files.readString(fileTrain.toPath()), trainType);
            if (trains == null)
                trains = new HashMap<>();
        }
    }

    public void saveUserData(User user) throws IOException {
        users.put(user.getUserId(), user);
        Files.writeString(fileUser.toPath(), gson.toJson(users));
    }

    public void saveTrainData(Train train) throws IOException {
        trains.put(train.getTrainId(), train);
        Files.writeString(fileTrain.toPath(), gson.toJson(trains));
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public boolean userExists(String userId) {
        return users.containsKey(userId);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public Train getTrain(String trainId) {
        return trains.get(trainId);
    }

    public Collection<Train> getAllTrains() {
        return trains.values();
    }

}

class Pair {
    int first, second;

    Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }
}

class Train {
    private final String id;
    private final int maxSeats;
    private final String[] route;
    private final boolean[][] segmentSeat;

    private static final int COLS = 6;
    private final int rows;

    Train(String id, int maxSeats, String[] route) {
        if (maxSeats <= 0) {
            throw new IllegalArgumentException("Invalid seat count");
        }

        this.id = id;
        this.maxSeats = maxSeats;
        this.route = route;

        this.rows = (maxSeats + COLS - 1) / COLS;
        this.segmentSeat = new boolean[maxSeats][route.length - 1];
    }

    public String getTrainId() {
        return id;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public String[] getRoute() {
        return route;
    }

    public Pair checkFeasibleJourney(String start, String end) {
        start = start.toLowerCase();
        end = end.toLowerCase();

        int st = -1, en = -1;

        for (int i = 0; i < route.length; i++) {
            if (route[i].equals(start))
                st = i;
            if (route[i].equals(end))
                en = i;
        }

        if (st == -1 || en == -1 || st >= en) {
            return new Pair(-1, -1);
        }
        return new Pair(st, en);
    }

    public boolean isSeatAvailable(int seatNo, int st, int en) {
        if (seatNo < 0 || seatNo >= maxSeats)
            return false;

        for (int i = st; i < en; i++) {
            if (segmentSeat[seatNo][i])
                return false;
        }
        return true;
    }

    public void bookSeat(int seatNo, int st, int en) {
        if (!isSeatAvailable(seatNo, st, en)) {
            throw new IllegalStateException("Seat not available");
        }
        // true -> seat is booked
        for (int i = st; i < en; i++) {
            segmentSeat[seatNo][i] = true;
        }
    }

    // false -> seat is canceled
    public void cancelSeat(int seatNo, int st, int end) {
        for (int i = st; i < end; i++) {
            segmentSeat[seatNo][i] = false;
        }
    }
}

class Ticket {
    private String ticketId;
    private String owner;
    private String starting, ending;
    // private String date;
    // private String time;
    private String trainId;
    private int seatNumber;
    private boolean status;

    // Map<String, Ticket> TicketList = new HashMap<>();

    Ticket(String ticketId, String trainId, String owner, int seatNum, String starting, String ending,
            boolean status) {

        this.status = status;
        this.ticketId = ticketId;
        this.trainId = trainId;
        this.owner = owner;
        this.seatNumber = seatNum;
        this.starting = starting;
        this.ending = ending;
        // this.time = time;
        // this.date = date;
    }

    public String getTicketId() {
        return ticketId;
    }

    public boolean getTicketStatus() {
        return status;
    }

    public void setTicketStatus(boolean b) {
        status = b;
    }

    public String getOwner() {
        return owner;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String start() {
        return starting;
    }

    public String ending() {
        return ending;
    }

    // public String dateTime() {
    // return "Date : " + date + " " + "at " + time;
    // }

    public String trainId() {
        return trainId;
    }

}

class User {
    private String name;
    private int age;
    private String userId;
    private String password;
    private List<Ticket> tickets;

    public void addTicket(Ticket t) {
        tickets.add(t);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    User(String name, int age, String userId, String password) {
        this.age = age;
        this.name = name;
        this.password = password;
        this.userId = userId;
        this.tickets = new ArrayList<>();
    }

    public void bookNewTicket(int seatNo, int st, int end) {
        // Train.bookSeat(seatNo, st, end);

    }

    public void cancelTicket(Ticket ticket) {

    }
}

// Does all the validation processes of user inputs
class AuthValidator {

    public static void validateName(String name) {

        if (name == null)
            throw new IllegalArgumentException("Name cannot be null");

        name = name.trim();

        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");

        if (name.length() < 2)
            throw new IllegalArgumentException("Name must have at least 2 characters");

        // Only letters and single spaces between words
        if (!name.matches("[A-Za-z]+( [A-Za-z]+)*")) {
            throw new IllegalArgumentException(
                    "Name must contain only letters and spaces");
        }
    }

    public static void ageValidation(String age) {
        if (age == null)
            throw new IllegalArgumentException("Age cannot be NULL");
        age = age.trim();

        if (age.isEmpty())
            throw new IllegalArgumentException("Age is a required field");

        if (Integer.parseInt(age) < 1 || Integer.parseInt(age) > 102)
            throw new IllegalArgumentException("Enter a valid age ");

    }

    public static void userChoiceValidation(String userChoice, int p) {
        // Scanner sc = new Scanner(System.in);
        if (userChoice == null)
            throw new IllegalArgumentException("userChoice cannot be NULL");

        userChoice = userChoice.trim();

        if (userChoice.isEmpty())
            throw new IllegalArgumentException("userChoice is a required field");

        if (Integer.parseInt(userChoice) < 1 || Integer.parseInt(userChoice) > p)
            throw new IllegalArgumentException("Enter a number between 1 & " + p);
    }

    public static void validateRegistration(String userId, String password) {

        validateUserId(userId);
        validatePassword(password);
    }

    public static void validateLogin(String userId, String password) {

        if (userId == null || password == null)
            throw new IllegalArgumentException("UserId or password cannot be null");
    }

    public static void validateUserId(String userId) {

        if (userId == null)
            throw new IllegalArgumentException("UserId cannot be null");

        if (userId.trim().isEmpty())
            throw new IllegalArgumentException("UserId cannot be empty");

        if (userId.length() < 4)
            throw new IllegalArgumentException("UserId must be at least 4 characters");

        if (!userId.matches("[a-zA-Z0-9]+"))
            throw new IllegalArgumentException("UserId must be alphanumeric");
    }

    public static void validatePassword(String password) {

        if (password == null)
            throw new IllegalArgumentException("Password cannot be null");

        if (password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters");

        if (!password.matches(".*[A-Za-z].*"))
            throw new IllegalArgumentException("Password must contain a letter");

        // if (!password.matches(".*\\d.*"))
        // throw new IllegalArgumentException("Password must contain a digit");
    }
}

class AuthService {

    public void registerNewUser(User newUser, DataBase db) {

        try {
            AuthValidator.validateRegistration(newUser.getUserId(), newUser.getPassword());

            db.users.put(newUser.getUserId(), newUser);
            db.saveUserData(newUser);
            System.out.println("//***** Successfully Registered ******//");
            // userList.put(userId, new ArrayList<Ticket>());

        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    public boolean login(String userId, String password, DataBase db) {
        User user = db.getUser(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Wrong password");
        }

        return true;
    }

}

public class BookingService {

    public void showSeatMatrix(
            Train t,
            int stIdx,
            int endIdx,
            DataBase db) {

        Train train = db.getTrain(t.getTrainId());

        final int COLS = 6;
        int totalSeats = train.getMaxSeats();
        int rows = (totalSeats + COLS - 1) / COLS;

        System.out.println("    ===== Seat Availability =====");
        System.out.println("    1 -> Available | 0 -> Booked\n");

        for (int i = 0; i < rows; i++) {
            System.out.print("            ");
            for (int j = 0; j < COLS; j++) {
                int seatIndex = i * COLS + j;

                if (seatIndex >= totalSeats) {
                    System.out.print("  ");
                    continue;
                }

                boolean available = train.isSeatAvailable(seatIndex, stIdx, endIdx);

                System.out.print((available ? 1 : 0) + " ");
            }
            System.out.println();
        }

        System.out.println("\nSeat numbers are 1-based (top-left is seat 1).");
    }

    public void bookTicket(
            String userId,
            String trainId,
            int seatNo,
            String start,
            String end,
            Random random,
            DataBase db) throws IOException {

        User user = db.getUser(userId);
        Train train = db.getTrain(trainId);

        if (user == null || train == null) {
            throw new IllegalStateException("Invalid booking request");
        }

        Pair segment = train.checkFeasibleJourney(start, end);
        if (segment.first == -1) {
            throw new IllegalArgumentException("Invalid route");
        }

        train.bookSeat(seatNo - 1, segment.first, segment.second);
        int tic = 1000 + random.nextInt(99999);
        Ticket ticket = new Ticket(
                String.valueOf(tic),
                trainId,
                user.getName(),
                seatNo,
                start,
                end,
                true);

        user.addTicket(ticket);

        db.saveUserData(user);
        db.saveTrainData(train);
        System.out.println("\t***** Congratulations ! *****");
        System.out.println("\n  Ticket Booked and confirmed for " + start + " -> " + end + "\n");
    }

    public void loadTickets(DataBase db, String userId) {
        User user = db.getUser(userId);

        if (!user.getTickets().isEmpty()) {
            int i = 1;
            for (Ticket t : user.getTickets()) {
                System.out.println("        Ticket " + i++);
                System.out.println("    Name : " + t.getOwner());
                System.out.println("    From : " + t.start());
                System.out.println("    To : " + t.ending());
                System.out.println("    TrainId : " + t.trainId());
                System.out.println("    Seat Number : " + t.getSeatNumber());
                if (t.getTicketStatus())
                    System.out.println("    Status : " + " OK");
                else
                    System.out.println("  Status : " + "Canceled");
                System.out.println("\n-----------------------------------\n");
            }
        } else {
            System.out.println("No tickets booked yet !");
        }
    }

    public void cancelTicket(String userId, String ticketId, DataBase db) {

        User user = db.getUser(userId);
        if (user == null) {
            System.out.println("Invalid user");
            return;
        }

        Ticket target = null;

        for (Ticket t : user.getTickets()) {
            if (t.getTicketId().equals(ticketId) && t.getTicketStatus()) {
                target = t;
                break;
            }
        }

        if (target == null) {
            System.out.println("No active ticket found with this ID");
            return;
        }

        Train train = db.getTrain(target.trainId());
        if (train == null) {
            System.out.println("Associated train not found");
            return;
        }

        Pair segment = train.checkFeasibleJourney(target.start(), target.ending());
        if (segment.first == -1) {
            System.out.println("Invalid journey segment");
            return;
        }

        // Free the seat
        train.cancelSeat(
                target.getSeatNumber() - 1,
                segment.first,
                segment.second);

        // Update ticket status
        target.setTicketStatus(false);

        try {
            db.saveTrainData(train);
            db.saveUserData(user);
        } catch (IOException e) {
            System.out.println("Failed to persist cancellation");
        }

        System.out.println("Ticket canceled successfully");
    }

    public static void main(String[] args) {

        // ======== Routes available =======
        String[] route1 = { "agartala", "guwahati", "silchar", "sikkim", "patna", "deogarh" };
        String[] route2 = { "damdam", "sealdah", "howrah", "jalpaiguri", "silchar", "guwahati", "dimapur", "agartala" };
        String[] route3 = { "agartala", "bishramganj", "sabrum", "kumarghat", "pecharthal", "khowai" };

        File file = new File("User.json");
        File fileTrain = new File("TrainData.json");

        BookingService bs = new BookingService();
        Gson gson = new Gson();
        AuthService auth = new AuthService();
        Random random = new Random();

        Ticket newTicket;
        User newUser;

        Train t1 = new Train("Train1", 30, route3);
        Train t2 = new Train("Train_2", 60, route2);

        DataBase db = new DataBase(gson, file, fileTrain);

        try {
            if (!file.exists())
                file.createNewFile();
            if (!fileTrain.exists())
                file.createNewFile();

            db.loadData();

        } catch (Exception e) {
            e.getMessage();
        }

        Scanner sc = new Scanner(System.in);

        System.out.println("====== Welcome to IRCTC ======");

        // User choice validation
        // System.out.println("Enter service number whichever you want: ");
        while (true) {
            System.out.println(
                    "\n1. Check Train Availability  \n2. BookingServices\n3. View Booked Tickets         Press 4 to exit\n");
            String userChoice;
            while (true) {
                System.out.print("Enter your choice: ");
                userChoice = sc.next();
                sc.nextLine();

                try {
                    AuthValidator.userChoiceValidation(userChoice, 4);
                    // userChoice = Integer.parseInt(userChoice);
                    break; // valid input → exit loop
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                    System.out.println("Enter from the given options only\n");
                }
            }
            // int userChoice = AuthValidator.userChoiceValidation(sc, 3);

            if (Integer.parseInt(userChoice) == 1) {
                String start, end;

                System.out.println("\b______Check Train Availability_______");

                System.out.println("Enter starting : ");
                start = sc.next();
                sc.nextLine();
                System.out.println("Enter destination : ");
                end = sc.next();
                sc.nextLine();

                Pair p = t1.checkFeasibleJourney(start, end);
                if (p.first == -1) {
                    System.out.println("\tOops!, No Train available>\n");
                } else {
                    System.out.println("\tTrain with id " + t1.getTrainId() + " is available\n");
                }

                // Booking Service
            } else if (Integer.parseInt(userChoice) == 2) {
                System.out.println("\n\t======Booking Services=====");
                System.out.println(" Before booking, you have to Login/Register");
                System.out.println("    \n1. Login          2. Register");

                String choice;

                while (true) {
                    System.out.print("Enter your choice: ");
                    choice = sc.next();
                    sc.nextLine();

                    try {
                        AuthValidator.userChoiceValidation(choice, 2);
                        break; // valid input → exit loop
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                        System.out.println("Please try again.\n");
                    }
                }

                // Login
                if (Integer.parseInt(choice) == 1) {
                    String userId, password;
                    System.out.println("\n\t=====  LOGIN  =====");
                    while (true) {
                        while (true) {
                            System.out.println("User Id : ");
                            userId = sc.next();
                            sc.nextLine();
                            try {
                                AuthValidator.validateUserId(userId);
                                break;
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error " + e.getMessage());
                            }

                        }

                        while (true) {
                            System.out.println("Password : ");
                            password = sc.next();
                            sc.nextLine();
                            try {
                                AuthValidator.validatePassword(password);
                                break;
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error " + e.getMessage());
                            }
                        }

                        boolean userAvailable = false;

                        try {
                            userAvailable = auth.login(userId, password, db);
                        } catch (Exception e) {
                            userAvailable = false;
                            System.out.println(e.getMessage());
                            System.out.println("Try Again\n");
                        }

                        if (userAvailable) {
                            System.out.println("\n\t===== Welcome =====");
                            break;
                        }
                    }
                    String choice1;
                    System.out.println("1. Book New Ticket \t 2. Cancel Ticket");
                    while (true) {
                        System.out.print("Enter your choice: ");
                        choice1 = sc.next();
                        sc.nextLine();

                        try {
                            AuthValidator.userChoiceValidation(choice1, 2);
                            break; // valid input → exit loop
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                            System.out.println("Please try again.\n");
                        }
                    }
                    // Ticket booking
                    if (Integer.parseInt(choice1) == 1) {
                        System.out.println("\n  ===  New Ticket Booking  ===");
                        String start, end;

                        System.out.println("Enter starting : ");
                        start = sc.next();
                        sc.nextLine();
                        System.out.println("Enter destination : ");
                        end = sc.next();
                        sc.nextLine();

                        Pair p = t1.checkFeasibleJourney(start, end);
                        int stIdx = p.first, endIdx = p.second;
                        if (stIdx == -1) {
                            System.out.println("No Trains availabe\n");
                            continue;
                        } else {
                            System.out.println("\n Train with id <" + t1.getTrainId() + "> is available");
                        }

                        // Displaying the seat matrix
                        bs.showSeatMatrix(t1, stIdx, endIdx, db);

                        String seatNo;
                        while (true) {
                            System.out.println("\nEnter seat No : ");
                            seatNo = sc.next();
                            sc.nextLine();
                            try {
                                AuthValidator.userChoiceValidation(seatNo, t1.getMaxSeats());
                                break;
                            } catch (Exception e) {
                                System.out.println("Enter a valid seat number !");
                            }
                        }

                        try {
                            bs.bookTicket(userId, t1.getTrainId(), Integer.parseInt(seatNo), start, end, random, db);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("=======  Cancel Ticket  =======");
                        String ticketId;
                        while (true) {
                            System.out.println("Enter Ticket Id : ");
                            ticketId = sc.next();
                            try {
                                AuthValidator.userChoiceValidation(ticketId, 99999);
                                break;
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }

                        bs.cancelTicket(userId, ticketId, db);
                    }

                    // Registration
                } else if (Integer.parseInt(choice) == 2) {
                    String name, age, userId, password;

                    while (true) {
                        System.out.println("Enter name : ");
                        name = sc.nextLine();
                        try {
                            AuthValidator.validateName(name);
                            break;
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error " + e.getMessage());
                        }

                    }

                    while (true) {
                        System.out.print("Enter age: ");
                        age = sc.next();
                        sc.nextLine();

                        try {
                            AuthValidator.ageValidation(age);
                            break; // valid input → exit loop
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                            System.out.println("Please try again.\n");
                        }
                    }

                    while (true) {
                        System.out.println("New user Id (lowercase only) : ");
                        userId = sc.next();
                        sc.nextLine();

                        try {
                            AuthValidator.validateUserId(userId);
                            userId = userId.toLowerCase();
                            break; // valid input → exit loop
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                            System.out.println("Please try again.\n");
                        }
                    }
                    while (true) {
                        System.out.println("Enter new password : ");
                        password = sc.next();
                        sc.nextLine();
                        try {
                            AuthValidator.validatePassword(password);
                            break; // valid input → exit loop
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                            System.out.println("Please try again.\n");
                        }
                    }

                    // New User creation
                    newUser = new User(name, Integer.parseInt(age), userId, password);

                    auth.registerNewUser(newUser, db);

                    System.out.println("   Now you can login and Book Tickets\n");

                }
            } else if (Integer.parseInt(userChoice) == 3) {
                String userId;
                System.out.println("Enter your userId : ");
                userId = sc.next();
                sc.nextLine();
                AuthValidator.validateUserId(userId);

                bs.loadTickets(db, userId);
            }

            else {
                System.out.println("\n-----Thank You... Visit Again------\n");
                break;
            }
        }

        sc.close();
    }
}
