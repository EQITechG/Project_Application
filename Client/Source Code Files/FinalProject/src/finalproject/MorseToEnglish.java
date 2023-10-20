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
public class MorseToEnglish {

  /** Variable Declaration */
  private String c = "";

  public String convertToEnglish( String string )
  {
    if ( string.equals( ".-" ) ) {
          c = "A";
      }

    else if ( string.equals( "-..." ) ) {
          c = "B";
      }

    else if ( string.equals( "-.-." ) ) {
          c = "C";
      }

    else if ( string.equals( "-.." ) ) {
          c = "D";
      }

    else if ( string.equals( "." ) ) {
          c = "E";
      }

    else if ( string.equals( "..-." ) ) {
          c = "F";
      }

    else if ( string.equals( "--." ) ) {
          c = "G";
      }

    else if ( string.equals( "...." ) ) {
          c = "H";
      }

    else if ( string.equals( ".." ) ) {
          c = "I";
      }

    else if ( string.equals( ".---" ) ) {
          c = "J";
      }

    else if ( string.equals( "-.-" ) ) {
          c = "K";
      }

    else if ( string.equals( ".-.." ) ) {
          c = "L";
      }

    else if ( string.equals( "--" ) ) {
          c = "M";
      }

    else if ( string.equals( "-." ) ) {
          c = "N";
      }

    else if ( string.equals( "---" ) ) {
          c = "O";
      }

    else if ( string.equals( ".--." ) ) {
          c = "P";
      }

    else if ( string.equals( "--.-" ) ) {
          c = "Q";
      }

    else if ( string.equals( ".-." ) ) {
          c = "R";
      }

    else if ( string.equals( "..." ) ) {
          c = "S";
      }

    else if ( string.equals( "-" ) ) {
          c = "T";
      }

    else if ( string.equals( "..-" ) ) {
          c = "U";
      }

    else if ( string.equals( "...-" ) ) {
          c = "V";
      }

    else if ( string.equals( ".--." ) ) {
          c = "W";
      }

    else if ( string.equals( "-..-" ) ) {
          c = "X";
      }

    else if ( string.equals( "-.--" ) ) {
          c = "Y";
      }

    else if ( string.equals( "--.." ) ) {
          c = "Z";
      }

    else if ( string.equals( ".----" ) ) {
          c = "1";
      }

    else if ( string.equals( "..---" ) ) {
          c = "2";
      }

    else if ( string.equals( "...--" ) ) {
          c = "3";
      }

    else if ( string.equals( "....-" ) ) {
          c = "4";
      }

    else if ( string.equals( "....." ) ) {
          c = "5";
      }

    else if ( string.equals( "-...." ) ) {
          c = "6";
      }

    else if ( string.equals( "--..." ) ) {
          c = "7";
      }

    else if ( string.equals( "---.." ) ) {
          c = "8";
      }

    else if ( string.equals( "----." ) ) {
          c = "9";
      }

    else if ( string.equals( "-----" ) ) {
          c = "0";
      }

    else if ( string.equals( "\\w" ) ) {
          c = " ";
      }

    else if ( string.equals( "   " ) ) {
          c = " ";
      }

    else {
          JOptionPane.showMessageDialog( null,
                    "'" + string + "' is not a valid Morse Code",
                    "Error", JOptionPane.ERROR_MESSAGE );
      }


    return c;
  }
} /** End class MorseToEnglish */

