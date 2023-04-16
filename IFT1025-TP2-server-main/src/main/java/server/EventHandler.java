package server;

/**
 * Cette interface fonctionnelle définit une méthode de traitement qui peut être
 * utilisée pour gérer différents événements du serveur.
 */
@FunctionalInterface
public interface EventHandler {

    void handle(String cmd, String arg);
}

