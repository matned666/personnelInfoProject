package personnelInfo.frameFX;

import personnelInfo.mechanics.enums.SortPersonType;
import personnelInfo.mechanics.enums.WorkersType;

class SortingType {

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

    WorkersType returnWorkersType(String value) {
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

    WorkersType workerType(String value) {
        if ("REMOVED".equals(value)) {
            return WorkersType.REMOVED;
        } else if ("ACTUAL".equals(value)) {
            return WorkersType.ACTUAL;
        } else return null;
    }
}
