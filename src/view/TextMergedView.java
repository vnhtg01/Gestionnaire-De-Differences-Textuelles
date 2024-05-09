package view;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.FileChooser;
import name.fraser.neil.plaintext.diff_match_patch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class TextMergedView extends BorderPane {

    /* créer deux propriétés textarea qui représentent les zones de saisie de mes deux textes*/
        private TextArea originalTextArea;
        private TextArea modifiedTextArea;
   /*deux volets de défilement pour contenir du texte avec leurs différences */
    private ScrollPane originalScrollPane;
    private ScrollPane modifiedScrollPane;
    private ScrollPane scrollPane;

    /* bouton importer */
    private Button importoriginaltextButton;
    private Button importedittextButton;

    /* bouton comaparer */
    private Button compareButton;
    /* AcceptAll */
    private Button acceptAllButton;
    /* RejectAll */
    private Button rejectAllButton;
    /* bouton de gestion des changements */
    private MenuItem managingchanges;
    
    private Button confirmEditButton;
	private Button openListFileOldButton;
	
	private Stage stage;



        /* ------------------------------ créer un constructeur qui initialise les deux zones de texte -----------------------------*/
        public TextMergedView() {
            originalTextArea = new TextArea();
            modifiedTextArea = new TextArea();

            /*
            originalScrollPane = new ScrollPane();
            originalScrollPane.setPrefSize(600, 600);
            originalScrollPane.setFitToWidth(true);
            modifiedScrollPane = new ScrollPane();
            modifiedScrollPane.setPrefSize(600, 600);
            modifiedScrollPane.setFitToWidth(true); // Enables automatic line wrapping
             */

            scrollPane = new ScrollPane();
            scrollPane.setPrefSize(800, 1200);
            scrollPane.setFitToWidth(true); // Activer les sauts de ligne automatiques



            originalTextArea.setPromptText("Importez votre fichier d'origine...");
            originalTextArea.setStyle("-fx-control-inner-background: #D3D3D3;");
            originalTextArea.setPrefHeight(1000);
            originalTextArea.setPrefWidth(600);
            originalTextArea.setEditable(false); // Désactiver l'édition de texte ORIGINAL
            originalTextArea.setWrapText(true); // Activer les sauts de ligne automatiques

            modifiedTextArea.setPromptText("Importez votre fichier modifié...");
            modifiedTextArea.setPrefHeight(1000);
            modifiedTextArea.setPrefWidth(600);
            modifiedTextArea.setWrapText(true); // Activer les sauts de ligne automatiques

            this.setLeft(originalTextArea);
            this.setRight(modifiedTextArea);

            /* -------------------------------Creation le menu -------------------------------*/
            MenuBar menuBar = new MenuBar();
            // sous-élément de menu 
            Menu Menufichier = new Menu("Fichier");
            MenuItem saveItem = new MenuItem("Sauvegarder");

            saveItem.setOnAction(e -> {
                if (originalTextArea.getText().isEmpty() || modifiedTextArea.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Impossible de sauvegarder. Les zones de texte sont vides !");

                    alert.showAndWait();
                } else {
                    try {
                        BufferedWriter writer;

                        // Enregistrer le contenu de la zone de texte d'origine dans un fichier
                        writer = new BufferedWriter(new FileWriter("originalText.txt"));
                        writer.write(originalTextArea.getText());
                        writer.close();

                        // Enregistrer le contenu modifié de la zone de texte dans un fichier
                        writer = new BufferedWriter(new FileWriter("modifiedText.txt"));
                        writer.write(modifiedTextArea.getText());
                        writer.close();

                        // Afficher une boîte de dialogue de confirmation
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("Les fichiers ont été enregistrés avec succès !");

                        alert.showAndWait();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });
                    MenuItem exportItem = new MenuItem("Exporter du fichier .txt");

                    exportItem.setOnAction(e -> {
                        FileChooser fileChooser = new FileChooser();

                        // Définir le filtre d'extension
                        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                        fileChooser.getExtensionFilters().add(extFilter);

                        // Afficher la boîte de dialogue d'enregistrement du fichier
                        File file = fileChooser.showSaveDialog(stage); // 'stage' est votre fenêtre de candidature actuelle

                        if (file != null) {
                            try {
                                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                                writer.write(originalTextArea.getText());
                                writer.close();
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    });
                    MenuItem deleteItem = new MenuItem("Tous supprimer");
                    deleteItem.setOnAction(e -> {
                        originalTextArea.setText("");
                        modifiedTextArea.setText("");
                        scrollPane.setContent(null);
                    });
                    
            // sous-elements editer
            Menu Menuedit = new Menu("Editer");
            MenuItem acceptitem = new MenuItem("Accepter");
            MenuItem refuseritem = new MenuItem("Refuser");
            managingchanges = new MenuItem("Gestion des changes");

            // Ajouter des fichiers et modifier des éléments de menu
            Menufichier.getItems().addAll(exportItem, saveItem, deleteItem);
            Menuedit.getItems().addAll(acceptitem, refuseritem , managingchanges);
            menuBar.getMenus().addAll(Menufichier,Menuedit);

            this.setTop(menuBar);

            /* boutons d'import et de comparaison*/
            //  bouton de comparaison
             compareButton = new Button("Comparer");
            // bouton d'acceptation
             acceptAllButton = new Button("Tous accepter");
            // bouton de refuse
             rejectAllButton = new Button("Tous refuser");

            // bouton d'import
             importoriginaltextButton = new Button("Importer du fichier original");
             openListFileOldButton = new Button("Ouvrir de l'ancien fichier");

             importedittextButton = new Button("Importer du fichier modifié");
             confirmEditButton = new Button("Confirmer l'édition");

            /*------ Placer des objets dans Hbox-------*/
            // zone de texte dans une Hbox
            HBox textAreasBox = new HBox(5, originalTextArea, modifiedTextArea);
            textAreasBox.setPadding(new Insets(10));
            textAreasBox.setAlignment(Pos.CENTER);
            
            // Créez une nouvelle HBox contenant deux boutons : importoriginaltextButton et openListFileOldButton
            HBox importOriginalAndOpenBox = new HBox(10, importoriginaltextButton, openListFileOldButton);
            importOriginalAndOpenBox.setPadding(new Insets(10));
            importOriginalAndOpenBox.setAlignment(Pos.CENTER); 
            
            // boutons dans Hbox
            HBox importButtonsBox = new HBox(330, importOriginalAndOpenBox, importedittextButton);
            importButtonsBox.setPadding(new Insets(10));
            importButtonsBox.setAlignment(Pos.CENTER);

            // 2 scrollpane dans Hbox
            HBox scrollPaneBox = new HBox(5, scrollPane);
            scrollPaneBox.setPadding(new Insets(10));
            scrollPaneBox.setAlignment(Pos.CENTER);

            // les boutons de Hbox
            HBox compareButtonBox = new HBox(10, acceptAllButton,compareButton, rejectAllButton);
            compareButtonBox.setPadding(new Insets(10));
            compareButtonBox.setAlignment(Pos.CENTER);

            /* Placez les deux Hbox et le bouton comparer dans la Vbox*/
            VBox vbox = new VBox(5, importButtonsBox,textAreasBox, scrollPaneBox, compareButtonBox, confirmEditButton);
            vbox.setPadding(new Insets(15));
            vbox.setAlignment(Pos.CENTER);

            this.setTop(menuBar);
            this.setCenter(vbox);

        }


 /*       public Button createAcceptButton( Diff diff) {
            Button acceptButton = new Button("Accepter");
            acceptButton.setVisible(diff.operation != diff_match_patch.Operation.EQUAL);
            acceptButton.setOnAction(event -> controller.handleAccept(diff));
            this.controller = controller;

            return acceptButton;
        }
*/


        /*-------------------------- getters and seters --------------------------------*/
        public TextArea getOriginalTextArea() {
            return originalTextArea;
        }

        public void setOriginalTextArea(TextArea originalTextArea) {
            this.originalTextArea = originalTextArea;
        }

        public TextArea getModifiedTextArea() {
            return modifiedTextArea;
        }

        public void setModifiedTextArea(TextArea modifiedTextArea) {
            this.modifiedTextArea = modifiedTextArea;
        }

        public ScrollPane getOriginalScrollPane() {
            return originalScrollPane;
        }

        public void setOriginalScrollPane(ScrollPane originalScrollPane) {
            this.originalScrollPane = originalScrollPane;
        }

        public ScrollPane getModifiedScrollPane() {
            return modifiedScrollPane;
        }

        public void setModifiedScrollPane(ScrollPane modifiedScrollPane) {
            this.modifiedScrollPane = modifiedScrollPane;
        }

        public ScrollPane getScrollPane() {
            return scrollPane;
        }

        public void setScrollPane(ScrollPane scrollPane) {
            this.scrollPane = scrollPane;
        }




        // Méthode pour récupérer le contenu de la zone de texte d'origine
    public void setOriginalFileImportAction(FileImportAction action) {
        importoriginaltextButton.setOnAction(e -> action.importFile(originalTextArea));
    }

    // Méthode modifiée pour récupérer le contenu d'une zone de texte
    public void setModifiedFileImportAction(FileImportAction action) {
        importedittextButton.setOnAction(e -> action.importFile(modifiedTextArea));
    }


    public MenuItem getManagingchanges() {
    return managingchanges;
}

    // Méthode pour obtenir le nœud de comparaison
    public Button getCompareButton() {
            return compareButton;
    }

    public Button getAcceptAllButton() {
        return acceptAllButton;
    }

    public Button getRejectAllButton() {
        return rejectAllButton;
    }

    public void setAcceptAllButton(Button acceptAllButton) {
        this.acceptAllButton = acceptAllButton;
    }

    public void setRejectAllButton(Button rejectAllButton) {
        this.rejectAllButton = rejectAllButton;
    }
    
    public Button getConfirmEditButton( ) {
    	return confirmEditButton;
    }
    
    public Button getOpenListFileOldButton () {
    	return openListFileOldButton;
    }


}