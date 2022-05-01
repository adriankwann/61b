package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;
import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Adrian Kwan
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        while (_input.hasNextLine()) {
            String temp = _input.nextLine();
            if (temp.isBlank()) {
                _output.println();
                continue;
            }
            if (temp.charAt(0) == '*') {
                setUp(machine, temp);
            } else {
                String conv = temp;
                String res = machine.convert(conv);
                printMessageLine(res);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String a = _config.next();
            if (!_config.hasNext("\\d")) {
                throw new EnigmaException("Empty Config");
            }
            if (a.contains("*") || a.contains("(") || a.contains(")")) {
                throw new EnigmaException("Invalid Characters in Alphabet!");
            }
            _alphabet = new Alphabet(a);
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Invalid Number for numRotors");
            }
            int numRotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Invalid Number for numPawls");
            }
            int pawls = _config.nextInt();
            if (pawls <= 0 || numRotors <= 0) {
                throw new EnigmaException("Number of Rotors or Pawls "
                        + "cannot be 0 or less");
            }
            java.util.Set<Rotor> allRotors = new java.util.HashSet<Rotor>();
            while (_config.hasNextLine()) {
                Rotor r = readRotor();
                if (r != null) {
                    allRotors.add(r);
                } else {
                    break;
                }
            }
            if (allRotors.size() < numRotors) {
                throw new EnigmaException("Number of rotors in machine "
                        + "exceeds number of rotors given");
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            if (!_config.hasNext()) {
                return null;
            }
            String name = _config.next();
            if (name.contains("(") || name.contains(")")) {
                throw new EnigmaException("Incorrect name format!");
            }
            String notches = _config.next();
            String p = "";
            String currentPerm;
            while (_config.hasNext(Pattern.compile("\\(.*\\)"))) {
                currentPerm = _config.next();
                p = p + currentPerm;
            }
            if (notches.charAt(0) != 'M' && notches.charAt(0) != 'm'
                    && notches.charAt(0) != 'R' && notches.charAt(0) != 'r'
                    && notches.charAt(0) != 'N' && notches.charAt(0) != 'n') {
                throw new EnigmaException("Incorrect Rotor Indicator "
                        + "(R, N, M only)!");
            }
            Permutation perm = new Permutation(p, _alphabet);
            if (notches.charAt(0) == 'M' || notches.charAt(0) == 'm') {
                String notch = notches.substring(1);
                if (notch.isBlank()) {
                    throw new EnigmaException("Moving Rotors "
                            + "must have a notch");
                }
                return new MovingRotor(name, perm, notch);
            } else if (notches.charAt(0) == 'R' || notches.charAt(0) == 'r') {
                return new Reflector(name, perm);
            } else {
                return new FixedRotor(name, perm);
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] s = settings.split(" ");
        if (s.length - 1 < M.numRotors()) {
            throw new EnigmaException("Wrong Settings Config. "
                    + "Not Enough Arguments.");
        }
        int ind = 0;
        if (!s[0].equals("*")) {
            throw new EnigmaException("Wrong Settings Config");
        }
        String[] rotors = new String[M.numRotors()];
        ind += 1;
        java.util.Set<String> check = new java.util.HashSet<>();
        for (int i = 1; i < M.numRotors() + 1; i++) {
            rotors[i - 1] = s[i];
            ind += 1;
            if (!check.add(s[i])) {
                throw new EnigmaException("Repeated Rotors");
            }
        }
        M.insertRotors(rotors);
        if (!M.getRotor(0).reflecting()) {
            throw new EnigmaException("Rotor at Pos 0 is not a reflector");
        }
        M.setRotors(s[ind]);
        ind += 1;
        String ring = "";
        if (ind < s.length && s[ind].matches("\\w+")) {
            ring = s[ind];
            ind += 1;
        }
        M.setRing(ring);
        String plug = "";
        if (ind < s.length) {
            for (int i = ind; i < s.length; i++) {
                plug += s[i];
            }
        }
        Permutation plugboard = new Permutation(plug, _alphabet);
        M.setPlugboard(plugboard);
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String[] groups;
        if (msg.length() % 5 == 0) {
            groups = new String[Math.floorDiv(msg.length(), 5)];
        } else {
            groups = new String[Math.floorDiv(msg.length(), 5) + 1];
        }
        int indGroup = 0;
        int indMsg = 0;
        int len = msg.length();
        while (len >= 5) {
            groups[indGroup] = msg.substring(indMsg, indMsg + 5);
            indGroup += 1;
            indMsg += 5;
            len -= 5;
        }
        if (msg.length() % 5 != 0) {
            groups[groups.length - 1] = msg.substring(indMsg);
        }
        for (int i = 0; i < groups.length; i++) {
            if (i == groups.length - 1) {
                _output.print(groups[i]);
            } else {
                _output.print(groups[i] + "\s");
            }
        }
        _output.print("\n");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

}
