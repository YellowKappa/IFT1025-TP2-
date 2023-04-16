import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Run from this clas
 */
public class MainClass {

    public static List<Course> cources = null;
    private static String sessionSelected="";
    /**
     * sfd asdfak
     *
     * @param args
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean quit = false;
        while (!quit) {
            System.out.println("Bienvenue au portail d'inscription de cours de l'WEM ");
            int choice = 0;
            boolean validChoice=false;
            while (!validChoice){
                System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
                System.out.println("1. Automne ");
                System.out.println("2. Hiver");
                System.out.println("3. Ete");
                System.out.print("> Choix: ");
                choice = input.nextInt();
                if(choice<1 || choice>3){
                    validChoice=false;
                    System.out.println("Erreur: Ceci est un choix invalide");
                }else {
                    validChoice=true;
                }
            }
            getCourses(choice);

            System.out.println("1. Consulter les cours offerts pour une autre session");
            System.out.println("2. Inscription à un cours");
            System.out.print("> Choix: ");
            int subChoice = input.nextInt();
            if (subChoice == 1) {
                continue;
            } else if (subChoice == 2) {
                registerUser(input);
                quit = true;
            }
        }
    }

    public static void getCourses(int choice) {

        String LOAD_COMMAND = "CHARGER";
        String command = LOAD_COMMAND;
        switch (choice) {
            case 1:
                command += " " + "Automne";
                sessionSelected="Automne";
                System.out.println("Les cours offerts pendant la session d'automne sont: ");
                break;

            case 2:
                command += " " + "Hiver";
                sessionSelected="Hiver";
                System.out.println("Les cours offerts pendant la session d'Hiver sont: ");
                break;

            case 3:
                command += " " + "Ete";
                sessionSelected="Ete";
                System.out.println("Les cours offerts pendant la session d'Ete sont: ");
                break;
        }

        new LoadRequest().runLoadReqeust("localhost", 1337, command);
    }

    public static void registerUser(Scanner input) {
        System.out.print("Veuillez saisir votre prénom: ");
        String firstName = input.next();
        System.out.print("Veuillez saisir votre nom: ");
        String lastName = input.next();
        String email = "";
        do {
            System.out.print("Veuillez saisir votre email: ");
            email = input.next();
        } while (!validateEmail(email));

        String matricule = "";

        do {
            System.out.print("Veuillez saisir votre matricule: ");
            matricule = input.next();

        } while (!validateMatricule(matricule));
        String courseCode="";
        do {
            System.out.print("Veuillez saisir le code du cours: ");
            courseCode = input.next();
        } while (!validateCourseCode(courseCode));

        String REGISTER_COMMAND = "INSCRIRE";
        // Send command to server
        String command = REGISTER_COMMAND;
        Course course = new Course("Programmation1", courseCode, sessionSelected);
        RegistrationForm registrationForm = new RegistrationForm(firstName, lastName, email, matricule, course);
        new RegisterRequest().runRegisterReqeust("localhost", 1337, command, registrationForm);

    }

    private static boolean validateCourseCode(String courseCode) {
        try {
            LoadRequest.loadCoursesData(sessionSelected);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (Course c:
             cources) {
            if(c.getCode().equalsIgnoreCase(courseCode))
                return true;
        }
        return false;
    }

    public static boolean validateEmail(String email) {
        if (!email.endsWith("@umontreal.ca")) {
            System.out.println("Error: Invalid email address. The email address must end with '@umontreal.ca'.");
            return false;
        }
        return true;
    }

    public static boolean validateMatricule(String matricule) {
        if (matricule.length() != 8) {
            System.out.println("Erreur: La matricule est invalide");
            return false;
        }
        if (!matricule.matches("\\d+")) {
            System.out.println("Erreur: La matricule est invalide");
            return false;
        }
        return true;
    }
}
