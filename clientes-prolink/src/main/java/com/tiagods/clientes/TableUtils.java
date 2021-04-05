package com.tiagods.clientes;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.*;

import java.util.Arrays;
import java.util.Optional;

public class TableUtils {

    public enum StatusColor {
        REGULARIZACAO("REGULARIZAÇÃO", "-fx-text-fill : black; -fx-background-color: #A4C539"),
        EXTINTA("EXTINTA", "-fx-text-fill : black; -fx-background-color: #A4C539"),
        SUSPENSA("SUSPENSA", "-fx-text-fill : black; -fx-background-color: #62C9C9"),
        DESLIGADA("DESLIGADA", "-fx-text-fill : black; -fx-background-color: #C0C0C0"),
        JURÍDICO("JURÍDICO", "-fx-text-fill : black; -fx-background-color: #C3A4FA"),
        JURÍDICO_INAPTA("JURÍDICO/INAPTA", "-fx-text-fill : black; -fx-background-color: #C3A4FA"),
        PROPONENTE("PROPONENTE", "-fx-text-fill : black; -fx-background-color: #F2A2CC"),
        ADVOCACIA_OUTROS("ADVOCACIA/OUTROS", "-fx-text-fill : black; -fx-background-color: #F49B3E"),
        CONJUR("CONJUR", "-fx-text-fill : black; -fx-background-color: #F2A2CC"),

        EM_ANDAMENTO("EM ANDAMENTO", "-fx-text-fill : black; -fx-background-color: #FFFAA5"),
        DIAMANTE("DIAMANTE", "-fx-text-fill : black; -fx-background-color: #8F3F66"),
        PLATINA("PLATINA", "-fx-text-fill : black; -fx-background-color: #E3E3E3"),
        OURO("OURO", "-fx-text-fill : black; -fx-background-color: #FFF852"),
        PRATA("PRATA", "-fx-text-fill : black; -fx-background-color: #D5FDFE"),
        BRONZE("BRONZE", "-fx-text-fill : black; -fx-background-color: #FFFAA5"),
        PESSOA_FISICA("PESSOA FÍSICA", "-fx-text-fill : black; -fx-background-color: #96A1F8"),
        ASSOCIACAO("ASSOCIAÇÃO", "-fx-text-fill : black; -fx-background-color: #62C9C9");

        private String descricao;
        private String color;

        StatusColor(String descricao, String color) {
            this.descricao = descricao;
            this.color = color;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getColor() {
            return color;
        }
    }

    public static String getColor(String statusName){
        Optional<StatusColor> result = Arrays.asList(StatusColor.values())
                .stream()
                .filter(c-> c.getDescricao().equalsIgnoreCase(statusName))
                .findFirst();
        if(result.isPresent()) {
            return result.get().getColor();
        } else {
            return "";
        }
    }
    /**
     * Install the keyboard handler:
     *   + CTRL + C = copy to clipboard
     * @param table
     */
    public static void installCopyPasteHandler(TableView<?> table) {
        table.setOnKeyPressed(new TableKeyEventHandler());
        table.setOnMouseClicked(event -> {
            copySelectionToClipboard((TableView<?>) event.getSource());
            event.consume();
        });
    }

    /**
     * Copy/Paste keyboard event handler.
     * The handler uses the keyEvent's source for the clipboard data. The source must be of type TableView.
     */
    public static class TableKeyEventHandler implements EventHandler<KeyEvent> {
        KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);

        public void handle(final KeyEvent keyEvent) {
            if (copyKeyCodeCompination.match(keyEvent)) {
                if( keyEvent.getSource() instanceof TableView) {
                    copySelectionToClipboard( (TableView<?>) keyEvent.getSource());
                    keyEvent.consume();
                }
            }
        }
    }

    /**
     * Get table selection and copy it to the clipboard.
     * @param table
     */
    public static void copySelectionToClipboard(TableView<?> table) {

        StringBuilder clipboardString = new StringBuilder();
        ObservableList<TablePosition> positionList = table.getSelectionModel().getSelectedCells();
        int prevRow = -1;

        for (TablePosition position : positionList) {

            int row = position.getRow();
            int col = position.getColumn();
            Object cell = (Object) table.getColumns().get(col).getCellData(row);

            // null-check: provide empty string for nulls
            if (cell == null) {
                cell = "";
            }
            if (prevRow == row) {
                clipboardString.append('\t');
            } else if (prevRow != -1) {
                clipboardString.append('\n');
            }
            // create string from cell
            String text = cell.toString();
            // add new item to clipboard
            clipboardString.append(text);
            prevRow = row;
        }
        // create clipboard content
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(clipboardString.toString());
        // set clipboard content
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
}
