package personnelInfo.frameFX;


import javafx.scene.control.Button;
import personnelInfo.mechanics.Person;

class WorkerField {

    private Button button;
    private Person person;

    WorkerField(Person person) {
        button = new Button();
        this.person = person;
    }

    Button getButton() {
        return button;
    }

    Person getPerson() {
        return person;
    }
}
