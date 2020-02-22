/*
 * Copyright © 2020 MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) and Mateusz Niedbał
 * As of January 2020, all rights in any software published by MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) & Mateusz Niedbał will remain with the author. Contact the author with any permission requests.
 */

package personnelInfo.frameFX;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import personnelInfo.mechanics.Company;
import personnelInfo.mechanics.Encrypting;
import personnelInfo.mechanics.Person;
import personnelInfo.mechanics.StringToInt;
import personnelInfo.mechanics.enums.SortPersonType;
import personnelInfo.mechanics.enums.SortType;
import personnelInfo.mechanics.enums.WorkersType;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PersonnelController {

    private final static String CHANGE_LOG_SEPARATOR = " <<< message";
    private final static int DEFAULT_ENCRYPT_LEVEL = 3;
    private final static String WARNING_CompanyNULLMessage = "Company wasn't made yet!!";
    private final static String WARNING_CompanyNULLInformation = "It shouldn't have happen. Try to make a new company.";
    private final static String WARNING_NoPersonSelectedMessage = "No worker selected!!";
    private final static String WARNING_NoPersonSelectedInformation = "Select a worker";
    private final static String INFORMATION_Message = "Made by Mateusz Niedbał";
    private final static String INFORMATION_Information = "All rights reserved :-)";
    private final static String WARNING_WrongNumberMESSAGE = "Wrong number format!";
    private final static String WARNING_WrongNumberINFO = "Try it with numbers...";
    private final static String ERROR_IOExceptionMESSAGE = " file has failed. Check it out!";

    //FXML fields
    @FXML
    public TextField additionalSearchTextField;
    @FXML
    public VBox vBoxWithWorkers;
    @FXML
    public ChoiceBox<String> workerStatusChoiceBox;
    @FXML
    public Font x2;
    @FXML
    public Font x1;
    @FXML
    public TextField encryptMoveField;
    @FXML
    public Label labelBottomInformation;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField surnameTextField;
    @FXML
    private TextField ageTextField;
    @FXML
    private TextField positionTextField;
    @FXML
    private TextField workersIdTextField;
    @FXML
    private TextField companyNameTextField;
    @FXML
    private TextField numberOfWorkersTextField;
    @FXML
    private ChoiceBox<String> sortByChoiceBox;
    @FXML
    private ChoiceBox<String> workersTypeShowChoiceBox;

    //variables
    private DateTime startTime;
    private String changeLog;
    private List<WorkerField> buttonsWithWorkers;
    private Company company;
    private Person actualPerson;
    private Button actualWorkerButton;
    private StringToInt convert;

    //Constructor
    public PersonnelController() {

    }

    //FXML methods section
    @FXML
    private void initialize() {
        convert = new StringToInt();
        changeLog = "";
        initializeChoiceBoxes();
        makeNewCompany_ButtonAction();
        message("Welcome");
    }

    @FXML
    private void confirm_ButtonAction() {
        if (company != null && actualPerson != null) {
            actualPerson.setAGE(convert.i(ageTextField.getText()));
            actualPerson.setNAME(nameTextField.getText());
            actualPerson.setSURNAME(surnameTextField.getText());
            actualPerson.setPosition(positionTextField.getText());
            actualPerson.setWorkerType(workerType(workerStatusChoiceBox.getValue().trim()));
            actualWorkerButton.setText(actualPerson.print());
            message("Personal data changed");
        } else {
            alertMessageDialog(WARNING_NoPersonSelectedMessage, WARNING_NoPersonSelectedInformation + ", type his data again and accept.");
            message("Personal data NOT changed");
        }
    }

    @FXML
    private void makeNewCompany_ButtonAction() {
        changeLog = "";
        vBoxWithWorkers.getChildren().clear();
        clearAllTextFields();
        company = new Company(companyNameTextField.getText(), getNumberOfWorkers());
        buttonsWithWorkers = new LinkedList<>();
        for (int i = 0; i < company.getListOfWorkers().size(); i++) {
            buttonsWithWorkers.add(new WorkerField(company.getListOfWorkers().get(i)));
            personButtonFactoring(buttonsWithWorkers.get(i).getButton(), i);
            vBoxWithWorkers.getChildren().add(buttonsWithWorkers.get(i).getButton());
        }
        startTime = new DateTime();
        startMessage();
    }

    @FXML
    private void addWorker_ButtonAction() {
        if (company == null) makeNewCompany_ButtonAction();
        company.addWorker();
        numberOfWorkersTextField.setText(String.valueOf(company.getListOfWorkers().size()));
        refreshWorkerButtons();
        message("New empty worker field added");
    }

    @FXML
    private void removeWorker_ButtonAction() {
        if (company == null) {
            makeNewCompany_ButtonAction();
            alertMessageDialog(WARNING_CompanyNULLMessage, WARNING_CompanyNULLInformation);
            message("Unsuccessful worker removal. No company was selected.");
        } else if (actualPerson == null || company.getListOfWorkers().size() <= 0) {
            alertMessageDialog(WARNING_NoPersonSelectedMessage, WARNING_NoPersonSelectedInformation + " and try to remove him again.");
            message("Unsuccessful worker removal. No worker was selected.");
        } else {
            company.removeWorker(actualPerson.getID());
            message("Successful worker +"+actualPerson.getNAME()+" "+actualPerson.getSURNAME()+" removal.");
            refreshWorkerButtons();

        }
    }

    @FXML
    private void searchWorkers_ButtonAction() {
        try {
            sort(sortByChoiceBox.getValue());
            message("Successful sort by "+sortByChoiceBox.getValue());
            refreshWorkerButtons();
        } catch (Exception ex) {
            System.out.println("error");
            message("Unsuccessful sort. Error message.");
        }
    }

    @FXML
    private void newCompany_MenuItemAction() {
        numberOfWorkersTextField.setText("0");
        clearAllTextFields();
        makeNewCompany_ButtonAction();
        message("Completely new company has been set.");
    }

    @FXML
    private void close_MenuItemAction() {
        Platform.exit();
    }

    @FXML
    private void load_MenuItemAction() {
        loadAction1_LoadFile();
    }

    @FXML
    private void save_MenuItemAction() {
        if (company != null) {
            company.sort(SortPersonType.ID, 1);
            saveAction1_PrepareData_ForSave();
            message("Company saved.");
        } else {
            alertMessageDialog(WARNING_CompanyNULLMessage, WARNING_CompanyNULLInformation);
            message("Company save ERROR!!");
        }
    }

    @FXML
    private void information_MenuItemAction() {
        informationDialog();
    }

    @FXML
    public void encryptOK_ButtonAction() {
        encryptMoveField.setText(String.valueOf(getEncryptMove_Number()));
        message("Encrypt level set.");
    }

    @FXML
    public void renameCompany() {

        if (company != null) {
            company.setName(companyNameTextField.getText());
            message("Company name set.");
        } else {
            alertMessageDialog(WARNING_CompanyNULLMessage, WARNING_CompanyNULLInformation);
            message("Company name NOT set. NO company selected.");
        }
    }

    public void showChangeLogAction() {
        changeLogLabel();
    }

    // rest of methods
    private void initializeChoiceBoxes() {
        sortByChoiceBox.setValue(SortType.SORT_BY_ID.toString());
        sortByChoiceBox.setItems(getSortByChoiceBoxList());
        workersTypeShowChoiceBox.setValue(WorkersType.ACTUAL_AND_REMOVED.toString());
        workersTypeShowChoiceBox.setItems(getWorkersType_Sort_ChoiceBoxList());
        workerStatusChoiceBox.setValue(WorkersType.ACTUAL.toString());
        workerStatusChoiceBox.setItems(getWorkersTypeList());
    }

    private void refreshWorkerButtons() {
        if (company != null) {
            clearVBoxWithWorkersList();
            int actualButton = 0;
            for (int workerCounter = 0; workerCounter < company.getListOfWorkers().size(); workerCounter++) {
                if (sortStatement_ForAllWorkers(workerCounter)) {
                    setNewButtonWithData(actualButton, workerCounter);
                    actualButton++;
                }
            }
            companyNameTextField.setText(company.getName());
        } else {
            alertMessageDialog(WARNING_CompanyNULLMessage, WARNING_CompanyNULLInformation);
        }

    }

    private void setNewButtonWithData(int actualButton, int idCounter) {
        buttonsWithWorkers.add(new WorkerField(company.getListOfWorkers().get(idCounter)));
        buttonsWithWorkers.get(actualButton).getButton().setPrefSize(430, 30);
        buttonsWithWorkers.get(actualButton).getButton().setOnAction(eventHandler -> {
            setPersonDataTextFields(actualButton);
            actualPerson = buttonsWithWorkers.get(actualButton).getPerson();
            actualWorkerButton = buttonsWithWorkers.get(actualButton).getButton();
        });
        buttonsWithWorkers.get(actualButton).getButton().setText(company.getListOfWorkers().get(idCounter).print());
        vBoxWithWorkers.getChildren().add(buttonsWithWorkers.get(actualButton).getButton());
    }

    private boolean sortStatement_ForAllWorkers(int idCounter) {
        return (returnWorkersType(workersTypeShowChoiceBox.getValue()) == company.getListOfWorkers().get(idCounter).getWorkerType()
                || returnWorkersType(workersTypeShowChoiceBox.getValue()) == WorkersType.ACTUAL_AND_REMOVED);
    }

    private void clearVBoxWithWorkersList() {
        vBoxWithWorkers.getChildren().clear();
        buttonsWithWorkers = new LinkedList<>();
    }

    private void setPersonDataTextFields(int idCounter) {
        nameTextField.setText(company.getListOfWorkers().get(idCounter).getNAME());
        surnameTextField.setText(company.getListOfWorkers().get(idCounter).getSURNAME());
        ageTextField.setText(String.valueOf(company.getListOfWorkers().get(idCounter).getAGE()));
        positionTextField.setText(company.getListOfWorkers().get(idCounter).getPosition());
        workersIdTextField.setText(String.valueOf(company.getListOfWorkers().get(idCounter).getID()));
        workerStatusChoiceBox.setValue(company.getListOfWorkers().get(idCounter).getWorkerType().toString());
    }

    private void personButtonFactoring(Button button,
                                       int idCounter) {
        button.setPrefSize(430, 30);
        button.setOnAction(eventHandler -> {
            setPersonDataTextFields(idCounter);
            actualPerson = buttonsWithWorkers.get(idCounter).getPerson();
            actualWorkerButton = buttonsWithWorkers.get(idCounter).getButton();
        });
        buttonsWithWorkers.get(idCounter).getButton().setText(company.getListOfWorkers().get(idCounter).print());
        button.setWrapText(true);
    }

    // load file action

    private void loadAction1_LoadFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PCSI files (*.pcsi)", "*.pcsi");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        List<String[]> listOfElements_LoadedFromFile = new LinkedList<>();
        loadAction2_LoadFile(file, listOfElements_LoadedFromFile);
        refreshWorkerButtons();
        loadAction5_LoadChangeLog(listOfElements_LoadedFromFile);
        startMessage();
    }

    private void loadAction2_LoadFile(File file,
                                      List<String[]> listOfElements_LoadedFromFile) {
        if (file != null) {
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    if (scanner.hasNextLine()) listOfElements_LoadedFromFile.add(scanner.nextLine().split(";"));
                    else listOfElements_LoadedFromFile.add(scanner.next().split(";"));
                }
                changeLog = "";
                message("Company loaded.");
                loadAction3_SetCompanyMakingTextFields(listOfElements_LoadedFromFile);
            } catch (FileNotFoundException ex) {
                errorMessageDialog(ex,"Your try to write "+ERROR_IOExceptionMESSAGE);
                message("Error while loading");
            }
        }
    }

    private void loadAction3_SetCompanyMakingTextFields(List<String[]> listOfElements_LoadedFromFile) {
        encryptMoveField.setText(listOfElements_LoadedFromFile.get(0)[0]);
        numberOfWorkersTextField.setText(listOfElements_LoadedFromFile.get(2)[0].trim());
        makeNewCompany_ButtonAction();
        setCompanyName(Encrypting.decrypt(listOfElements_LoadedFromFile.get(1)[0].trim(), getEncryptMove_Number()));
        loadAction4_addWorkers_FromLoadedFile(listOfElements_LoadedFromFile);
    }

    private void loadAction4_addWorkers_FromLoadedFile(List<String[]> listOfElements_LoadedFromFile) {
        for (int i = 0; i < company.getListOfWorkers().size(); i++) {
            company.getListOfWorkers().get(i).setNAME(Encrypting.decrypt(listOfElements_LoadedFromFile.get(i + 3)[1], getEncryptMove_Number()));
            company.getListOfWorkers().get(i).setSURNAME(Encrypting.decrypt(listOfElements_LoadedFromFile.get(i + 3)[2], getEncryptMove_Number()));
            company.getListOfWorkers().get(i).setAGE(convert.i(listOfElements_LoadedFromFile.get(i + 3)[3].trim()));
            company.getListOfWorkers().get(i).setPosition(Encrypting.decrypt(listOfElements_LoadedFromFile.get(i + 3)[4], getEncryptMove_Number()));
            company.getListOfWorkers().get(i).setWorkerType(returnWorkersType(Encrypting.decrypt(listOfElements_LoadedFromFile.get(i + 3)[5], getEncryptMove_Number())));
        }
    }

    private void loadAction5_LoadChangeLog(List<String[]> listOfElements_LoadedFromFile) {
        if (listOfElements_LoadedFromFile.size() > 0)
            changeLog = Encrypting.decrypt(listOfElements_LoadedFromFile.get(listOfElements_LoadedFromFile.size() - 1)[0], convert.i(encryptMoveField.getText()));
    }

    // save to file action

    private void saveAction1_PrepareData_ForSave() {
        if (company != null) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PCSI files (*.pcsi)", "*.pcsi");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(new Stage());
            saveAction2_prepareCompanyName_IfIsEmpty();
            changeLog += new DateTime() + CHANGE_LOG_SEPARATOR;
            if (file != null) {
                saveAction3_SaveTextToFile(Encrypting.encrypt(encryptMoveField.getText() + ";\n" + company.toString() + changeLog, getEncryptMove_Number()), file);
            }
        }
    }

    private void saveAction2_prepareCompanyName_IfIsEmpty() {
        if (company.getName() == null || company.getName().trim().equals(""))
            company.setName(companyNameTextField.getPromptText());
    }

    private void saveAction3_SaveTextToFile(String content,
                                            File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            errorMessageDialog(ex,"Your try to write "+ERROR_IOExceptionMESSAGE);
        }
    }

    //Working methods

    private void setCompanyName(String newName) {
        companyNameTextField.setText(newName);
        renameCompany();
    }

    private void sort(String value) {
        company.sort(getSortPersonType_BySortText(value), 1);
    }

    private ObservableList<String> getSortByChoiceBoxList() {
        return FXCollections
                .observableArrayList(
                        SortType.SORT_BY_ID.toString(), SortType.SORT_BY_ID_REV.toString(),
                        SortType.SORT_BY_NAME.toString(), SortType.SORT_BY_NAME_REV.toString(),
                        SortType.SORT_BY_SURNAME.toString(), SortType.SORT_BY_SURNAME_REV.toString(),
                        SortType.SORT_BY_AGE.toString(), SortType.SORT_BY_AGE_REV.toString(),
                        SortType.SORT_BY_POSITION.toString(), SortType.SORT_BY_POSITION_REV.toString());
    }

    private ObservableList<String> getWorkersType_Sort_ChoiceBoxList() {
        return FXCollections
                .observableArrayList(
                        WorkersType.ACTUAL.toString(),
                        WorkersType.ACTUAL_AND_REMOVED.toString(),
                        WorkersType.REMOVED.toString());
    }

    private ObservableList<String> getWorkersTypeList() {
        return FXCollections
                .observableArrayList(
                        WorkersType.ACTUAL.toString(),
                        WorkersType.REMOVED.toString());
    }

    private int getEncryptMove_Number() {
        int encryptedMovementNumber_FromTextField;
        if (!is_CorrectNumeric(encryptMoveField.getText())) {
            encryptedMovementNumber_FromTextField = DEFAULT_ENCRYPT_LEVEL;
        } else encryptedMovementNumber_FromTextField = convert.i(encryptMoveField.getText());
        if (encryptedMovementNumber_FromTextField < DEFAULT_ENCRYPT_LEVEL) {
            encryptMoveField.setText(String.valueOf(DEFAULT_ENCRYPT_LEVEL));
            encryptedMovementNumber_FromTextField = DEFAULT_ENCRYPT_LEVEL;
            alertMessageDialog("Insufficient encrypt level", "It was automatically set to Default value: " + DEFAULT_ENCRYPT_LEVEL);
        }
        return encryptedMovementNumber_FromTextField;
    }

    private int getNumberOfWorkers() {
        int numberOfWorkers_FromTextField;
        if (!is_CorrectNumeric(numberOfWorkersTextField.getText())) {
            numberOfWorkers_FromTextField = 0;
        } else numberOfWorkers_FromTextField = convert.i(numberOfWorkersTextField.getText());
        return numberOfWorkers_FromTextField;
    }


    private SortPersonType getSortPersonType_BySortText(String value) {
        value = value.replace("_REV", "");
        switch (value) {
            case "SORT_BY_ID":
                return SortPersonType.ID;
            case "SORT_BY_NAME":
                return SortPersonType.NAME;
            case "SORT_BY_SURNAME":
                return SortPersonType.SURNAME;
            case "SORT_BY_AGE":
                return SortPersonType.AGE;
            case "SORT_BY_POSITION":
                return SortPersonType.POSITION;
            default:
                return null;
        }
    }

    private WorkersType returnWorkersType(String value) {
        switch (value) {
            case "ACTUAL":
                return WorkersType.ACTUAL;
            case "ACTUAL_AND_REMOVED":
                return WorkersType.ACTUAL_AND_REMOVED;
            case "REMOVED":
                return WorkersType.REMOVED;
            default:
                return null;
        }
    }

    private WorkersType workerType(String value) {
        if ("REMOVED".equals(value)) {
            return WorkersType.REMOVED;
        } else if ("ACTUAL".equals(value)) {
            return WorkersType.ACTUAL;
        } else return null;
    }

    private boolean is_CorrectNumeric(String textFieldContent) {
        if (textFieldContent == null) {
            return false;
        }
        try {
            convert.i(textFieldContent);
        } catch (NumberFormatException nfe) {
            alertMessageDialog(WARNING_WrongNumberMESSAGE, WARNING_WrongNumberINFO);
            return false;
        }
        return true;
    }

    private void clearAllTextFields() {
        ageTextField.clear();
        nameTextField.clear();
        surnameTextField.clear();
        workersIdTextField.clear();
        positionTextField.clear();
        companyNameTextField.clear();
    }


    private void startMessage() {
        message("New company " + companyNameTextField.getText() + "has been set with " + getNumberOfWorkers() + " worker fields. Session start: " + startTime);
    }

    private void message(String message) {
        LocalTime logTime = new LocalTime();
        LocalDate logDate = new LocalDate();
        message += " " + logTime + " " + logDate + CHANGE_LOG_SEPARATOR;
        labelBottomInformation.setText(message);
        changeLog += message;
    }

    // Message dialogs

    private void alertMessageDialog(String message,
                                    String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("WARNING");
        alert.setHeaderText(message);
        alert.setContentText(content);

        alert.showAndWait();
    }

    private void errorMessageDialog(Throwable ex, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(message);
        alert.setContentText(String.valueOf(ex.getCause()));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        alert.getDialogPane().setExpandableContent(expContent(exceptionText));
        alert.showAndWait();
    }

    private GridPane expContent(String exceptionText) {
        GridPane.setVgrow(textArea_StackTraceInfo(exceptionText), Priority.ALWAYS);
        GridPane.setHgrow(textArea_StackTraceInfo(exceptionText), Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        Label label = new Label("The exception stacktrace was:");
        expContent.add(label, 0, 0);
        expContent.add(textArea_StackTraceInfo(exceptionText), 0, 1);
        return expContent;
    }

    private TextArea textArea_StackTraceInfo(String exceptionText) {
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        return textArea;
    }

    private void informationDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(PersonnelController.INFORMATION_Message);
        alert.setContentText(PersonnelController.INFORMATION_Information);
        alert.showAndWait();
    }

    private void changeLogLabel() {
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

}
