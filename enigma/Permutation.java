package enigma;


import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Adrian Kwan
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _cycles = cycles;
        _alphabet = alphabet;
        int[] result = new int[_alphabet.size()];
        int[] inverse = new int[_alphabet.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
            inverse[i] = i;
        }
        int cyclestart = cycles.indexOf("(");
        while (cyclestart >= 0) {
            cyclestart += 1;
            char first = cycles.charAt(cyclestart);
            while (cycles.charAt(cyclestart) != ')') {
                while (Character.isWhitespace(cycles.charAt(cyclestart))) {
                    cyclestart += 1;
                }
                char current = cycles.charAt(cyclestart);
                char next = cycles.charAt(cyclestart + 1);
                if (next == ')') {
                    result[_alphabet.toInt(current)] = _alphabet.toInt(first);
                    inverse[_alphabet.toInt(first)] = _alphabet.toInt(current);
                } else {
                    result[_alphabet.toInt(current)] = _alphabet.toInt(next);
                    inverse[_alphabet.toInt(next)] = _alphabet.toInt(current);
                }
                cyclestart += 1;
            }
            cyclestart = cycles.indexOf("(", cyclestart);
        }
        _permutation = result;
        _invpermutation = inverse;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int r = wrap(p);
        return _permutation[r];
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int r = wrap(c);
        return _invpermutation[r];
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _permutation.length; i++) {
            if (_permutation[i] == i) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** The Permutation Array. */
    private int[] _permutation;
    /** The Inverse Array. */
    private int[] _invpermutation;
    /** The Cycles. */
    private String _cycles;
}
