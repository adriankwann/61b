package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Adrian Kwan
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean atNotch() {
        char c = this.permutation().alphabet().toChar(this.setting());
        for (char ch: _notches.toCharArray()) {
            if (ch == c) {
                return true;
            }
        }
        return false;
    }

    @Override
    void setRsetting(char r) {
        setRsetting(this.alphabet().toInt(r));
    }

    @Override
    void advance() {
        this.set(this.setting() + 1);
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    String notches() {
        return _notches;
    }

    /** The Notches of the Rotor. */
    private String _notches;

}
