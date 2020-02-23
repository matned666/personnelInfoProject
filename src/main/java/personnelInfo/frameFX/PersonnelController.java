/*
 * Copyright © 2020 MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) and Mateusz Niedbał
 * As of January 2020, all rights in any software published by MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) & Mateusz Niedbał will remain with the author. Contact the author with any permission requests.
 */

package personnelInfo.frameFX;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
import personnelInfo.mechanics.converters.Converting;
import personnelInfo.mechanics.enums.SortPersonType;
import personnelInfo.mechanics.enums.WorkersType;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PersonnelController {

    private final static int DEFAULT_ENCRYPT_LEVEL = 3;
    private final static String CHANGE_LOG_SEPARATOR = " <<< message";


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
    private Converting convert;

    //Constructor
    public PersonnelController() {

    }

    //FXML methods section
    @FXML
    private void initialize() {
        convert = new Converting();
        changeLog = "";
        new BoxListCollectionsInitialize(sortByChoiceBox,workersTypeShowChoiceBox,workerStatusChoiceBox);
        makeNewCompany_ButtonAction();
        message("Welcome");
    }

    @FXML
    private void confirm_ButtonAction() {
        if (company != null && actualPerson != null) {
            setActualPersonData();
            message("Personal data changed");
        } else {
            Messages.alertMessageDialog(Messages.getWARNING_NoPersonSelectedMessage(), Messages.getWARNING_NoPersonSelectedInformation() + ", type his data again and accept.");
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
        numberOfWorkersTextField.setText(convert.string(company.getListOfWorkers().size()));
        refreshWorkerButtons();
        message("New empty worker field added");
    }

    @FXML
    private void removeWorker_ButtonAction() {
        if (company == null) {
            makeNewCompany_ButtonAction();
            Messages.alertMessageDialog(Messages.getWARNING_CompanyNULLMessage(), Messages.getWARNING_CompanyNULLInformation());
            message("Unsuccessful worker removal. No company was selected.");
        } else if (actualPerson == null || company.getListOfWorkers().size() <= 0) {
            Messages.alertMessageDialog(Messages.getWARNING_NoPersonSelectedMessage(), Messages.getWARNING_NoPersonSelectedInformation() + " and try to remove him again.");
            message("Unsuccessful worker removal. No worker was selected.");
        } else {
            company.removeWorker(actualPerson.getID());
            message("Successful worker +"+actualPerson.getNAME()+" "+actualPerson.getSURNAME()+" removal.");
            refreshWorkerButtons();

        }
    }

    @FXML
    private void searchWorkers_ButtonAction() {
        if (company != null) {
            if (company.getListOfWorkers().size() <= 0)
                message("Unsuccessful sort. Nothing to sort yet.");
            else if (company.getListOfWorkers().size() == 1)
                message("Successful sort by " + sortByChoiceBox.getValue() + " of a single element (nothing has actually changed)");
            else {
                sort(sortByChoiceBox.getValue());
                message("Successful sort by " + sortByChoiceBox.getValue());
                refreshWorkerButtons();
            }
        } else {
            Messages.alertMessageDialog("Sort impossible", "Company haven't been set.");
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
            Messages.alertMessageDialog(Messages.getWARNING_CompanyNULLMessage(), Messages.getWARNING_CompanyNULLInformation());
            message("Company save ERROR!!");
        }
    }

    @FXML
    private void information_MenuItemAction() {
        Messages.informationDialog();
    }

    @FXML
    public void encryptOK_ButtonAction() {
        encryptMoveField.setText(convert.string(getEncryptMove_Number()));
        message("Encrypt level set.");
    }

    @FXML
    public void renameCompany() {

        if (company != null) {
            company.setName(companyNameTextField.getText());
            message("Company name set.");
        } else {
            Messages.alertMessageDialog(Messages.getWARNING_CompanyNULLMessage(), Messages.getWARNING_CompanyNULLInformation());
            message("Company name NOT set. NO company selected.");
        }
    }

    @FXML
    public void showChangeLogAction() {
        Messages.changeLogLabel(changeLog,CHANGE_LOG_SEPARATOR);
    }

    // rest of methods

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
            Messages.alertMessageDialog(Messages.getWARNING_CompanyNULLMessage(), Messages.getWARNING_CompanyNULLInformation());
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

    private void setActualPersonData() {
        actualPerson.setAGE(convert.integer(ageTextField.getText()));
        actualPerson.setNAME(nameTextField.getText());
        actualPerson.setSURNAME(surnameTextField.getText());
        actualPerson.setPosition(positionTextField.getText());
        actualPerson.setWorkerType(workerType(workerStatusChoiceBox.getValue().trim()));
        actualWorkerButton.setText(actualPerson.print());
    }

    private void clearVBoxWithWorkersList() {
        vBoxWithWorkers.getChildren().clear();
        buttonsWithWorkers = new LinkedList<>();
    }

    private void setPersonDataTextFields(int idCounter) {
        nameTextField.setText(company.getListOfWorkers().get(idCounter).getNAME());
        surnameTextField.setText(company.getListOfWorkers().get(idCounter).getSURNAME());
        ageTextField.setText(convert.string(company.getListOfWorkers().get(idCounter).getAGE()));
        positionTextField.setText(company.getListOfWorkers().get(idCounter).getPosition());
        workersIdTextField.setText(convert.string(company.getListOfWorkers().get(idCounter).getID()));
        workerStatusChoiceBox.setValue(company.getListOfWorkers().get(idCounter).getWorkerType().toString());
    }

    private void clearAllTextFields() {
        ageTextField.clear();
        nameTextField.clear();
        surnameTextField.clear();
        workersIdTextField.clear();
        positionTextField.clear();
        companyNameTextField.clear();
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
                scanner.close();
            } catch (FileNotFoundException ex) {
                Messages.errorMessageDialog(ex,"Your try to write "+Messages.getERROR_IOExceptionMESSAGE(),convert);
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
            company.getListOfWorkers().get(i).setAGE(convert.integer(listOfElements_LoadedFromFile.get(i + 3)[3].trim()));
            company.getListOfWorkers().get(i).setPosition(Encrypting.decrypt(listOfElements_LoadedFromFile.get(i + 3)[4], getEncryptMove_Number()));
            company.getListOfWorkers().get(i).setWorkerType(returnWorkersType(Encrypting.decrypt(listOfElements_LoadedFromFile.get(i + 3)[5], getEncryptMove_Number())));
        }
    }

    private void loadAction5_LoadChangeLog(List<String[]> listOfElements_LoadedFromFile) {
        if (listOfElements_LoadedFromFile.size() > 0)
            changeLog = Encrypting.decrypt(listOfElements_LoadedFromFile.get(listOfElements_LoadedFromFile.size() - 1)[0], convert.integer(encryptMoveField.getText()));
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
            Messages.errorMessageDialog(ex,("Your try to write "+Messages.getERROR_IOExceptionMESSAGE()),convert);
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

    private int getEncryptMove_Number() {
        int encryptedMovementNumber_FromTextField;
        if (!is_CorrectNumeric(encryptMoveField.getText())) {
            encryptedMovementNumber_FromTextField = DEFAULT_ENCRYPT_LEVEL;
        } else encryptedMovementNumber_FromTextField = convert.integer(encryptMoveField.getText());
        if (encryptedMovementNumber_FromTextField < DEFAULT_ENCRYPT_LEVEL) {
            encryptMoveField.setText(convert.string(DEFAULT_ENCRYPT_LEVEL));
            encryptedMovementNumber_FromTextField = DEFAULT_ENCRYPT_LEVEL;
            Messages.alertMessageDialog("Insufficient encrypt level", "It was automatically set to Default value: " + DEFAULT_ENCRYPT_LEVEL);
        }
        return encryptedMovementNumber_FromTextField;
    }

    private int getNumberOfWorkers() {
        int numberOfWorkers_FromTextField;
        if (!is_CorrectNumeric(numberOfWorkersTextField.getText())) {
            numberOfWorkers_FromTextField = 0;
        } else numberOfWorkers_FromTextField = convert.integer(numberOfWorkersTextField.getText());
        return numberOfWorkers_FromTextField;
    }


    private SortPersonType getSortPersonType_BySortText(String value) {
      return  new SortingType().getSortPersonType_BySortText(value);
    }

    private WorkersType returnWorkersType(String value) {
   return new SortingType().returnWorkersType(value);
    }

    private WorkersType workerType(String value) {
     return new SortingType().workerType(value);
    }

    private boolean is_CorrectNumeric(String textFieldContent) {
        if (textFieldContent == null) {
            return false;
        }
        try {
            convert.integer(textFieldContent);
        } catch (NumberFormatException nfe) {
            Messages.alertMessageDialog(Messages.getWARNING_WrongNumberMESSAGE(), Messages.getWARNING_WrongNumberINFO());
            return false;
        }
        return true;
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


}
