package view;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import java.util.LinkedList;
import java.util.List;
// import arraylist
import java.util.ArrayList;
// import des couleurs
import javafx.scene.paint.Color;

public class DiffDialog extends Dialog<List<Diff>> {
    private List<CheckBox> checkBoxes = new ArrayList<>();

    public DiffDialog(LinkedList<Diff> diffs) {
        setTitle("Differences");
        setHeaderText("SELECT THE DIFFERENCES YOU WANT TO ACCEPT OR REJECT:"); // Titre de la fenêtre



        initModality(Modality.NONE);

        VBox vbox = new VBox();
        for (Diff diff : diffs) {
            String[] lines = diff.text.split("\n"); // Divise le texte en plusieurs lignes
            for (String line : lines) {
                HBox hbox = new HBox();
                Label diffLabel = new Label(line);

                // Ajoutez une croix ou un crochet devant le texte pour indiquer si c'est une suppression ou un ajout
                String prefix = "";
                if (diff.operation == diff_match_patch.Operation.DELETE) {
                    prefix = "✖ "; // Croix pour les suppressions
                    CheckBox checkBox = new CheckBox();
                    checkBoxes.add(checkBox);
                    hbox.getChildren().add(checkBox);
                } else if (diff.operation == diff_match_patch.Operation.INSERT) {
                    prefix = "✔ "; // Crochet pour les ajouts
                    CheckBox checkBox = new CheckBox();
                    checkBoxes.add(checkBox);
                    hbox.getChildren().add(checkBox);
                }
                diffLabel.setText(prefix + line);

                hbox.getChildren().add(diffLabel);
                vbox.getChildren().add(hbox);
            }
        }

        ButtonType acceptAllButtonType = new ButtonType("Accept All", ButtonBar.ButtonData.OTHER);
        ButtonType rejectAllButtonType = new ButtonType("Reject All", ButtonBar.ButtonData.OTHER);
        ButtonType acceptButtonType = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
        ButtonType rejectButtonType = new ButtonType("Reject", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType annulerButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.OTHER);
        getDialogPane().getButtonTypes().addAll(acceptButtonType, rejectButtonType , acceptAllButtonType, rejectAllButtonType, annulerButtonType);

        getDialogPane().setContent(vbox);

        setResultConverter(dialogButton -> {
            if (dialogButton == acceptButtonType) {
                List<Diff> acceptedDiffs = new ArrayList<>();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        acceptedDiffs.add(diffs.get(i));
                    }
                }
                return acceptedDiffs;
            }
            return null;
        });
    }
}