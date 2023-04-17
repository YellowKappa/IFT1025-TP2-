import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * La classe LoadRequest permet de lancer une requête de chargement des cours depuis le serveur.
 */
public class LoadRequest {
    /**
     * Constructeur vide de la classe.
     */
    public LoadRequest() {
    }

    /**
     * Cette méthode permet de lancer une requête de chargement des cours depuis le serveur.
     * @param host L'hôte du serveur.
     * @param port Le port du serveur.
     * @param command La commande à envoyer au serveur.
     */
    public void runLoadReqeust(String host,int port,String command){
        try {
            // Se connecter au serveur
            Socket socket = new Socket(host,port);

            // Créer un flux de sortie pour envoyer des données au serveur
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            // Envoyer la commande au serveur

            objectOutputStream.writeObject(command);

            // Créer un flux d'entrée pour recevoir les données du serveur
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            // Lire la réponse du serveur
            List<Course> response = (List<Course>) objectInputStream.readObject();
            int i=1;
            for(Course c: response){
                System.out.println(i+" .\t"+c.getCode()+"\t"+c.getName());
                i++;
            }
            // Fermer les flux et le socket
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cette méthode permet de charger les données des cours à partir du serveur.
     * @param session La session pour laquelle charger les cours.
     * @throws IOException Si une erreur survient lors de la lecture ou de l'écriture des données.
     * @throws ClassNotFoundException Si une classe n'a pas été trouvée lors de la désérialisation.
     */
    public static void loadCoursesData(String session) throws IOException, ClassNotFoundException {
        String host="localhost";
        int port=1337;
        String LOAD_COMMAND = "CHARGER";
        String command = LOAD_COMMAND;
        command+=" "+session;
        // Se connecter au serveur
        Socket socket = new Socket(host,port);

        // Créer un flux de sortie pour envoyer des données au serveur
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        // Envoyer la commande au serveur
        objectOutputStream.writeObject(command);

        // Créer un flux d'entrée pour recevoir les données du serveur
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        // Lire la réponse du serveur
        List<Course> response = (List<Course>) objectInputStream.readObject();
        MainClass.cources=response;

        // Fermer les flux et la socket
        objectOutputStream.close();
        objectInputStream.close();
        socket.close();

    }

}
