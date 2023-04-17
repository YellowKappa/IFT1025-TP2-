import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Cette classe implémente une requête d'inscription. Elle envoie une demande d'inscription
 * à un serveur distant et reçoit une réponse en format String. Elle contient une méthode
 * principale qui crée un formulaire d'inscription et envoie une demande d'inscription
 * au serveur.
 */
public class RegisterRequest {

    /**
     * Cette méthode envoie une demande d'inscription au serveur spécifié, avec le formulaire d'inscription fourni.
     * @param host le nom d'hôte du serveur auquel envoyer la demande d'inscription.
     * @param port le port du serveur auquel envoyer la demande d'inscription.
     * @param command la commande à envoyer au serveur, ici "INSCRIRE" pour une demande d'inscription.
     * @param registrationForm le formulaire d'inscription à envoyer au serveur.
     */
    public void runRegisterReqeust(String host, int port, String command, RegistrationForm registrationForm){
        try {
            // Connecter au serveur
            Socket socket = new Socket("localhost", 1337);

            // Créer un flux de sortie pour envoyer des données au serveur
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(command);
            objectOutputStream.writeObject(registrationForm);

            // Créer un flux d'entrée pour recevoir des données du serveur
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            // Lire la réponse du serveur
            String response = (String) objectInputStream.readObject();
            System.out.println(response);
            // Fermer les flux et le socket
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * La méthode principale qui envoie une requête d'inscription au serveur.
     * @param args les arguments de la ligne de commande
     */
    public static void main1(String [] args){

        String REGISTER_COMMAND = "INSCRIRE";
        Course course = new Course("Programmation1","IFT1015","Automne");

        // Créer un formulaire d'inscription avec des informations d'utilisateur et de cours
        RegistrationForm registrationForm = new RegistrationForm("Jordon","Femlis","test@gm.co","34321234",course);

        // Envoyer la requête d'inscription au serveur
        new RegisterRequest().runRegisterReqeust("localhost", 1337,REGISTER_COMMAND,registrationForm);
    }
}
