import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * usage
 new RegisterRequest().runRegisterReqeust("localhost", 1337,command,registrationForm);
 *
 */
public class RegisterRequest {

    /**
     * test the functionality
     * @param host
     * @param port
     * @param command
     * @param registrationForm
     */
    public void runRegisterReqeust(String host, int port, String command, RegistrationForm registrationForm){
        try {
            // Connect to server
            Socket socket = new Socket("localhost", 1337);

            // Create output stream to send data to server
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(command);
            objectOutputStream.writeObject(registrationForm);

            // Create input stream to receive data from server
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            // Read response from server
            String response = (String) objectInputStream.readObject();
            System.out.println(response);
            // Close streams and socket
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main1(String [] args){
        String REGISTER_COMMAND = "INSCRIRE";
        String LOAD_COMMAND = "CHARGER";
        // Send command to server
        String command = REGISTER_COMMAND;
        Course course = new Course("Programmation1","IFT1015","Automne");

        RegistrationForm registrationForm = new RegistrationForm("Jordon","Femlis","test@gm.co","34321234",course);



        new RegisterRequest().runRegisterReqeust("localhost", 1337,command,registrationForm);
    }
}
