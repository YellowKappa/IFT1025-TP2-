
package controller;

import javafx.scene.control.Alert;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Cette classe représente le contrôleur de l'application. Elle contient les méthodes permettant d'envoyer des requêtes au
 * serveur et de vérifier la validité des données entrées par l'utilisateur.
 */
public class Controller {

    /**
     *l'hôte par défaut pour se connecter au serveur.
     */
    public final String host = "localhost";
    /**
     * Le port par défaut pour se connecter au serveur.
     */
    public final int port = 1337;

    /**
     * Cette méthode envoie une demande d'enregistrement au serveur avec les informations d'inscription fournies et renvoie la réponse reçue du serveur.
     * @param host l'hôte pour se connecter au serveur.
     * @param port le port pour se connecter au serveur.
     * @param command la commande à envoyer au serveur.
     * @param registrationForm le formulaire d'inscription contenant les informations d'inscription à envoyer au serveur
     * @return la réponse reçue du serveur.
     */
    public String runRegisterReqeust(String host, int port, String command, RegistrationForm registrationForm) {
        String response="";
        try {
            // Connecter au serveur
            Socket socket = new Socket(host, port);

            // Créer un flux de sortie pour envoyer des données au serveur
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(command);
            objectOutputStream.writeObject(registrationForm);

            // Créer un flux d'entrée pour recevoir des données du serveur
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            // Lire la réponse du serveur
            response = (String) objectInputStream.readObject();

            // Fermer les flux et le socket
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Cette méthode charge les données de cours pour une session donnée à partir du serveur et les renvoie sous forme de liste de cours.
     * @param session la session pour laquelle charger les données de cours.
     * @param host l'hôte pour se connecter au serveur.
     * @param port le port pour se connecter au serveur.
     * @return la liste de cours chargée à partir du serveur.
     */
    public List<Course> loadCourseData(String session,String host,int port) {
        List<Course> response=null;
        String LOAD_COMMAND = "CHARGER";
        String command = LOAD_COMMAND;
        command += " " + session;

        try {
            // Se connecter au serveur
            Socket socket = new Socket(host, port);

            // Créer un flux de sortie pour envoyer des données au serveur
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            // Envoyer la commande au serveur
            objectOutputStream.writeObject(command);

            // Créer un flux d'entrée pour recevoir les données du serveur
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            // Lire la réponse du serveur
            response = (List<Course>) objectInputStream.readObject();

            // Fermer les flux et le socket
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Cette méthode vérifie si les données entrées sont valides.
     * @param email_ L'adresse email à vérifier.
     * @param matri_ Le numéro de matricule à vérifier.
     * @return Retourne true si les données sont valides, sinon false.
     */
    public boolean checkValidData(String email_, String matri_) {
        if (!email_.endsWith("@umontreal.ca")) {

            showAlertMessage("Erreur : Adresse email invalide. L'adresse email doit se terminer par '@umontreal.ca'.");
            return false;
        }
        if (matri_.length() != 8) {
            System.out.println("Erreur: La matricule est invalide");
            showAlertMessage("Erreur: La matricule doit être 8 charactères de long");
            return false;
        }
        if (!matri_.matches("\\d+")) {
            showAlertMessage("Erreur: La matricule ne doit pas contenir de lettres");
            return false;
        }
        return true;
    }

    /**
     * Cette méthode affiche une boîte de dialogue contenant le message spécifié.
     * @param message Le message à afficher.
     */
    public void showAlertMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Afficher la boîte de dialogue et attendre que l'utilisateur clique sur le bouton OK.
        alert.showAndWait();

    }

}
