/*
 * Copyright © 2020 MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) and Mateusz Niedbał
 * As of January 2020, all rights in any software published by MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) & Mateusz Niedbał will remain with the author. Contact the author with any permission requests.
 */

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
