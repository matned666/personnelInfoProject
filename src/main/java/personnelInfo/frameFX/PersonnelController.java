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
import personnelInfo.mechanics.*;
import personnelInfo.mechanics.enums.SortPersonType;
import personnelInfo.mechanics.enums.SortType;
import personnelInfo.mechanics.enums.WorkersType;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PersonnelController {

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
    private List<WorkerField> buttonsWithWorkers;
    private Company company;
    private Person actualPerson;
    private Button actualWorkerButton;

    //Constructor
    public PersonnelController() {
    }

    //FXML methods section
    @FXML
    private void initialize() {
        initializeChoiceBoxes();
        makeNewCompany_ButtonAction();
    }

    @FXML
    private void confirm_ButtonAction() {
        if (company != null && actualPerson != null) {
            actualPerson.setAGE(Integer.parseInt(ageTextField.getText()));
            actualPerson.setNAME(nameTextField.getText());
            actualPerson.setSURNAME(surnameTextField.getText());
            actualPerson.setPosition(positionTextField.getText());
            actualPerson.setWorkerType(workerType(workerStatusChoiceBox.getValue().trim()));
            actualWorkerButton.setText(actualPerson.print());
        } else {
            alertMessageDialog(WARNING_NoPersonSelectedMessage, WARNING_NoPersonSelectedInformation + ", type his data again and accept.");
        }
    }

    @FXML
    private void makeNewCompany_ButtonAction() {
        vBoxWithWorkers.getChildren().clear();
        company = new Company(companyNameTextField.getText(), getNumberOfWorkers());
        buttonsWithWorkers = new LinkedList<>();
        for (int i = 0; i < company.getListOfWorkers().size(); i++) {
            buttonsWithWorkers.add(new WorkerField(company.getListOfWorkers().get(i)));
            personButtonFactoring(buttonsWithWorkers.get(i).getButton(), i);
            vBoxWithWorkers.getChildren().add(buttonsWithWorkers.get(i).getButton());
        }
    }

    @FXML
    private void addWorker_ButtonAction() {
        if (company == null) makeNewCompany_ButtonAction();
        company.addWorker();
        numberOfWorkersTextField.setText(String.valueOf(company.getListOfWorkers().size()));
        refreshWorkerButtons();
    }

    @FXML
    private void removeWorker_ButtonAction() {
        if (company == null) {
            makeNewCompany_ButtonAction();
            alertMessageDialog(WARNING_CompanyNULLMessage, WARNING_CompanyNULLInformation);
        } else if (actualPerson == null || company.getListOfWorkers().size() <= 0) {
            alertMessageDialog(WARNING_NoPersonSelectedMessage, WARNING_NoPersonSelectedInformation + " and try to remove him again.");
        } else {
            company.removeWorker(actualPerson.getID());
            refreshWorkerButtons();
        }
    }

    @FXML
    private void searchWorkers_ButtonAction() {
        try {
            sort(sortByChoiceBox.getValue());
            refreshWorkerButtons();
        } catch (Exception ex) {
            System.out.println("error");
        }
    }

    @FXML
    private void newCompany_MenuItemAction() {
        numberOfWorkersTextField.setText("0");
        makeNewCompany_ButtonAction();
    }

    @FXML
    private void close_MenuItemAction() {
        Platform.exit();
    }

    @FXML
    private void load_MenuItemAction() throws FileNotFoundException {
        loadFile();
    }

    @FXML
    private void save_MenuItemAction() {
        if (company != null) {
            company.sort(SortPersonType.ID, 1);
            getSortedDataToSaveToFile();
        } else {
            alertMessageDialog(WARNING_CompanyNULLMessage, WARNING_CompanyNULLInformation);
        }
    }

    @FXML
    private void information_MenuItemAction() {
        informationDialog();
    }

    // rest of methods
    private void initializeChoiceBoxes() {
        sortByChoiceBox.setValue(SortType.SORT_BY_ID.toString());
        sortByChoiceBox.setItems(getSortByChoiceBoxList());
        workersTypeShowChoiceBox.setValue(WorkersType.ACTUAL_WORKER.toString());
        workersTypeShowChoiceBox.setItems(getWorkersType_Sort_ChoiceBoxList());
        workerStatusChoiceBox.setValue(WorkersType.ACTUAL_WORKER.toString());
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
                || returnWorkersType(workersTypeShowChoiceBox.getValue()) == WorkersType.ACTUAL_AND_REMOVED)
                && (company.getListOfWorkers().get(idCounter).print().toLowerCase().contains(additionalSearchTextField.getText()));
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

    private void sort(String value) {
        company.sort(getSortPersonType_BySortText(value), 1);
    }

    public void renameCompany() {

        if (company != null) {
            company.setName(companyNameTextField.getText());
        } else {
            alertMessageDialog(WARNING_CompanyNULLMessage, WARNING_CompanyNULLInformation);
        }
    }

    private void loadFile() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PCSI files (*.pcsi)", "*.pcsi");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        List<String[]> temp = new LinkedList<>();
        loadFile(file, temp);
        refreshWorkerButtons();
    }

    private void loadFile(File file,
                          List<String[]> temp)
             {
        if (file != null) {
            try {
                Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                if (scanner.hasNextLine()) temp.add(scanner.nextLine().split(";"));
                else temp.add(scanner.next().split(";"));
            }
            addCompanyInfoFromLoadedFile(temp);
            } catch (FileNotFoundException ex) {
                errorMessageDialog(ex,"Your try to write "+ERROR_IOExceptionMESSAGE);
            }
        }
    }

    private void addCompanyInfoFromLoadedFile(List<String[]> temp) {
        companyNameTextField.setText(Encrypting.decrypt(temp.get(0)[0].trim(), getEncryptMove_Number()));
        numberOfWorkersTextField.setText(temp.get(1)[0].trim());
        makeNewCompany_ButtonAction();
        addCompanyDataFromLoadedFile(temp);
    }

    private void addCompanyDataFromLoadedFile(List<String[]> temp) {
        for (int i = 2; i < temp.size() - 1; i++) {
            company.getListOfWorkers().get(i - 2).setNAME(Encrypting.decrypt(temp.get(i)[1], getEncryptMove_Number()));
            company.getListOfWorkers().get(i - 2).setSURNAME(Encrypting.decrypt(temp.get(i)[2], getEncryptMove_Number()));
            company.getListOfWorkers().get(i - 2).setAGE(Integer.parseInt(temp.get(i)[3].trim()));
            company.getListOfWorkers().get(i - 2).setPosition(Encrypting.decrypt(temp.get(i)[4], getEncryptMove_Number()));
            company.getListOfWorkers().get(i - 2).setWorkerType(returnWorkersType(Encrypting.decrypt(temp.get(i)[5], getEncryptMove_Number())));
        }
    }

    private void getSortedDataToSaveToFile() {
        if (company != null) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PCSI files (*.pcsi)", "*.pcsi");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(new Stage());
            prepareCompanyNameForSave_IfIsEmpty();
            if (file != null) {
                saveTextToFile(Encrypting.encrypt(company.toString(), getEncryptMove_Number()), file);
            }
        }
    }

    private void prepareCompanyNameForSave_IfIsEmpty() {
        if (company.getName() == null || company.getName().trim().equals(""))
            company.setName(companyNameTextField.getPromptText());
    }

    private void saveTextToFile(String content,
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
                        WorkersType.ACTUAL_WORKER.toString(),
                        WorkersType.ACTUAL_AND_REMOVED.toString(),
                        WorkersType.REMOVED_WORKER.toString());
    }

    private ObservableList<String> getWorkersTypeList() {
        return FXCollections
                .observableArrayList(
                        WorkersType.ACTUAL_WORKER.toString(),
                        WorkersType.REMOVED_WORKER.toString());
    }

    private int getEncryptMove_Number() {
        int encryptedMovementNumber_FromTextField;
        if (!is_CorrectNumeric(encryptMoveField.getText())) {
            encryptedMovementNumber_FromTextField = 0;
        } else encryptedMovementNumber_FromTextField = Integer.parseInt(encryptMoveField.getText());
        return encryptedMovementNumber_FromTextField;
    }

    private int getNumberOfWorkers() {
        int numberOfWorkers_FromTextField;
        if (!is_CorrectNumeric(numberOfWorkersTextField.getText())) {
            numberOfWorkers_FromTextField = 0;
        } else numberOfWorkers_FromTextField = Integer.parseInt(numberOfWorkersTextField.getText());
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
            case "ACTUAL_WORKER":
                return WorkersType.ACTUAL_WORKER;
            case "ACTUAL_AND_REMOVED":
                return WorkersType.ACTUAL_AND_REMOVED;
            case "REMOVED_WORKER":
                return WorkersType.REMOVED_WORKER;
            default:
                return null;
        }
    }

    private WorkersType workerType(String value) {
        if ("REMOVED_WORKER".equals(value)) {
            return WorkersType.REMOVED_WORKER;
        } else if ("ACTUAL_WORKER".equals(value)) {
            return WorkersType.ACTUAL_WORKER;
        } else return null;
    }

    private boolean is_CorrectNumeric(String textFieldContent) {
        if (textFieldContent == null) {
            return false;
        }
        try {
            Integer.parseInt(textFieldContent);
        } catch (NumberFormatException nfe) {
            alertMessageDialog(WARNING_WrongNumberMESSAGE, WARNING_WrongNumberINFO);
            return false;
        }
        return true;
    }

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

}
