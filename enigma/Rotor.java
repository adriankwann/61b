package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Adrian Kwan
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
        _rsetting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    void setRsetting(int r) {
        _rsetting = r;
    }

    void setRsetting(char r) {
        int temp = _permutation.alphabet().toInt(r);
        setRsetting(temp);
    }
    /** Set setting() to POSN.  */
    void set(int posn) {
        if (posn >= 0) {
            _setting = posn % size();
        } else {
            _setting = size() + posn;
        }
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        set(alphabet().toInt(cposn));
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int actual = _setting + p % size() - _rsetting;
        int result = _permutation.permute(actual);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        result = result - _setting;
        if (result >= 0) {
            return (result + _rsetting) % size();
        } else {
            return result + size() + _rsetting;
        }
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int actual = _setting + e % size() - _rsetting;
        int result = _permutation.invert(actual);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        result = result - _setting;
        if (result >= 0) {
            return (result + _rsetting) % size();
        } else {
            return result + size() + _rsetting;
        }
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return "";
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** Current Setting Of The Rotor. */
    private int _setting;

    /** ring setting of rotor. */
    private int _rsetting;


}
