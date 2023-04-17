package server;

import javafx.util.Pair;

import java.io.*;
import server.models.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Cette classe implémente un serveur qui écoute sur un port donné et traite les commandes envoyées par les clients.
 */
public class Server {
    /**
     * Constante représentant la commande INSCRIRE.
     */

    public final static String REGISTER_COMMAND = "INSCRIRE";

    /**
     * Constante représentant la commande CHARGER.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Crée un nouveau serveur qui écoute les connexions sur le port spécifié.
     * @param port le numéro de port sur lequel écouter les connexions
     * @throws IOException si une erreur se produit lors de la création du serveur
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Ajoute un gestionnaire d'événements à la liste des gestionnaires enregistrés.
     * @param h le gestionnaire d'événements à ajouter
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Alertes tous les gestionnaires d'événements enregistrés avec la commande et les arguments spécifiés.
     * @param cmd la commande à envoyer aux gestionnaires d'événements
     * @param arg les arguments de la commande à envoyer aux gestionnaires d'événements
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Exécute le serveur en boucle infinie en acceptant les connexions de clients,
     * puis en écoutant les commandes reçues et en se déconnectant lorsque la communication est terminée.
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Écoute les données envoyées par le client et alerte les gestionnaires d'événements
     * enregistrés avec la commande et les arguments spécifiés.
     * @throws IOException si une erreur de communication se produit avec le client
     * @throws ClassNotFoundException si la classe d'un objet reçu du client ne peut pas être trouvée
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Traite une ligne de commande en la divisant en une commande et des arguments.
     * @param line la ligne de commande à traiter
     * @return une paire contenant la commande et les arguments
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Cette méthode ferme les flux de sortie et d'entrée de l'objet Socket client
     * ainsi que la socket client elle-même.
     * @throws IOException si une erreur se produit lors de la fermeture des flux de
     * sortie ou d'entrée ou de la socket client.
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Cette méthode gère les événements en fonction de la commande et de l'argument
     * fournis. Si la commande est "INSCRIRE", la méthode appelle la méthode
     * handleRegistration(), sinon, si la commande est "CHARGER", la méthode appelle
     * la méthode handleLoadCourses() avec l'argument donné.
     * @param cmd la commande fournie.
     * @param arg l'argument fourni.
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/cours.txt"))) {
            List<Course> courseList = reader.lines()
                    .map(line -> line.split("\t"))
                    .filter(splitLine -> splitLine.length == 3)
                    .filter(splitLine -> splitLine[2].equals(arg))
                    .map(splitLine -> new Course(splitLine[1], splitLine[0], splitLine[2]))
                    .collect(Collectors.toList());
            objectOutputStream.writeObject(courseList);
        } catch (IOException e) {
            System.err.println("Error while reading file: " + e.getMessage());
        }
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        try {
            RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();
            Course course = registrationForm.getCourse();
            boolean isRegistered = false;
            
            // Vérifier si l'étudiant est déjà inscrit dans le cours
            BufferedReader reader = new BufferedReader(new FileReader("data/inscription.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("\t");
                if (splitLine[1].equals(course.getCode()) && splitLine[2].equals(registrationForm.getMatricule())) {
                    isRegistered = true;
                    break;
                }
            }
            reader.close();
            
            // Si l'étudiant est déjà inscrit, retourner un message d'erreur
            if (isRegistered) {
                objectOutputStream.writeObject("\nErreur : l'étudiant est déjà inscrit dans ce cours.");
                return;
            }
            
            // Si l'étudiant n'est pas inscrit, ajouter l'inscription au fichier d'inscriptions
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/inscription.txt", true));
            String registration = course.getSession() + "\t" + course.getCode() + "\t" +
                    registrationForm.getMatricule() + "\t" + registrationForm.getPrenom() + "\t" +
                    registrationForm.getNom() + "\t" + registrationForm.getEmail() + "\n";
            writer.write(registration);
            writer.close();
            objectOutputStream.writeObject("\nFélicitations! Inscription réussie de " + registrationForm.getPrenom() +
                    " au cours " + registrationForm.getCourse().getCode() + ".");
        }
        catch (IOException e) {
            System.out.println("Erreur IO: " + e);
        }
        catch (ClassNotFoundException e) {
            System.out.println("Class pas trouvé:" + e);
        }
    }
}

