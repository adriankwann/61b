package gitlet;

import java.io.File;
import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Adrian Kwan
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        checkEmptyArgs(args);
        Git g = new Git();
        switch (args[0]) {
        case "init":
            checkArgsLengthInit(args);
            g.setupInit();
            break;
        case "add":
            checkArgsLength(args, 2);
            g.add(args[1]);
            break;
        case "commit":
            g.commit(args);
            break;
        case "log":
            checkArgsLength(args, 1);
            g.log();
            break;
        case "checkout":
            checkInit();
            g.checkout(args);
            break;
        case "global-log":
            checkArgsLength(args, 1);
            g.globallog();
            break;
        case "rm":
            checkArgsLength(args, 2);
            g.rm(args[1]);
            break;
        case "find":
            checkArgsLength(args, 2);
            g.find(args[1]);
            break;
        case "branch":
            checkArgsLength(args, 2);
            g.branch(args[1]);
            break;
        case "rm-branch":
            checkArgsLength(args, 2);
            g.rmbranch(args[1]);
            break;
        case "reset":
            checkArgsLength(args, 2);
            g.reset(args[1]);
            break;
        case "status":
            checkArgsLength(args, 1);
            g.status();
            break;
        case "merge":
            checkArgsLength(args, 2);
            g.merge(args[1]);
            break;
        default:
            defaultMethod();
        }
        return;
    }

    /** Runs when a command doesn't exist. */
    public static void defaultMethod() {
        System.out.println("No command with that name exists.");
        System.exit(0);
    }

    /** Verifies that a gitlet repository has been initialized. */
    public static void checkInit() {
        if (!new File(".gitlet").exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    /**
     * Helper function for main, used in Init.
     * @param args Args from Main.
     */
    public static void checkArgsLengthInit(String [] args) {
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /**
     * Helper function for Main.
     * @param args What is passed into Main by the user.
     * @param l Length needed.
     */
    public static void checkArgsLength(String[] args, int l) {
        checkInit();
        if (args.length != l) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /**
     * Check if there was a command entered.
     * @param args The Args passed in.
     */
    public static void checkEmptyArgs(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
    }

}
