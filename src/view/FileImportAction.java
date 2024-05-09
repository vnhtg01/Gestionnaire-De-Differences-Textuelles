package view;

import javafx.scene.control.TextArea;

//Interface fonctionnelle pour importer un fichier
@FunctionalInterface
public interface FileImportAction {
    void importFile(TextArea textArea);
}