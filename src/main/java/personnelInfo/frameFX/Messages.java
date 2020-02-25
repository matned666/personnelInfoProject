/*
 * Copyright © 2020 MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) and Mateusz Niedbał
 * As of January 2020, all rights in any software published by MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) & Mateusz Niedbał will remain with the author. Contact the author with any permission requests.
 */

package personnelInfo.frameFX;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import personnelInfo.mechanics.converters.Converting;

import java.io.PrintWriter;
import java.io.StringWriter;

class Messages {

    private static Converting convert = new Converting();

    private final static String WARNING_CompanyNULLMessage = "Company wasn't made yet!!";
    private final static String WARNING_CompanyNULLInformation = "It shouldn't have happen. Try to make a new company.";
    private final static String WARNING_NoPersonSelectedMessage = "No worker selected!!";
    private final static String WARNING_NoPersonSelectedInformation = "Select a worker";
    private final static String INFORMATION_Message = "Made by Mateusz Niedbał";
    private final static String INFORMATION_Information = "All rights reserved :-)";
    private final static String WARNING_WrongNumberMESSAGE = "Wrong number format!";
    private final static String WARNING_WrongNumberINFO = "Try it with numbers... Automaticly changed to '0'";
    private final static String ERROR_IOExceptionMESSAGE = " file has failed. Check it out!";

    static void alertMessageDialog(String message,
                                    String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("WARNING");
        alert.setHeaderText(message);
        alert.setContentText(content);

        alert.showAndWait();
    }

    static void errorMessageDialog(Throwable ex, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(message);
        alert.setContentText(convert.throwable(ex.getCause()));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        alert.getDialogPane().setExpandableContent(expContent(exceptionText));
        alert.showAndWait();
    }

    private static GridPane expContent(String exceptionText) {
        GridPane.setVgrow(textArea_StackTraceInfo(exceptionText), Priority.ALWAYS);
        GridPane.setHgrow(textArea_StackTraceInfo(exceptionText), Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        Label label = new Label("The exception stacktrace was:");
        expContent.add(label, 0, 0);
        expContent.add(textArea_StackTraceInfo(exceptionText), 0, 1);
        return expContent;
    }

    private static TextArea textArea_StackTraceInfo(String exceptionText) {
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        return textArea;
    }

    static void informationDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(INFORMATION_Message);
        alert.setContentText(INFORMATION_Information);
        alert.showAndWait();
    }

    static void changeLogLabel(String changeLog, String CHANGE_LOG_SEPARATOR) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Change log dialog.");
        alert.setHeaderText("Current session changes log. ");
        alert.setGraphic(null);

        TextArea textArea = new TextArea(changeLog.replace(CHANGE_LOG_SEPARATOR, "\n"));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

     static String getWARNING_CompanyNULLMessage() {
        return WARNING_CompanyNULLMessage;
    }
     static String getWARNING_CompanyNULLInformation() {
        return WARNING_CompanyNULLInformation;
    }
     static String getWARNING_NoPersonSelectedMessage() {
        return WARNING_NoPersonSelectedMessage;
    }
     static String getWARNING_NoPersonSelectedInformation() {
        return WARNING_NoPersonSelectedInformation;
    }
     static String getWARNING_WrongNumberMESSAGE() {
        return WARNING_WrongNumberMESSAGE;
    }
     static String getWARNING_WrongNumberINFO() {
        return WARNING_WrongNumberINFO;
    }
     static String getERROR_IOExceptionMESSAGE() {
        return ERROR_IOExceptionMESSAGE;
    }
}
