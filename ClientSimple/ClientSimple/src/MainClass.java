import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principale pour le client simple d'inscription de cours de l'UDEM.
 */
public class MainClass {

    /**
     * Liste des cours disponibles pour la session sélectionnée.
     */
    public static List<Course> cources = null;

    /**
     * Session sélectionnée par l'utilisateur.
     */
    private static String sessionSelected="";

    /**
     * Méthode principale qui permet à l'utilisateur d'interagir avec le portail d'inscription.
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean quit = false;
        while (!quit) {
            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
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
            int subChoice=1;

            do{
                System.out.println("1. Consulter les cours offerts pour une autre session");
                System.out.println("2. Inscription à un cours");
                System.out.print("> Choix: ");
                subChoice = input.nextInt();
                if (subChoice == 1) {
                    break;
                } else if (subChoice == 2) {
                    registerUser(input);
                    quit = true;
                    break;
                }else{
                    System.out.println("Erreur: Ceci est un choix invalide");
                }
            }while(subChoice>2|| subChoice<1);

        }
    }

    /**
     * Cette méthode charge la liste des cours disponibles pour la session sélectionnée
     * à partir du serveur et affiche la liste des cours à l'utilisateur.
     * @param choice Le choix de l'utilisateur pour la session.
     */
    public static void getCourses(int choice) {

        String LOAD_COMMAND = "CHARGER";
        String command = LOAD_COMMAND;

        // Sélection de la session
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

        // Récupération des cours disponibles pour la session sélectionnée
        new LoadRequest().runLoadReqeust("localhost", 1337, command);
    }

    /**
     * Cette méthode permet à l'utilisateur de s'inscrire à un cours en particulier.
     * @param input Le scanner utilisé pour lire les entrées de l'utilisateur.
     */
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

    /**
     * Cette méthode permet de valider si un code de cours est bien formé.
     * @param courseCode le code de cours à valider
     * @return true si le code de cours est bien formé, false sinon
     */
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
        System.out.println("Erreur: Le code du cours saisie est invalide ou n'est pas disponible dans la session choisie");
        return false;
    }

    /**
     * Cette méthode permet de valider si une adresse email est bien formée.
     * @param email l'adresse email à valider
     * @return true si l'adresse email est bien formée, false sinon
     */
    public static boolean validateEmail(String email) {
        if (!email.endsWith("@umontreal.ca")) {
            System.out.println("Erreur: L'adresse email doit finir par '@umontreal.ca'.");
            return false;
        }
        return true;
    }

    /**
     * Cette méthode permet de valider si un matricule est bien formé.
     * @param matricule le matricule à valider
     * @return true si le matricule est bien formé, false sinon
     */
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
