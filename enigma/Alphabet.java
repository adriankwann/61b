package enigma;

import java.util.HashSet;
import java.util.Set;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Adrian Kwan
 */
class Alphabet {
    /** The characters. */
    private String _chars;
    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may bgoo
     *  e duplicated. */
    Alphabet(String chars) {
        Set<Character> s = new HashSet<Character>();
        for (char ch: chars.toCharArray()) {
            s.add(ch);
        }
        if (s.size() != chars.length()) {
            throw new EnigmaException("Duplicates in Alphabet!");
        } else {
            this._chars = chars;
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return !(_chars.indexOf(ch) == -1);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < size() && index >= 0) {
            return _chars.charAt(index);
        } else {
            throw new EnigmaException("Index Out Of Range for Given Alphabet!");
        }
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (contains(ch)) {
            return _chars.indexOf(ch);
        } else {
            throw new EnigmaException("Character is not in Alphabet!");
        }
    }

}
