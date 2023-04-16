package server;

/**
 * Cette classe contient la méthode principale pour lancer le serveur.
 */
public class ServerLauncher {
    /**
     * Port utilisé pour le serveur.
     */
    public final static int PORT = 1337;

    /**
     * La méthode principale qui lance le serveur et affiche un message indiquant
     * que le serveur est en cours d'exécution.
     * @param args les arguments de la ligne de commande (inutilisés).
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}