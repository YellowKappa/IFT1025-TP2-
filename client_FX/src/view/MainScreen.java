package view;

import controller.Controller;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.models.Course;
import server.models.RegistrationForm;

import javax.swing.*;
import java.util.List;

/**
 * Classe principale de l'application, gérant l'affichage de la fenêtre et
 * l'affichage de l'interaction avec l'utilisateur utilisateur.
 */
public class MainScreen extends Application {

    // Les éléments graphiques
    private TableView<Course> table;
    private TextField prenom;
    private TextField nom;
    private TextField email;
    private TextField matricule;

    // Contrôleur pour communiquer avec le serveur
    Controller controller;

    /**
     *Méthode appelée lors du lancement de l'application et affiche le GUI de base.
     *@param primaryStage la fenêtre principale de l'application
     */
    @Override
    public void start(Stage primaryStage) {
        // Titre de l'application
        primaryStage.setTitle("Inscription Udem");

        // Initialiser le contrôleur
        controller = new Controller();
        // Créer le panneau de gauche avec le tableau et la barre de sélection
       table = new TableView<>();

        // Colonnes du tableau
        TableColumn<Course, String> nameCol = new TableColumn<>("Cours");
        nameCol.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getName();
            return new SimpleStringProperty(name);
        });
        
        nameCol.setPrefWidth(200);
        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(cellData -> {
            String cs = cellData.getValue().getCode();
            return new SimpleStringProperty(cs);
        });
        
        TableColumn<Course, String> session = new TableColumn<>("Session");
        session.setCellValueFactory(cellData -> {
            String s = cellData.getValue().getSession();
            return new SimpleStringProperty(s);
        });
        // Ajouter les colonnes au tableau
        table.getColumns().addAll(codeCol, nameCol, session);

        // Supprimer la troisième colonne (session) du tableau
        table.getColumns().remove(session);
        // Créer une étiquette pour afficher le message "Aucun contenu"
        Label noContentLabel = new Label("No content in table");

        // Définir l'étiquette comme propriété "placeholder" du tableau
        table.setPlaceholder(noContentLabel);
        // Créer un panneau vertical pour le tableau et la barre de sélection
        VBox leftPanel = new VBox(10, table, createSelectBar());
        leftPanel.setPadding(new Insets(10));

        // Créer le panneau de droite avec le formulaire d'inscription
        GridPane rightPanel = createRegistrationForm();
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(10));

        // Créer le panneau principal et y ajouter les panneaux de gauche et de droite
        BorderPane mainPanel = new BorderPane();
        mainPanel.setLeft(leftPanel);
        mainPanel.setCenter(rightPanel);

        // Créer la scène principale et afficher la fenêtre
        Scene scene = new Scene(mainPanel, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Méthode permettant de créer la barre de sélection de session.
     * @return la barre de sélection
     */
    public HBox createSelectBar() {
        ComboBox<String> selectBox = new ComboBox<>();
        selectBox.getItems().addAll("Automne", "Hiver", "Ete");
        selectBox.setPrefWidth(250);
        selectBox.setValue(selectBox.getItems().get(0));
        Button chooseButton = new Button("Charger");
        chooseButton.setOnAction(event -> {
            String session = selectBox.getValue();
            loadCourseData(session);
        });
        HBox selectBar = new HBox(10, selectBox, chooseButton);
        return selectBar;
    }
    /**
     * Cette méthode crée le formulaire d'inscription et le retourne sous forme de GridPane.
     * @return GridPane représentant le formulaire d'inscription
     */
    public GridPane createRegistrationForm() {
        
        prenom = new TextField();
        nom = new TextField();
        email = new TextField();
        matricule = new TextField();
        Button registerButton = new Button("Envoyer");
        registerButton.setOnAction(event -> register());
        GridPane form = new GridPane();
        form.addRow(0, new Label("Prenom:"), prenom);
        form.addRow(1, new Label("Nom:"), nom);
        form.addRow(2, new Label("Email:"), email);
        form.addRow(3, new Label("Matricule:"), matricule);
        form.addRow(4, registerButton);
        form.setHgap(10);
        form.setVgap(10);
        return form;
    }
    /**
     * Cette méthode gère l'envoi des données d'inscription.
     * Elle récupère les données entrées dans le formulaire d'inscription et les envoie au serveur.
     * Elle gère également les cas où des champs sont vides ou des données invalides sont entrées.
     */
    public void register() {
        TableView.TableViewSelectionModel<Course> selectionModel = table.getSelectionModel();
        // Récupérer l'item sélectionné
        Course selectedItem = selectionModel.getSelectedItem();
        // Vérifier si un item est sélectionné
        if (selectedItem != null) {
            // Print le cours sélectionné dans la console
            System.out.println("Cours sélectionné: " + selectedItem.getName());
        } else {
            System.out.println("No item selected");
            controller.showAlertMessage("Erreur: Vous devez selectionnez un cours!");
            return;
        }
        String firstName = prenom.getText();
        String nom_ = nom.getText();
        String email_ = email.getText();
        String matri_ = matricule.getText();
        
        if(!controller.checkValidData(email_,matri_)){
            return;
        }
        
        if (!firstName.isEmpty() && !nom_.isEmpty() && !email_.isEmpty() && !matri_.isEmpty()) {
            sendRegisterDataRequest(selectedItem, firstName, nom_, email_, matri_);
            prenom.clear();
            nom.clear();
            email.clear();
            matricule.clear();
        } else {
            JOptionPane.showMessageDialog(null, "Erreur: Tous les champs sont nécessaire");
            
        }
    }
    /**
     * Méthode principale pour lancer l'application JavaFX.
     * @param args les arguments passés à l'application lors du lancement
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Cette méthode charge les données de cours pour une session donnée.
     @param session la session pour laquelle les données doivent être chargées
     */
    public void loadCourseData(String session) {
        //String LOAD_COMMAND = "CHARGER";
        //String command = LOAD_COMMAND;
        //command += " " + session;

        // Lire la réponse du serveur
        List<Course> response = controller.loadCourseData(session, controller.host, controller.port);
        if (response == null || response.isEmpty()) {
            controller.showAlertMessage("Erreur: Le fichier cours.txt est introuvable.");
            return;
        }
        table.getItems().clear();
        for (Course c : response) {
            table.getItems().add(c);
        }
        
    }

    /**
     * Cette méthode envoie une requête d'inscription pour un cours donné et
     * affiche la réponse du serveur de la requête.
     * @param course le cours pour lequel l'inscription doit être envoyée
     * @param firstName le prénom de l'étudiant
     * @param nom_ le nom de l'étudiant
     * @param email_ l'adresse email de l'étudiant
     * @param matricule le numéro de matricule de l'étudiant
     */
    public void sendRegisterDataRequest(Course course, String firstName, String nom_, String email_, String matricule) {
        // Envoyer la commande au serveur
        String LOAD_COMMAND = "INSCRIRE";
        RegistrationForm registrationForm = new RegistrationForm(firstName, nom_, email_, matricule, course);
        String response = controller.runRegisterReqeust(controller.host, controller.port, LOAD_COMMAND, registrationForm);
        if (response.isEmpty()) {
            controller.showAlertMessage("Erreur : Le fichier inscription.txt est introuvable.");
        } else {
            controller.showAlertMessage(response);
        }
    }

}
