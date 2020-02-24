/*
 * Copyright © 2020 MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) and Mateusz Niedbał
 * As of January 2020, all rights in any software published by MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) & Mateusz Niedbał will remain with the author. Contact the author with any permission requests.
 */

package personnelInfo.frameFX;


import javafx.scene.control.Button;
import personnelInfo.mechanics.Person;

public class PersonField {

    private Button button;
    private Person person;
    private int personFieldId;

    PersonField(Person person, int personFieldId) {
        this.personFieldId = personFieldId;
        this.button = new Button();
        this.person = person;
    }

    Button getButton() {
        return button;
    }

    Person getPerson() {
        return person;
    }
}
