/*
 * Copyright © 2020 MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) and Mateusz Niedbał
 * As of January 2020, all rights in any software published by MRN-Design (brand owned by Manufacture-MRN sp. z o.o.) & Mateusz Niedbał will remain with the author. Contact the author with any permission requests.
 */

package personnelInfo.mechanics;

public class Encrypting {

    //public methods:
    public static String encrypt(String textToEncrypt,
                                 int encryptMove) {
        return startCoding(textToEncrypt, encryptMove, true);
    }

    public static String decrypt(String secretToDecrypt,
                                 int encryptMove) {
        return startCoding(secretToDecrypt, encryptMove, false);
    }

    // -----------------------------------------------------------------------------------------------------
    //private methods:
    private static String startCoding(String plainText,
                                      int codeMove,
                                      boolean isCryptOr_FalseIfIsDecrypt) {
        char[] listOfCharacters = new char[plainText.length()];
        StringBuilder temp = new StringBuilder();
        codingText(codeMove, listOfCharacters, plainText, isCryptOr_FalseIfIsDecrypt, temp);
        return String.valueOf(temp);
    }

    private static void codingText(int codeMove,
                                   char[] listOfCharacters,
                                   String plainText,
                                   boolean isCryptOr_FalseIfIsDecrypt,
                                   StringBuilder temp){
        for (int i = 0; i < listOfCharacters.length; i++) {
            listOfCharacters[i] = plainText.charAt(i);
            codingOrDecodingByStatement(i,codeMove,listOfCharacters,isCryptOr_FalseIfIsDecrypt);
            temp.append(listOfCharacters[i]);
        }
    }

    private static void codingOrDecodingByStatement(int counter,
                                                    int codeMove,
                                                    char[] listOfCharacters,
                                                    boolean isCryptOr_FalseIfIsDecrypt) {
        if (Character.isLetter(listOfCharacters[counter])) {
            char codingChar = listOfCharacters[counter];
            if (isCryptOr_FalseIfIsDecrypt) {
                codingChar = encryptInner(codingChar, codeMove);
            } else {
                codingChar = decryptInner(codingChar, codeMove);
            }
            listOfCharacters[counter] = codingChar;
        }
    }


    private static char encryptInner(char encryptingChar,
                                     int encryptMove) {
        int counter = 0;
        while (counter < encryptMove) {
            encryptingChar++;
            if (encryptingChar >= 123) encryptingChar = 65;
            counter++;
        }
        return encryptingChar;
    }

    private static char decryptInner(char encryptingChar,
                                     int encryptMove) {
        int counter = 0;
        while (counter < encryptMove) {
            encryptingChar--;
            if (encryptingChar <= 64) encryptingChar = 122;
            counter++;
        }
        return encryptingChar;
    }

}
