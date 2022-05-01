package gitlet;
import java.io.File;

/** A class to represent Blobs, stored under .gitlet/blobs.
 *  @author Adrian Kwan
 */

public class Blob {
    /** A file reference to where all BLOBS are stored. */
    private static final File BLOB_FOLDER = new File(".gitlet/blobs");

    /**
     * Constructor for Class BLOB.
     * @param file The file name.
     */
    public Blob(String file) {
        File f = new File(file);
        _file = file;
        _info = Utils.readContentsAsString(f);
        _hash = createBlobHash();

    }

    /**
     * Creates a new blob hash.
     * @return The new hash.
     */
    String createBlobHash() {
        return Utils.sha1(_file + _info);
    }

    /** Serializes and saves Blob B to the BLOB_FOLDER. */
    public static void saveBlob(Blob b) {
        File tempBlobFile = Utils.join(BLOB_FOLDER, b.getHash());
        Utils.writeContents(tempBlobFile, b.getInfo());
    }

    /** Returns the current Blob's Hash. */
    public String getHash() {
        return _hash;
    }

    /** Returns the current Blob's text. */
    public String getInfo() {
        return _info;
    }

    /** The SHA-1 Hash representation of this Blob. */
    private String _hash;

    /** The file name associated with this Blob. */
    private String _file;

    /** The String representation text of the Blob's contents. */
    private String _info;
}
