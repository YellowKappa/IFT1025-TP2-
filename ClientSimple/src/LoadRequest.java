import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

public class LoadRequest {
    public LoadRequest() {
    }
    public void runLoadReqeust(String host,int port,String command){
        try {
            // Connect to server
            Socket socket = new Socket(host,port);

            // Create output stream to send data to server
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            // Send command to server

            objectOutputStream.writeObject(command);

            // Create input stream to receive data from server
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            // Read response from server
            List<Course> response = (List<Course>) objectInputStream.readObject();
            int i=1;
            for(Iterator var9 = response.iterator(); var9.hasNext(); ++i) {
                Course c = (Course)var9.next();
                System.out.println("" + i + " .\t" + c.getCode() + "\t" + c.getName());
            }

            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
        } catch (ClassNotFoundException | IOException var11) {
            var11.printStackTrace();
        }

    }
    public static void loadCoursesData(String fall) throws IOException, ClassNotFoundException {
        String host="localhost";
        int port=1337;
        String LOAD_COMMAND = "CHARGER";
        String command = LOAD_COMMAND;
        command+=" "+fall;
        // Connect to server
        Socket socket = new Socket(host,port);

        // Create output stream to send data to server
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        // Send command to server

        objectOutputStream.writeObject(command);

        // Create input stream to receive data from server
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        // Read response from server
        List<Course> response = (List<Course>) objectInputStream.readObject();
        MainClass.cources=response;
        // Close streams and socket
        objectOutputStream.close();
        objectInputStream.close();
        socket.close();

    }
    public static void main1(String [] args){
        String REGISTER_COMMAND = "INSCRIRE";
        String LOAD_COMMAND = "CHARGER";
        String command = LOAD_COMMAND;
        command+=" "+"Automne";
        new LoadRequest().runLoadReqeust("localhost", 1337,command);
    }
}
