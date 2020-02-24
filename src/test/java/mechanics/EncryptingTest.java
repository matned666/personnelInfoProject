package mechanics;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import personnelInfo.mechanics.Encrypting;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EncryptingTest {

    Encrypting code;

    @BeforeEach
    void setup(){
        code = new Encrypting();
    }

    @Test
    void isEncryptmentWorking(){
        String text = "Ala ma kota i go zje";
        String encryptedText = code.encrypt(text,1);

        assertEquals(encryptedText, "Bmb nb lpub j hp Akf" );
    }

    @Test
    void isDecryptmentWorking(){
        String text = "Bmb nb lpub j hp Akf";
        String encryptedText = code.decrypt(text,1);

        assertEquals(encryptedText, "Ala ma kota i go zje" );
    }

}