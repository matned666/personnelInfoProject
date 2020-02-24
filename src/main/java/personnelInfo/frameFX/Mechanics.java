package personnelInfo.frameFX;

import javafx.scene.control.TextField;
import personnelInfo.mechanics.converters.Converting;
import personnelInfo.mechanics.enums.SortPersonType;
import personnelInfo.mechanics.enums.PersonType;

class Mechanics {

    private static Converting convert = new Converting();

    static int getNumberOfWorkers(TextField numberOfWorkersTextField) {
        int numberOfWorkers_FromTextField;
        if (!is_CorrectNumeric(numberOfWorkersTextField.getText())) {
            numberOfWorkers_FromTextField = 0;
        } else numberOfWorkers_FromTextField = convert.integer(numberOfWorkersTextField.getText());
        return numberOfWorkers_FromTextField;
    }

    SortPersonType getSortPersonType_BySortText(String value) {
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

    PersonType returnWorkersType(String value) {
        switch (value) {
            case "ACTUAL":
                return PersonType.ACTUAL;
            case "ACTUAL_AND_REMOVED":
                return PersonType.ACTUAL_AND_REMOVED;
            case "REMOVED":
                return PersonType.REMOVED;
            default:
                return null;
        }
    }

    PersonType workerType(String value) {
        if ("REMOVED".equals(value)) {
            return PersonType.REMOVED;
        } else if ("ACTUAL".equals(value)) {
            return PersonType.ACTUAL;
        } else return null;
    }

    int getEncryptMove_Number(int DEFAULT_ENCRYPT_LEVEL,
                              TextField encryptMoveField) {
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

    private static boolean is_CorrectNumeric(String textFieldContent) {
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

}
