/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject;

/**
 *This code was copied from the below Author and moderated by me
 * Original Name: Morse Code Ver. 1.0
 * Created by Melody King
 */
 

import javax.swing.*;
public class EnglishToMorse  {

   /** Variable declaration */
   private String morseCode = new String( "" );

  /** Convert each character into equivalent Morse Code */
  public String convertToMorse( char c )
  {
    switch( c )
    {
        case 'A':
        case 'a':
              morseCode = ".-";
              break;

        case 'B':
        case 'b':
              morseCode = "-...";
                  break;

        case 'C':
        case 'c':
              morseCode = "-.-.";
              break;

        case 'D':
        case 'd':
              morseCode = "-..";
              break;

        case 'E':
        case 'e':
              morseCode = ".";
              break;

        case 'F':
        case 'f':
              morseCode = "..-.";
              break;

        case 'G':
        case 'g':
             morseCode = "--.";
             break;

       case 'H':
       case 'h':
             morseCode = "....";
             break;

       case 'I':
       case 'i':
             morseCode = "..";
             break;

       case 'J':
       case 'j':
             morseCode = ".---";
             break;

       case 'K':
       case 'k':
             morseCode = "-.-";
             break;

       case 'L':
       case 'l':
             morseCode = ".-..";
             break;

       case 'M':
       case 'm':
             morseCode = "--";
             break;

       case 'N':
       case 'n':
             morseCode = "-.";
             break;

       case 'O':
       case 'o':
             morseCode = "---";
             break;

       case 'P':
       case 'p':
             morseCode = ".--.";
             break;

       case 'Q':
       case 'q':
             morseCode = "--.-";
             break;

       case 'R':
       case 'r':
             morseCode = ".-.";
             break;

       case 'S':
       case 's':
             morseCode = "...";
             break;

       case 'T':
       case 't':
             morseCode = "-";
             break;

       case 'U':
       case 'u':
             morseCode = "..-";
             break;

       case 'V':
       case 'v':
             morseCode = "...-";
             break;

       case 'W':
       case 'w':
             morseCode = ".--";
             break;

       case 'X':
       case 'x':
             morseCode = "-..-";
             break;

       case 'Y':
       case 'y':
             morseCode = "-.--";
             break;

       case 'Z':
       case 'z':
             morseCode = "--..";
             break;

       case '1':
             morseCode = ".----";
             break;

       case '2':
             morseCode = "..---";
             break;

       case '3':
             morseCode = "...--";
             break;

       case '4':
             morseCode = "....-";
             break;

       case '5':
             morseCode = ".....";
             break;

       case '6':
             morseCode = "-....";
             break;

       case '7':
             morseCode = "--...";
             break;

       case '8':
             morseCode = "---..";
             break;

       case '9':
             morseCode = "----.";
             break;

       case '0':
             morseCode = "-----";
             break;

       case '.':
       case ',':
       case '?':
       case '!':
             morseCode = "   ";
             break;

       default:
             JOptionPane.showMessageDialog( null,
                "Character '" + c + "' doesn't have an equivalent Morse Code.",
                "Error", JOptionPane.ERROR_MESSAGE );
             break;

    }

    return morseCode;
  }

} /** End class EnglishToMorse */

