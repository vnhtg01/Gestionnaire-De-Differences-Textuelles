package control;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import name.fraser.neil.plaintext.DiffMatchPatchPublic;
import view.DiffDialog;
import view.TextMergedView;
import java.io.BufferedReader;

import javafx.scene.control.TextArea;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.regex.Pattern;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileReader;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert;
import model.TextModel;
import java.nio.file.StandardOpenOption;
/* j'importe la bibliotheque diff_match_patch depuis le package name.fraser.neil.plaintext  */
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import java.util.HashMap;





public class TextMergedController {
    private TextMergedView view;
    private  TextModel model;
    private HashMap<Diff, Integer> offsets = new HashMap<>();

    private File selectedOriginalFile;
    
    private String commentOfSelectedFile; 
    
    /* listes des differences */
    private LinkedList<Diff> diffs;

    /*----------- creation d'un constructeur qui prend en parametre la vue--------*/
    public TextMergedController(TextMergedView view) {
        this.view = view;
        this.model = new TextModel();
        view.setOriginalFileImportAction(this::importFileAndSave);
        view.setModifiedFileImportAction(this::importFile);
        
        // Définir un événement pour le bouton "Confirmer la modification"
        view.getConfirmEditButton().setOnAction(EventHandler -> showCommentInputDialog());
        view.getOpenListFileOldButton().setOnAction(EventHandler -> handleOpenListfileOld());
        
        // Définir l'événement pour afficher la boîte de dialogue de commentaire 
        // lors d'un clic gauche sur originalTextArea
        view.getOriginalTextArea().setOnMouseClicked(event -> {
            if (event.getButton().toString().equals("PRIMARY")) {
                showCommentDialog();
            }
        });
        
        // Ajout des écouteurs de changement de texte aux zones de texte
        view.getOriginalTextArea().textProperty().addListener((observable, oldValue, newValue) -> model.setOriginalText(newValue));
        view.getModifiedTextArea().textProperty().addListener((observable, oldValue, newValue) -> model.setModifiedText(newValue));


        /* connexion du bouton comparer à la méthode CompareText */
        view.getCompareButton().setOnAction(event -> compareText());

        /* connexion du bouton Accepter toutes les modifications en utilisant la méthode AcceptAllChanges */
        view.getAcceptAllButton().setOnAction(event -> acceptAllChanges());

        /* connexion le bouton rejeter toutes les modifications en utilisant la méthode de rejetAllChanges */
        view.getRejectAllButton().setOnAction(event -> rejectAllChanges());
        

        // Désactiver le menu "Gérer les modifications" si la liste des différences est vide
        view.getManagingchanges().setDisable(diffs == null || diffs.isEmpty());

        // connexion du bouton de gestion des changements à la méthode shoDiffs
        view.getManagingchanges().setOnAction(event -> showDiffs());

    }

    
    private void showCommentDialog() {
        // Affiche une boîte de dialogue avec les commentaires de l'ancien fichier
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Commentaire");
        alert.setHeaderText("Commentaire pour la modification");
        alert.setContentText(commentOfSelectedFile);
        alert.showAndWait();
	    }																											
    
    
     /* ---------------- creation d'une methode pour importer les fichiers */
	private void importFile(TextArea textArea) {
	    FileChooser fileChooser = new FileChooser();
	    
	    // Définir le filtre d'extension pour les fichiers .txt
	    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
	    fileChooser.getExtensionFilters().add(extFilter);
	    
	    File selectedFile = fileChooser.showOpenDialog(null);
	    if (selectedFile != null) {
	        try {
	            String content = new String(Files.readAllBytes(Paths.get(selectedFile.getPath())));
	            if (content.length() > 10000) {
	                Alert alert = new Alert(Alert.AlertType.WARNING);
	                alert.setTitle("Warning Dialog");
	                alert.setHeaderText("Texte Trop Long");
	                alert.setContentText("Le texte que vous essayez d'importer est trop long. Veuillez importer un texte de moins de 10 000 caractères.");
	                alert.showAndWait();
	            } else {
	                textArea.setText(content);
	                if (textArea == view.getOriginalTextArea()) {
	                    model.setOriginalText(content); // mise a jour du texte original(model)
	                } else {
	                    model.setModifiedText(content); // mise a jour du texte modifie(model)
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setTitle("Error Dialog");
	        alert.setHeaderText("Incorrect File Format");
	        alert.setContentText("The selected file is not a .txt file. Please select a .txt file.");
	        alert.showAndWait();
	    }
	}


    /* créer une méthode pour comparer deux textes : utilisez la bibliothèque diff_match_patch */
	public void compareText() {
	    String originalText = view.getOriginalTextArea().getText() + "\n";
	    String modifiedText = view.getModifiedTextArea().getText() + "\n";
	
	    DiffMatchPatchPublic dmp = new DiffMatchPatchPublic();
	    long deadline = System.currentTimeMillis() + 1000; // 1 seconde
	    LinkedList<Diff> lineDiffs = dmp.diffLineModePublic(originalText, modifiedText, deadline);
	
	    // Convertir les différences de ligne en différences de mot
	    diffs = new LinkedList<>();
	    for (Diff lineDiff : lineDiffs) {
	        if (lineDiff.operation != diff_match_patch.Operation.EQUAL) {
	            LinkedList<Diff> wordDiffs = dmp.diff_main(lineDiff.text, "");
	            for (Diff wordDiff : wordDiffs) {
	                wordDiff.operation = lineDiff.operation;
	                diffs.add(wordDiff);
	            }
	        } else {
	            diffs.add(lineDiff);
	        }
	    }
	
	    updateScrollPaneWithDiffs(view.getScrollPane(), diffs);
	
	    // Activer le menu "Managing Changes" si la liste des différences n'est pas vide
	    view.getManagingchanges().setDisable(diffs.isEmpty());
	}
	
	    /* creation d'ne methode pour afficher les differences */
	    public void showDiffs() {
	        // Creation d'une nouvelle instance de DiffDialog
	        DiffDialog diffDialog = new DiffDialog(diffs);
	
	        // Afichage de la boîte de dialogue et attendez que l'utilisateur la ferme
	        diffDialog.showAndWait();
	    }


    /* creation d'une methode pour mettre a jour les textes avec les differences */

	 private void updateScrollPaneWithDiffs(ScrollPane scrollPane, LinkedList<Diff> diffs) {
	    TextFlow textFlow = new TextFlow();
	
	    for (Diff diff : diffs) {
	        Text text = new Text(diff.text);
	
	        if (diff.operation == diff_match_patch.Operation.DELETE) {
	            text.setFill(Color.RED);
	        } else if (diff.operation == diff_match_patch.Operation.INSERT) {
	            text.setFill(Color.GREEN);
	        } else {
	            text.setFill(Color.BLACK);
	        }
	
	        // Si la différence n'est pas une opération EQUAL, ajoutez les boutons "Accepter" et "Refuser"
	        if (diff.operation != diff_match_patch.Operation.EQUAL) {
	            Button acceptButton = new Button("Accepter");
	            acceptButton.setOnAction(event -> handleAccept(diff));
	
	            Button rejectButton = new Button("Refuser");
	            rejectButton.setOnAction(event -> handleReject(diff));
	
	            textFlow.getChildren().addAll(text, acceptButton, rejectButton);
	        } else {
	            textFlow.getChildren().add(text);
	        }
	    }
	
	    scrollPane.setContent(textFlow);
	}


    public void acceptAllChanges() {
        System.out.println("Méthode Accept_All_Changes appelée");
        model.setOriginalText(model.getModifiedText());
        updateViewForAcceptAll();
        compareText();
    }

    public void rejectAllChanges() {
        System.out.println("Méthode Reject_All_Changes appelée");
        model.setModifiedText(model.getOriginalText());
        updateViewForRejectAll();
        compareText();
    }

    private void updateViewForAcceptAll() {
        System.out.println("Update View pour la méthode Accept_All_Changes appelée");
        view.getOriginalTextArea().setText(model.getOriginalText());
        //System.out.println("Original text area updated with: " + model.getOriginalText());
    }


    private void updateViewForRejectAll() {
        System.out.println("Update View pour la méthode Reject_All_Changes appelée");
        view.getModifiedTextArea().setText(model.getModifiedText());
        //System.out.println("Modified text area updated with: " + model.getModifiedText());
    }
    
    private void handleAccept(Diff diff) {
        // Si la différence est une suppression, supprimez le texte de la différence du texte original
        // et mettez à jour les décalages pour toutes les différences qui suivent cette suppression
        if (diff.operation == diff_match_patch.Operation.DELETE) {
            model.setOriginalText(model.getOriginalText().replaceFirst(Pattern.quote(diff.text), ""));
            int deletionLength = diff.text.length();
            int diffIndex = diffs.indexOf(diff);
            for (int i = diffIndex + 1; i < diffs.size(); i++) {
                Diff followingDiff = diffs.get(i);
                if (followingDiff.operation == diff_match_patch.Operation.INSERT) {
                    int currentOffset = offsets.getOrDefault(followingDiff, 0);
                    offsets.put(followingDiff, currentOffset + deletionLength);
                }
            }
        }
        // Si la différence est un ajout, ajoutez le texte de la différence au texte original à la position appropriée
        else if (diff.operation == diff_match_patch.Operation.INSERT) {
            int index = getInsertionIndex(diffs, diff);
            String originalText = model.getOriginalText();
            model.setOriginalText(originalText.substring(0, index) + diff.text + originalText.substring(index));
        }

        // Supprimez la différence acceptée de la liste des différences
        diffs.remove(diff);

        // Mettez à jour la vue pour refléter le changement
        updateViewForAcceptAll();
        updateScrollPaneWithDiffs(view.getScrollPane(), diffs);
    }



    private int getInsertionIndex(LinkedList<Diff> diffs, Diff insertDiff) {
        int index = 0;
        String originalText = model.getOriginalText();
        for (Diff diff : diffs) {
            if (diff == insertDiff) {
                break;
            }
            if (diff.operation != diff_match_patch.Operation.INSERT) {
                index += diff.text.length();
            }
        }
        int offset = offsets.getOrDefault(insertDiff, 0);
        return index + offset; // Ajoutez le décalage de la différence à l'index d'insertion
    }
    
    
    private void handleOpenListfileOld() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        // Spécifiez le dossier par défaut à ouvrir
        fileChooser.setInitialDirectory(new File("manage_file"));

        // Filtre de fichiers pour afficher uniquement les fichiers txt
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Ouvre la boîte de dialogue de sélection de fichier
        File selectedFile = fileChooser.showOpenDialog(null);

        // Traiter le fichier sélectionné
        if (selectedFile != null) {
            // Met à jour selectedOriginalFile avec le fichier original sélectionné
            selectedOriginalFile = selectedFile;
            
            // Effectuer les actions nécessaires lorsqu'un fichier est sélectionné
            // Ví dụ: Affiche la dernière modification du fichier sélectionné
            displayRecentModification(selectedFile);
        }
    }
    
    private void displayRecentModification(File selectedFile) {
        try {
            // Lisez le fichier link_file.txt pour trouver la version la plus récemment modifiée du fichier sélectionné.
            BufferedReader reader = new BufferedReader(new FileReader("manage_file/link_file.txt"));
            String line;
            String recentModification = null;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(selectedFile.getName())) {
                    recentModification = parts[1];
                    // Récupérez le commentaire de l'ancien fichier et enregistrez-le dans la variable commentOfSelectedFile
                    commentOfSelectedFile = parts[2];
                }
            }
            reader.close();

            if (recentModification != null) {
                // Lire le contenu de la version la plus récemment éditée
                String content = new String(Files.readAllBytes(Paths.get("manage_file/" + recentModification)));

                // Affiche le contenu de la version la plus récemment modifiée
                view.getOriginalTextArea().setText(content);
                System.out.println("Recent modification of file " + selectedFile.getName() + " displayed.");
            } else {
                // Affiche un avertissement si aucune version modifiée n'est trouvée pour le fichier sélectionné
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("No Recent Modification Found");
                alert.setContentText("No recent modification found for the selected file.");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Méthode pour afficher la boîte de dialogue de saisie de commentaire
    private void showCommentInputDialog() {
        if (selectedOriginalFile != null) { // Vérification si le fichier d'origine a été précédemment sélectionné
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Comment");
            dialog.setHeaderText("Enter your comment for the modification:");
            dialog.setContentText("Comment:");

            // Écoute des événements lorsque les utilisateurs acceptent ou annulent
            dialog.showAndWait().ifPresent(comment -> {
                if (!comment.isEmpty()) {
                    // Enregistrement le fichier modifié et mettez à jour les informations de version et les commentaires
                    saveModifiedFile(selectedOriginalFile.getName(), comment);
                } else {
                    // Afficher un avertissement si l'utilisateur ne saisit pas de commentaire
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Comment Required");
                    alert.setContentText("Please enter a comment for the modification.");
                    alert.showAndWait();
                }
            });
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("File Not Selected");
            alert.setContentText("Please import an original file first.");
            alert.showAndWait();
        }
    }
    
    // Méthode pour enregistrer les fichiers modifiés et 
    // mettre à jour les informations de version et de commentaire
    private void saveModifiedFile(String originalFileName, String comment) {
        try {
            // Récupérer le contenu de la TextArea originale (à droite)
            String content = view.getOriginalTextArea().getText();

            // Création un dossier "manage_file" s'il n'existe pas déjà
            Path destinationDirectory = Paths.get("manage_file");
            if (!Files.exists(destinationDirectory)) {
                Files.createDirectories(destinationDirectory);
            }

            // Création du chemin complet du fichier link_file.txt dans le dossier manage_file
            Path linkFilePath = destinationDirectory.resolve("link_file.txt");

            // Création le chemin complet du fichier qui enregistre les versions modifiées
            String modifiedFileName = "modified_" + comment + "_" + System.currentTimeMillis() + ".txt";
            Path destinationFile = destinationDirectory.resolve(modifiedFileName);

            // Enregistrement les informations de version et les commentaires dans le fichier link_file.txt
            String link = originalFileName + "," + modifiedFileName + "," + comment + "\n";
            Files.write(linkFilePath, link.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            // Écrire le contenu du fichier modifié dans un nouveau fichier dans le répertoire manage_file
            Files.write(destinationFile, content.getBytes());

            System.out.println("Fichier modifié enregistré : " + destinationFile.toString());
            System.out.println("Lien mis à jour dans " + linkFilePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void importFileAndSave(TextArea textArea) {
        FileChooser fileChooser = new FileChooser();
        selectedOriginalFile = fileChooser.showOpenDialog(null); // Enregistrer le fichier original sélectionné
        if (selectedOriginalFile != null) {
            try {
                String content = new String(Files.readAllBytes(selectedOriginalFile.toPath()));
                if (content.length() > 10000) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning Dialog");
                    alert.setHeaderText("Texte Trop Long");
                    alert.setContentText("Le texte que vous essayez d'importer est trop long. Veuillez importer un texte de moins de 10 000 caractères.");
                    alert.showAndWait();
                } else {
                    textArea.setText(content);
                    if (textArea == view.getOriginalTextArea()) {
                        model.setOriginalText(content); // Mettre à jour le texte original
                    } else {
                        model.setModifiedText(content); // Mettre à jour le texte modifié
                    }

                    // Copiez le fichier dans le dossier "manage_file"
                    Path destinationDirectory = Paths.get("manage_file");
                    if (!Files.exists(destinationDirectory)) {
                        Files.createDirectories(destinationDirectory);
                    }
                    Path destinationFile = destinationDirectory.resolve(selectedOriginalFile.getName());
                    Files.copy(selectedOriginalFile.toPath(), destinationFile);
                    System.out.println("Fichier copié vers : " + destinationFile.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handleReject(Diff diff) {
        // Supprimer les écarts rejetés de la liste des écarts
        diffs.remove(diff);

        // Mettre à jour la vue pour refléter le changement
        updateViewForRejectAll();
        
        clearTextFlow(view.getScrollPane());
        
        updateScrollPaneWithDiffs(view.getScrollPane(), diffs);
    }
    
    private void clearTextFlow(ScrollPane scrollPane) {
        TextFlow textFlow = (TextFlow) scrollPane.getContent();
        textFlow.getChildren().clear();
    }

}

