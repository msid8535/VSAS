// Main.java
import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FilenameUtils;

public class Main {
    private static User currentUser;

    //Colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[31m\u001B[32m";
    public static final String ANSI_RED_BOLD = "\u001B[31m\u001B[1m";

    public static void main(String[] args) {
        if (!DatabaseHelper.isDatabaseCreated()) {
            System.out.println("Database not found. Creating new database and tables...");
            // Create tables since the database is new
            DatabaseHelper.createTables();
        } else {
            //System.out.println("Database already exists. No need to create tables.");
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Welcome! Please choose an option:");
            System.out.println("1. Log in");
            System.out.println("2. Sign up");
            System.out.println("3. Continue as Guest");
            System.out.println("0. Exit");
            System.out.print("\nEnter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    login(scanner);
                    break;
                case 2:
                    signUp(scanner);
                    break;
                case 3:
                    loginAsGuest();
                    break;
                case 0:
                    closeProgram();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    // Method for user login
    public static void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = User.authenticateUser(username, password);
        if (user != null) {
            System.out.println(ANSI_GREEN + "Login successful!" + ANSI_RESET + "\n");
            setCurrentUser(user);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mainMenu();
        } else {
            System.out.println(ANSI_RED_BOLD + "Invalid username or password. Please try again." + ANSI_RESET);
        }
    }

    public static void loginAsGuest() {
        setCurrentUser(User.getGuestUser());
        mainMenu();
    }

    public static void signUp(Scanner scanner) {
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (!username.trim().isEmpty()) {
                if (User.getUserByName(username) != null) {
                    System.out.println(ANSI_RED_BOLD + "Username already exists. Please choose a different username." + ANSI_RESET);
                } else {
                    break;
                }
            } else {
                System.out.println(ANSI_RED_BOLD + "Username cannot be empty. Please try again." + ANSI_RESET);
            }
        }

        String password;
        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (!password.trim().isEmpty()) {
                break;
            } else {
                System.out.println(ANSI_RED_BOLD + "Password cannot be empty. Please try again." + ANSI_RESET);
            }
        }

        String email;
        while (true) {
            System.out.print("Enter email: ");
            email = scanner.nextLine();
            if (!email.trim().isEmpty() && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                break;
            } else {
                System.out.println(ANSI_RED_BOLD + "Invalid email format or empty email. Please try again." + ANSI_RESET);
            }
        }

        String fullName;
        while (true) {
            System.out.print("Enter Full Name: ");
            fullName = scanner.nextLine();
            if (!fullName.trim().isEmpty()) {
                break;
            } else {
                System.out.println(ANSI_RED_BOLD + "Full Name cannot be empty. Please try again." + ANSI_RESET);
            }
        }

        String phoneNumber;
        while (true) {
            System.out.print("Enter Phone Number: ");
            phoneNumber = scanner.nextLine();
            if (!phoneNumber.trim().isEmpty() && phoneNumber.matches("^\\+?[0-9]{7,15}$")) {
                break;
            } else {
                System.out.println(
                        ANSI_RED_BOLD +
                        "Invalid phone number format or empty phone number. Please enter a valid phone number (7-15 digits, optional +)." +
                        ANSI_RESET);
            }
        }

        User newUser = new User(username, password, email, fullName, phoneNumber, UserType.REGISTERED_USER);
        newUser.saveToDatabase();

        System.out.println(ANSI_GREEN + "Account created successfully! You can now log in with it." + ANSI_RESET);
    }

    private static void mainMenu() {
        int menuChoice;
        while (true) {
            menuChoice = displayMenu();

            switch (menuChoice) {
                case 1:
                    viewScrolls();
                    break;
                case 2:
                    filterScrollsMenu();
                    break;
                case 3:
                    if (currentUser.getUserType() == UserType.GUEST) {
                        addScrollGuest();
                    } else {
                        addScroll();
                    }
                    break;
                case 4:
                    editScroll();
                    break;
                case 5:
                    removeScroll();
                    break;
                case 6:
                    System.out.println("Here we implement functionality to view log file");
                    break;
                case 7:
                    downloadScrolls(true);
                    break;
                case 8:
                    updateProfileMenu();
                    break;
                case 9:
                    viewAllUsers();
                    break;
                case 10:
                    if (!currentUser.isUserAdmin()) {
                        System.out.println("Invalid Operation");
                        break;
                    }
                    System.out.println("Admin - Add a new user");
                    signUp(new Scanner(System.in));
                    break;
                case 11:
                    deleteUser();
                    break;
                case 12:
                    scrollsStatsMenu();
                    break;
                case 0:
                    closeProgram();
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
                    break;
            }
        }
    }

    public static void viewAllUsers() {
        if (!currentUser.isUserAdmin()) {
            System.out.println("Invalid Operation");
            return;
        }

        List<User> users = User.getAllUsers();
        System.out.println("\nThe users in this application are: ");
        displayUsersTable(users);

        returnToMenu();
    }

    public static void deleteUser() {
        if (!currentUser.isUserAdmin()) {
            System.out.println("Invalid Operation");
            System.out.println();
            return;
        }

        List<User> allUsers = User.getAllUsers();
        System.out.println("\nThe users in this application are: ");
        displayUsersTable(allUsers);

        Scanner scanner = new Scanner(System.in);
        String usernameToRemove;

        while (true) {
            System.out.print("\nEnter the username of the user you want to remove: ");
            usernameToRemove = scanner.nextLine();
            if (usernameToRemove.equals("admin") || usernameToRemove.equals("Guest")) {
                System.out.println("You cannot remove this user.");
            } else {
                break;
            }
        }

        boolean success = User.removeUserFromDatabase(usernameToRemove);
        if (success) {
            System.out.println("User " + usernameToRemove + " has been removed.");
            ScrollLogger.logActionToFile(currentUser, "removed user:", usernameToRemove);
        } else {
            System.out.println("Failed to remove user " + usernameToRemove + ". They may not exist.");
        }

        returnToMenu();
    }

    public static void viewScrolls() {
        Scanner scanner = new Scanner(System.in);
        List<Scroll> scrolls = Scroll.getAllScrollsFromDatabase();
        
        if (scrolls.isEmpty()) {
            System.out.println("No scrolls found in the database.");
            returnToMenu();
            return;
        }
        
        System.out.println("\nAll Scrolls:");
        displayScrollsTable(scrolls);
        if (currentUser.getUserType() != UserType.GUEST) {
            System.out.print("\nPress enter to download a scroll, or enter 0 to return to the main menu: ");
            String userInput = scanner.nextLine();
            if (userInput.equals("0")) {
                System.out.println();
                return;
            }

            downloadScrolls(false);
        } else {
            returnToMenu();
        }
    }
    private static void filterScrollsMenu() {
        Scanner scanner = new Scanner(System.in);
        ScrollFilter filter = new ScrollFilter();

        while (true) {
            System.out.println("\nFilter Options:");
            System.out.println("1. Filter by Name");
            System.out.println("2. Filter by File Type");
            System.out.println("3. Filter by Upload Date Range");
            System.out.println("4. Filter by Minimum Downloads");
            System.out.println("5. Sort Results");
            System.out.println("6. Apply Filters and View Results");
            System.out.println("7. Clear All Filters");
            System.out.println("0. Back to View Menu");

            System.out.print("\nEnter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter name to search (partial match): ");
                    filter.setName(scanner.nextLine());
                    break;

                case 2:
                    System.out.print("Enter file type (e.g., pdf, txt): ");
                    filter.setFileType(scanner.nextLine());
                    break;

                case 3:
                    System.out.print("Enter start date (dd-MM-yyyy) or press enter to skip: ");
                    String startDate = scanner.nextLine();
                    if (!startDate.isEmpty()) {
                        filter.setStartDate(startDate);
                    }

                    System.out.print("Enter end date (dd-MM-yyyy) or press enter to skip: ");
                    String endDate = scanner.nextLine();
                    if (!endDate.isEmpty()) {
                        filter.setEndDate(endDate);
                    }
                    break;

                case 4:
                    System.out.print("Enter minimum number of downloads: ");
                    filter.setMinDownloads(scanner.nextInt());
                    scanner.nextLine(); // Consume newline
                    break;

                case 5:
                    System.out.println("Sort by:");
                    System.out.println("1. Name");
                    System.out.println("2. Upload Date");
                    System.out.println("3. Download Count");
                    System.out.print("Enter choice: ");
                    int sortChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (sortChoice) {
                        case 1:
                            filter.setOrderBy("name");
                            break;
                        case 2:
                            filter.setOrderBy("uploadDate");
                            break;
                        case 3:
                            filter.setOrderBy("downloadCount");
                            break;
                        default:
                            System.out.println("Invalid sort option.");
                            break;
                    }

                    System.out.print("Sort descending? (y/n): ");
                    filter.setDescending(scanner.nextLine().toLowerCase().startsWith("y"));
                    break;

                case 6:
                    List<Scroll> filteredScrolls = Scroll.filterScrolls(filter);
                    if (filteredScrolls.isEmpty()) {
                        System.out.println("\nNo scrolls found matching the specified criteria.");
                    } else {
                        System.out.println("\nFiltered Results:");
                        displayScrollsTable(filteredScrolls);

                        System.out.println();

                        if (currentUser.getUserType() != UserType.GUEST) {
                            System.out.print("Press enter to download a scroll, or enter 0 to return: ");
                            String userInput = scanner.nextLine();
                            if (userInput.equals("0")) {
                                System.out.println();
                            } else {
                                downloadScrolls(false);
                                return;
                            }
                        } else {
                            System.out.print("Press enter to return: ");
                            scanner.nextLine();
                        }

                    }
                    break;

                case 7:
                    filter = new ScrollFilter();
                    System.out.println("All filters cleared.");
                    break;

                case 0:
                    return;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
    // Method for updating user profile
    private static void updateProfileMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Update your profile:");
        System.out.println("1. Update Username");
        System.out.println("2. Update Password");
        System.out.println("3. Update Email");
        System.out.println("4. Update Full Name");
        System.out.println("5. Update Phone Number");
        System.out.println("0. Go back to main menu");

        int choice;

        while (true) {
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (choice <= 4 && choice >= 0) {
                break;
            }
        }

        switch (choice) {
            case 1:
                System.out.print("Enter new username: ");
                String newUsername = scanner.nextLine();
                currentUser.setUsername(newUsername);
                System.out.println("Username updated successfully.");
                break;
            case 2:
                System.out.print("Enter new password: ");
                String newPassword = scanner.nextLine();
                currentUser.setPassword(newPassword);
                System.out.println("Password updated successfully.");
                break;
            case 3:
                System.out.print("Enter new email: ");
                String newEmail = scanner.nextLine();
                currentUser.setEmail(newEmail);
                System.out.println("Email updated successfully.");
                break;
            case 4:
                System.out.print("Enter new full name: ");
                String newFullName = scanner.nextLine();
                currentUser.setFullName(newFullName);
                System.out.println("Full name updated successfully.");
                break;
            case 5:
                System.out.print("Enter new phone number: ");
                String newPhoneNumber = scanner.nextLine();
                currentUser.setPhoneNumber(newPhoneNumber);
                System.out.println("Phone number updated successfully.");
                break;
            case 0:
                System.out.println();
                return; // Go back to main menu
        }

        // Print out the updated user details
        printUserDetails(currentUser);
        returnToMenu();
    }

    // Method to print out user details
    private static void printUserDetails(User user) {
        System.out.println("\nUpdated User Details:");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Full Name: " + user.getFullName());
        System.out.println("Phone Number: " + user.getPhoneNumber());
        System.out.println("User Type: " + user.getUserType());
    }

    public static void closeProgram() {
        System.exit(0);
    }

    public static int displayMenu() {
        printMenuGreeting();
        System.out.println("Select one of the following options by typing in a number:\n");

        System.out.println("Display menu options:\n"
                + "1. View All Available Scrolls\n"
                + "2. Filter search for scrolls");
        if (currentUser.getUserType() == UserType.GUEST) {
            System.out.println("3. Add Scroll (Up to 10 lines)");
        }

        if (currentUser.getUserType() == UserType.REGISTERED_USER || currentUser.getUserType() == UserType.ADMIN) {
            System.out.println("3. Add Scroll\n"
                    + "4. Edit Scroll\n"
                    + "5. Remove Scroll\n"
                    + "6. View Scrolls\n"
                    + "7. Download Scrolls\n"
                    + "8. Update Profile");
        }

        if (currentUser.getUserType() == UserType.ADMIN) {
            System.out.println("\nAdmin options:\n"
                    + "9. View all users\n"
                    + "10. Add User\n"
                    + "11. Delete User\n"
                    + "12. View scroll stats");
        }

        System.out.println("\n0. Exit application");

        Scanner scanner = new Scanner(System.in);
        int userChoice;

        while (true) {
            System.out.print("\nEnter your choice: ");

            userChoice = scanner.nextInt();

            if (userChoice <= 3) {
                return userChoice;
            } else if (userChoice <= 8 && (currentUser.getUserType() != UserType.GUEST)) {
                return userChoice;
            } else if (userChoice <= 12 && (currentUser.getUserType() == UserType.ADMIN)) {
                return userChoice;
            } else {
                System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private static void printMenuGreeting() {
        System.out.println("Welcome, " + currentUser.getUsername());
        if (currentUser.getUserType() == UserType.REGISTERED_USER || currentUser.getUserType() == UserType.GUEST) {
            System.out.println("You are currently logged in as a " + currentUser.getUserType().toString().toLowerCase() + ".");
        } else {
            System.out.println("You are currently logged in as an " + currentUser.getUserType().toString().toLowerCase() + ".");
        }
        System.out.println();
    }

    public static void removeScroll() {
        List<Scroll> listofscrolls = Scroll.getAllScrollsFromDatabase();

        if (listofscrolls.isEmpty()) {
            System.out.println("No scrolls found in the database.");
            returnToMenu();
            return;
        }

        List<Scroll> userscrolls = new ArrayList<>();
        List<String> names = new ArrayList<>();
        String currUploaderID;
        String currUserID = currentUser.getIdKey();
        int l = 0;
        while (l < listofscrolls.size()) {
            Scroll curr = listofscrolls.get(l);
            currUploaderID = curr.getUploaderId();
            //curr.getUploaderId() == currentUser.getIdKey()

            if (currUploaderID.equals(currUserID)) {
                //System.out.println("added to lists");
                userscrolls.add(curr);
                names.add(curr.getName());
            }
            l += 1;
        }

        System.out.println("Your uploaded scrolls: ");
        displayScrollsTable(userscrolls);
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        String scrollChoice;
        while (true) {
            System.out.print("Enter the name of the scroll you want to delete\n" +
                    "Or press enter to return to the main menu: ");
            scrollChoice = scanner.nextLine();
            if (scrollChoice.isEmpty()) {
                System.out.println();
                return;
            } else if (names.contains(scrollChoice)) {
                Scroll currScroll = Scroll.getScrollByName(scrollChoice);
                String scrollName = currScroll.getName(); //we need to get the scroll name for logging purposes
                Scroll.removeFromDatabase(currScroll.getScrollId());
                System.out.println(ANSI_GREEN + "Scroll removed." + ANSI_RESET);
                ScrollLogger.logActionToFile(currentUser, "removed scroll:", scrollName);
                returnToMenu();
                return;
            } else {
                System.out.println(ANSI_RED_BOLD + "Scroll does not exist!" + ANSI_RESET);
            }
        }
    }

    private static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void addScroll() {
        byte[] fileBytes;
        Scanner scanner = new Scanner(System.in);
        try {
            //Get file path from user input
            System.out.print("Please enter the file path: ");
            String filePathString = scanner.nextLine();

            //Create a File object
            File file = new File(filePathString);

            //Check if the file exists and is a valid file
            if (!file.exists() || !file.isFile()) {
                System.out.println(file.getPath());
                throw new FileNotFoundException("The specified file does not exist or is not a valid file.");
            }

            //Convert the file to a Path object
            Path filePath = file.toPath();

            //Read the file into a byte array
            fileBytes = Files.readAllBytes(filePath);

            // Output the size of the file in bytes
            //System.out.println("File successfully read. Size: " + fileBytes.length + " bytes");

            //let user name their scroll
            String scrollName;
            System.out.print("Enter the name of the scroll: ");
            while (true) {
                scrollName = scanner.nextLine();
                if (scrollName.isEmpty() || scrollName.equals("0")) {
                    System.out.print("Please enter a valid name: ");
                } else if (Scroll.getScrollByName(scrollName) != null) {
                    System.out.print("Scroll name already exists! Please enter a new name: ");
                } else {
                    break;
                }
            }

            String currIdKey = currentUser.getIdKey();
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            Scroll newScroll = new Scroll(scrollName, currIdKey, fileBytes, date, FilenameUtils.getExtension(filePathString), 0, 0);
            newScroll.saveToDatabase();

            System.out.println(ANSI_GREEN + "Scroll has been saved to database!" + ANSI_RESET);
            ScrollLogger.logActionToFile(currentUser, "added scroll:", scrollName);
            returnToMenu();

        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());  // Handles missing or invalid file
        } catch (IOException e) {
            System.out.println("I/O error occurred while reading the file: " + e.getMessage());  // Handles issues like access errors
        } finally {
            // Close the scanner to avoid resource leaks
            //scanner.close();
        }
    }

    private static void returnToMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nPress enter to return to the main menu: ");
        scanner.nextLine();
        System.out.println();
    }


    public static void addScrollGuest() {
        byte[] fileBytes;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the file path: ");
        String filePathString = scanner.nextLine();
        int lengthOfFile = countLines(filePathString);
        if (lengthOfFile > 10) {
            System.out.print("File exceeds 10 line limit.");
            returnToMenu();
        } else {
            try {
                File file = new File(filePathString);
                if (!file.exists() || !file.isFile()) {
                    System.out.println(file.getPath());
                    throw new FileNotFoundException("The specified file does not exist or is not a valid file.");
                }
                Path filePath = file.toPath();
                fileBytes = Files.readAllBytes(filePath);

                //let user name their scroll
                String scrollName;
                System.out.print("Enter the name of the scroll: ");
                while (true) {
                    scrollName = scanner.nextLine();
                    if (scrollName.isEmpty() || scrollName.equals("0")) {
                        System.out.print("Please enter a valid name: ");
                    } else if (Scroll.getScrollByName(scrollName) != null) {
                        System.out.print("Scroll name already exists! Please enter a new name: ");
                    } else {
                        break;
                    }
                }

                //create user Guest if doesn't exist, if exists do nothing
                User guest = User.getUserByName("Guest");
                if (guest == null) {
                    guest = User.getGuestUser();
                    guest.saveToDatabase();
                }

                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                Scroll newScroll = new Scroll(scrollName, currentUser.getIdKey(), fileBytes, date, FilenameUtils.getExtension(filePathString), 0, 0);
                newScroll.saveToDatabase();

                System.out.println(ANSI_GREEN + "Scroll has been saved to database!" + ANSI_RESET);
                ScrollLogger.logActionToFile(currentUser, "added scroll:", scrollName);
                returnToMenu();

            } catch (FileNotFoundException e) {
                System.out.println("Error: " + e.getMessage());  // Handles missing or invalid file
                System.out.println(" ");
            } catch (IOException e) {
                System.out.println("I/O error occurred while reading the file: " + e.getMessage());
                System.out.println(" ");
            } finally {
            }
        }
    }

    public static int countLines(String filepath) {
        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            while (reader.readLine() != null) {
                lineCount++;
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        return lineCount;
    }

    public static void downloadScrolls(boolean displayAllScrolls) {
        Scanner scanner = new Scanner(System.in);
        List<Scroll> scrolls = Scroll.getAllScrollsFromDatabase();

        if (scrolls.isEmpty()) {
            System.out.println("No scrolls found in the database.");
            returnToMenu();
            return;
        }

        if (displayAllScrolls) {
            System.out.println("\nAll Scrolls:");
            displayScrollsTable(scrolls);
            System.out.println();
        } else {
            System.out.println();
        }

        String scrollName;
        Scroll scrollToDownload;

        while (true) {
            System.out.print("Enter name of scroll you would like to download OR 0 to exit: ");
            scrollName = scanner.nextLine();
            if (scrollName.isEmpty()) {
                System.out.println("Please enter a valid name.");
            } else if (scrollName.equals("0")) {
                return;
            } else if (Scroll.getScrollByName(scrollName) == null) {
                System.out.println("Scroll does not exist");
            } else {
                scrollToDownload = Scroll.getScrollByName(scrollName);
                break;
            }
        }

        System.out.println();

        if (scrollToDownload.getFileType().equals("txt")) {
            System.out.println("Preview of the file:");
            System.out.println(new String(scrollToDownload.getFileData()));
            System.out.println();
        } else {
            System.out.println("A preview is not available for this file\n");
        }

        System.out.println("Are you sure you want to download '"
                + scrollToDownload.getName()
                + "."
                + scrollToDownload.getFileType()
                + "'?");

        System.out.print("Press enter to download, or enter 0 to return to the main menu: \n");
        String userInput = scanner.nextLine();
        if (userInput.equals("0")) {
            System.out.println();
            return;
        }


        File downloadsFolder = Paths.get(System.getProperty("user.home"), "Downloads").toFile();

        File file = new File(downloadsFolder, createFileName(scrollToDownload));

        // Write the byte array to the file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(scrollToDownload.getFileData());
            System.out.println("File saved successfully at: " + file.getAbsolutePath());
            scrollToDownload.incrementDownloadCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ScrollLogger.logActionToFile(currentUser, "downloaded scroll:", scrollName);
        returnToMenu();
    }

    private static void displayScrollsTable(List<Scroll> scrolls) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.printf("%-16s | %-20s | %-15s | %-9s | %-20s\n", "Scroll ID", "Name", "Uploader ID", "File Type", "Upload Date");
        System.out.println("-----------------------------------------------------------------------------------");

        for (Scroll scroll : scrolls) {
            System.out.printf("%-16s | %-20s | %-15s | %-9s | %-20s\n",
                    scroll.getScrollId(),
                    scroll.getName(),
                    scroll.getUploaderId(),
                    scroll.getFileType(),
                    scroll.getUploadDate());
        }
        System.out.println("-----------------------------------------------------------------------------------");
    }

    private static void scrollsStatsMenu() {
        List<Scroll> scrolls = Scroll.getAllScrollsFromDatabase();

        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-16s | %-20s | %-15s | %-9s | %-20s | %-9s | %-9s\n", "Scroll ID", "Name", "Uploader ID", "File Type", "Upload Date", "Downloads", "Edits");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");

        for (Scroll scroll : scrolls) {
            System.out.printf("%-16s | %-20s | %-15s | %-9s | %-20s | %-9s | %-9s\n",
                    scroll.getScrollId(),
                    scroll.getName(),
                    scroll.getUploaderId(),
                    scroll.getFileType(),
                    scroll.getUploadDate(),
                    scroll.getDownloadCount(),
                    scroll.getEditCount());
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");

        returnToMenu();
    }

    private static void displayUsersTable(List<User> users) {
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-13s | %-20s | %-30s | %-30s | %-10s\n", "User ID", "Username", "Email", "Full Name", "Phone Number");
        System.out.println("----------------------------------------------------------------------------------------------------------------------");

        for (User user: users) {
            System.out.printf("%-13s | %-20s | %-30s | %-30s | %-10s\n",
                    user.getIdKey(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getPhoneNumber());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
    }

    private static String createFileName(Scroll scroll) {
        return scroll.getName() + "." + scroll.getFileType();
    }

    public static void editScroll() {
        List<Scroll> listofscrolls = Scroll.getAllScrollsFromDatabase();

        if (listofscrolls.isEmpty()) {
            System.out.println("No scrolls found in the database.");
            returnToMenu();
            return;
        }

        //this prints the scrolls the user added
        List<Scroll> userscrolls = new ArrayList<>();
        List<String> names = new ArrayList<>();
        String currUploaderID;
        String currUserID = currentUser.getIdKey();
        int l = 0;
        while (l < listofscrolls.size()) {
            Scroll curr = listofscrolls.get(l);
            currUploaderID = curr.getUploaderId();

            if (currUploaderID.equals(currUserID)) {
                userscrolls.add(curr);
                names.add(curr.getName());
            }
            l += 1;
        }
        System.out.println("Your uploaded scrolls: ");
        displayScrollsTable(userscrolls);
        System.out.println();


        //editing part
        Scanner scanner = new Scanner(System.in);
        String scrollChoice;
        while (true) { //ask for name of scroll
            System.out.print("Enter the name of the scroll you want to edit OR press enter to return to the main menu: \n");
            scrollChoice = scanner.nextLine();
            if (scrollChoice.isEmpty()) { //no input, returning to menu
                System.out.println();
                return;
            } else if (!names.contains(scrollChoice)) {
                System.out.println(ANSI_RED_BOLD + "Scroll does not exist." + ANSI_RESET);
            } else if (names.contains(scrollChoice)) { //we can go ahead with editing
                Scroll currScroll = Scroll.getScrollByName(scrollChoice);
                String scrollName = currScroll.getName();
                Scroll scrollToEdit = Scroll.getScrollById(currScroll.getScrollId());
                if (scrollToEdit.getFileType().equals("txt")) {
                    //append functionality
                    byte[] currBytes = scrollToEdit.getFileData();
                    System.out.println("Preview of the text file:");
                    System.out.println(new String(scrollToEdit.getFileData()));
                    byte[] initialArray = scrollToEdit.getFileData(); // Existing byte array
                    byte[] updatedArray = initialArray; //the array we will append to
                    System.out.print("Enter 'Y' if you want to erase the previous data in the text file: ");
                    String condition = scanner.nextLine();
                    if (condition.equals("Y")) { //this will erase the content before the user adds text
                        updatedArray = new byte[0];
                        scrollToEdit.setFileData(updatedArray);
                        System.out.println("Previous text erased.");
                        scrollToEdit.updateScrollInDatabase();
                    } else {
                        System.out.println("Previous text was NOT erased.");
                    }
                    byte[] newFileData = updatedArray;
                    try {
                        while (true) { // Append lines through input
                            System.out.println("\nCurrent file content: " + new String(updatedArray));
                            System.out.print("Enter the next line of text to add to the file OR enter 'stop' to stop adding text: \n");
                            String newLineOfText = scanner.nextLine();
                            if (newLineOfText.equals("stop")) {
                                System.out.println();
                                break;
                            } else {
                                updatedArray = appendLine(updatedArray, newLineOfText);
                                newFileData = updatedArray;
                                scrollToEdit.setFileData(newFileData); //setting as new filedata
                            }
                        }
                        System.out.println(ANSI_GREEN + "Scroll edited." + ANSI_RESET);
                        ScrollLogger.logActionToFile(currentUser, "edited scroll:", scrollName); //logging
                        scrollToEdit.incrementEditCount();
                        scrollToEdit.updateScrollInDatabase();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {//add new file through filepath
                        System.out.print("Enter the filepath of the replacement file:\n");
                        String newFilePath = scanner.nextLine();
                        //Create a File object
                        File file = new File(newFilePath);

                        //Check if the file exists and is a valid file
                        if (!file.exists() || !file.isFile()) {
                            System.out.println(file.getPath());
                            throw new FileNotFoundException("The specified file does not exist or is not a valid file.");
                        }

                        try {
                            Path filePath = file.toPath();
                            byte[] newFileBytes = Files.readAllBytes(filePath);
                            scrollToEdit.setFileData(newFileBytes); //setting as new filedata}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String newFileType = FilenameUtils.getExtension(newFilePath); //update file type
                        scrollToEdit.setFileType(newFileType); //set file type
                        scrollToEdit.incrementEditCount();
                        scrollToEdit.updateScrollInDatabase();
                        System.out.println(ANSI_GREEN + "Scroll edited." + ANSI_RESET);
                        ScrollLogger.logActionToFile(currentUser, "edited scroll:", scrollName); //logging
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Function to append a line to the existing byte array
    public static byte[] appendLine(byte[] originalArray, String line) throws IOException {
        // Create a ByteArrayOutputStream and write the original array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(originalArray);

        // Append the new line as bytes
        byteArrayOutputStream.write((line + "\n").getBytes(StandardCharsets.UTF_8));

        // Return the updated byte array
        return byteArrayOutputStream.toByteArray();
    }

}
