package personnelInfo.frameFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import personnelInfo.mechanics.enums.SortType;
import personnelInfo.mechanics.enums.WorkersType;

public class BoxListCollectionsInitialize {

    private ChoiceBox<String> sortByChoiceBox;
    private ChoiceBox<String> workersTypeShowChoiceBox;
    private ChoiceBox<String> workerStatusChoiceBox;

    public BoxListCollectionsInitialize(ChoiceBox<String> sortByChoiceBox,
                                        ChoiceBox<String> workersTypeShowChoiceBox,
                                        ChoiceBox<String> workerStatusChoiceBox) {
        this.sortByChoiceBox = sortByChoiceBox;
        this.workersTypeShowChoiceBox = workersTypeShowChoiceBox;
        this.workerStatusChoiceBox = workerStatusChoiceBox;

        initializeChoiceBoxes();
    }

    private void initializeChoiceBoxes() {
        sortByChoiceBox.setValue(SortType.SORT_BY_ID.toString());
        sortByChoiceBox.setItems(getSortByChoiceBoxList());
        workersTypeShowChoiceBox.setValue(WorkersType.ACTUAL_AND_REMOVED.toString());
        workersTypeShowChoiceBox.setItems(getWorkersType_Sort_ChoiceBoxList());
        workerStatusChoiceBox.setValue(WorkersType.ACTUAL.toString());
        workerStatusChoiceBox.setItems(getWorkersTypeList());
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
}
