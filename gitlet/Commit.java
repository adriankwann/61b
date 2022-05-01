package gitlet;

import java.io.Serializable;
import java.util.HashMap;


/** A class to represent a singular commit, stored under .gitlet/commits.
 *  @author Adrian Kwan
 */

public class Commit implements Serializable {
    /** Empty commit, used in init. */
    public Commit() {
        _message = "initial commit";
        _timestamp = "Wed Dec 31 16:00:00 1969 -0800";
        _hash = createHash();
        _parent = null;
        _commitblobmaps = new HashMap<>();
        _secondParent = null;
    }

    /**
     * Default Constructor for Commit.
     * @param m The Commit Message.
     * @param t The Commit Date.
     * @param p The Commit's Parent hash.
     * @param blobMap The Commit's Blob HashMap.
     */
    public Commit(String m, String t, String p,
                  HashMap<String, String> blobMap) {
        _message = m;
        _timestamp = t;
        _hash = createHash();
        _parent = p;
        _commitblobmaps = blobMap;
        _secondParent = null;
    }

    /**
     * Copies the given commit object and returns a new Commit.
     * @param c The Commit to be copied, often the parent.
     * @param message The new Commit's message.
     * @param date The new Commit's date.
     * @return The new commit that has been copied.
     */
    public static Commit copy(Commit c, String message, String date) {
        String parentHash = c.getHash();
        HashMap<String, String> prevBlobMap = c.getCommitBlobMap();
        return new Commit(message, date, parentHash, prevBlobMap);
    }

    /** Updates the given Commit's blobMaps accordingly.
     * @param fileName The file name associated with this Commit.
     * @param blobHash The Commit's SHA-1 hash representation.
     */
    public void putCommitBlobMap(String fileName, String blobHash) {
        _commitblobmaps.put(fileName, blobHash);
    }

    /** Returns the SHA-1 Hash representation of this Commit. */
    public String getHash() {
        return _hash;
    }

    /** Returns the merged parent for this Commit, default = Null. */
    public String getSecondParent() {
        return _secondParent;
    }

    /**
     * Setter method for the second parent of Commit.
     * @param s The hash to be set to.
     */
    public void setSecondParent(String s) {
        _secondParent = s;
    }

    /** Returns the first and default parent for this Commit. */
    public String getFirstParent() {
        return _parent;
    }

    /**
     * Creates a new SHA-1 Hash for this Commit.
     * @return The Hash Code.
     */
    public String createHash() {
        byte[] temp = Utils.serialize(this);
        return Utils.sha1(temp);
    }

    /** Re-Hash the current Commit and updates the _hash variable. */
    public void updateHash() {
        _hash = Utils.sha1(Utils.serialize(this));
    }

    /**
     * Getter method for the BlobMaps associated with this Commit.
     * @return The BlobMaps for this Commit.
     */
    public HashMap<String, String> getCommitBlobMap() {
        return _commitblobmaps;
    }

    /**
     * Getter method for the Commit message.
     * @return This Commit's commit message.
     */
    public String getCommitMessage() {
        return _message;
    }

    /**
     * Getter method for this Commit's Date.
     * @return The date.
     */
    public String getCommitTimestamp() {
        return _timestamp;
    }

    /** The SHA-1 Hash of this Commit's Parent. */
    private String _parent;

    /** The SHA-1 Hash of the current Commit. */
    private String _hash;

    /** The SHA-1 Hash of this Commit's merged parent. */
    private String _secondParent;

    /** The commit message for the current Commit. */
    private String _message;

    /** The commit date for the current Commit. */
    private String _timestamp;

    /**
     * The BlobMaps associated with this given Commit.
     * Maps the File Name to the Blob Hash.
     */
    private HashMap<String, String> _commitblobmaps;
}
