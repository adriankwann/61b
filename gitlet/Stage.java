package gitlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.io.Serializable;

/** A class to represent the Staging area, stored under .gitlet/staging.
 *  @author Adrian Kwan
 */

public class Stage implements Serializable {

    /** Initializes the Staging Area. */
    public Stage() {
        stageDeletions = new Stack<>();
        stageAdditions = new HashMap<>();
        fileNames = new ArrayList<>();
    }

    /** Clears the Staging Area. */
    public void clear() {
        stageDeletions.clear();
        stageAdditions.clear();
        fileNames.clear();
    }

    /**
     * Stages a file.
     * @param f The File Name.
     * @param h The Blob Hash.
     */
    public void add(String f, String h) {
        fileNames.add(f);
        stageAdditions.put(f, h);
    }

    /**
     * Removes a file for the next Commit.
     * @param f The File Name.
     */
    public void rm(String f) {
        stageDeletions.add(f);
        fileNames.add(f);
    }

    /**
     * Getter method for all files staged for addition.
     * @return The stageAdditions HashMap.
     */
    public HashMap<String, String> getAdd() {
        return stageAdditions;
    }

    /**
     * Getter method for all files staged for deletion.
     * @return The stageDeletions Stack.
     */
    public Stack<String> getDel() {
        return stageDeletions;
    }

    /**
     * Getter method for all files modified.
     * @return The fileNames ArrayList.
     */
    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    /**
     * Setter method for stage, used in serialization.
     * @param add The HashMap tracking files staged for addition.
     * @param del The Stack tracking files staged for deletion.
     * @param names The ArrayList tracking all modified files.
     */
    public void setter(HashMap<String, String> add,
                       Stack<String> del, ArrayList<String> names) {
        fileNames = names;
        stageAdditions = add;
        stageDeletions = del;
    }

    /** All file names that have been modified in the current stage. */
    private ArrayList<String> fileNames;

    /** Maps the file name to a blob hash, tracks files staged for addition. */
    private HashMap<String, String> stageAdditions;

    /** A stack of file names that have been staged for deletion. */
    private Stack<String> stageDeletions;
}
