/*
 * Copyright © 2020 MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) and Mateusz Niedbał
 * As of January 2020, all rights in any software published by MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) & Mateusz Niedbał will remain with the author. Contact the author with any permission requests.
 */

package personnelInfo.mechanics;

public class Encrypting {

    private char[] listOfCharacters;
    private boolean isCryptOr_FalseIfIsDecrypt;
    private int codeMove;
    private String text;
    private StringBuilder codeText;

    public Encrypting() {

    }

    //public methods:
    public String encrypt(String text, int codeMove) {
        this.codeMove = codeMove;
        this.text = text;
        return startCoding(true);
    }

    public String decrypt(String text, int codeMove) {
        this.codeMove = codeMove;
        this.text = text;
        return startCoding(false);
    }

    // -----------------------------------------------------------------------------------------------------
    //private methods:
    private String startCoding(boolean isCryptOr_FalseIfIsDecrypt) {
        this.isCryptOr_FalseIfIsDecrypt = isCryptOr_FalseIfIsDecrypt;
        listOfCharacters = new char[text.length()];
        codeText = new StringBuilder();
        codingText();
        return String.valueOf(codeText);
    }

    private void codingText(){
        for (int i = 0; i < listOfCharacters.length; i++) {
            listOfCharacters[i] = text.charAt(i);
            codingOrDecodingByStatement(i);
            codeText.append(listOfCharacters[i]);
        }
    }

    private void codingOrDecodingByStatement(int counter) {
        if (Character.isLetter(listOfCharacters[counter])) {
            char codingChar = listOfCharacters[counter];
            if (isCryptOr_FalseIfIsDecrypt) {
                codingChar = encryptInner(codingChar);
            } else {
                codingChar = decryptInner(codingChar);
            }
            listOfCharacters[counter] = codingChar;
        }
    }

    private char encryptInner(char encryptingChar) {
        int counter = 0;
        while (counter < codeMove) {
            encryptingChar++;
            if (encryptingChar >= 123) encryptingChar = 65;
            if (encryptingChar >= 91 && encryptingChar <= 96) encryptingChar = 97;
            counter++;
        }
        return encryptingChar;
    }

    private char decryptInner(char encryptingChar) {
        int counter = 0;
        while (counter < codeMove) {
            encryptingChar--;
            if (encryptingChar <= 64) encryptingChar = 122;
            if (encryptingChar >= 91 && encryptingChar <= 96) encryptingChar = 90;
            counter++;
        }
        return encryptingChar;
    }

}
