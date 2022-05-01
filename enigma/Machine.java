package enigma;

import java.util.Collection;


import static enigma.EnigmaException.*;


/** Class that represents a complete enigma machine.
 *  @author Adrian Kwan
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[_numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotors[k];
    }

    Rotor[] getRotors() {
        return _rotors;
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length != numRotors()) {
            throw new EnigmaException("Number of Rotors Inserted Does "
                    + "Not Equal to Number of Rotors Initialized");
        }
        for (Rotor r: _allRotors) {
            for (int i = 0; i < rotors.length; i++) {
                if (r.name().equals(rotors[i])) {
                    _rotors[i] = r;
                }
            }
        }
        for (int i = 0; i < _rotors.length; i++) {
            if (_rotors[i] == null) {
                throw new EnigmaException("Bad Rotor Name");
            }
        }
        if (!_rotors[0].reflecting()) {
            throw new EnigmaException("Rotor at Position 0 is not a reflector");
        }
        int count = 0;
        for (Rotor r: _rotors) {
            if (r.rotates()) {
                count += 1;
            }
        }
        if (count != numPawls()) {
            throw new EnigmaException("Wrong number of arguments");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        char[] ch = setting.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (!alphabet().contains(ch[i])) {
                throw new EnigmaException("Setting Character not in Alphabet!");
            }
            _rotors[i + 1].set(ch[i]);
        }
    }

    /** Sets the ring settings from RING. */
    void setRing(String ring) {
        char[] temp = ring.toCharArray();
        if (!ring.isBlank()) {
            for (int i = 1; i < _numRotors; i++) {
                _rotors[i].setRsetting(temp[i - 1]);
            }
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean[] atNotchArray = new boolean[_numRotors];
        atNotchArray[_numRotors - 1] = true;
        for (int i = _rotors.length - 1; i > _rotors.length - _pawls; i--) {
            if (_rotors[i] == null) {
                throw new EnigmaException("rotors has null rotor");
            }
            if (_rotors[i].atNotch()) {
                atNotchArray[i] = true;
                atNotchArray[i - 1] = true;
            }
        }
        for (int i = 0; i < atNotchArray.length; i++) {
            if (atNotchArray[i]) {
                _rotors[i].advance();
            }
        }

    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int r = c;
        for (int i = _rotors.length - 1; i >= 0; i--) {
            r = _rotors[i].convertForward(r);
        }
        for (int j = 1; j < _rotors.length; j++) {
            r = _rotors[j].convertBackward(r);
        }
        return r;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (char ch: msg.toCharArray()) {
            if (_alphabet.contains(ch)) {
                result += alphabet().toChar(convert(alphabet().toInt(ch)));
            }
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors in machine. */
    private int _numRotors;

    /** Number of Pawls in machine. */
    private int _pawls;

    /** Collection storing all possible rotors. */
    private Collection<Rotor> _allRotors;

    /** Array storing the rotors int the machine. */
    private Rotor[] _rotors;

    /** Storing the plugboard. */
    private Permutation _plugboard;
}
