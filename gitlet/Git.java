package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Stack;
import java.util.List;
import java.util.Calendar;
import java.util.Set;
import java.io.IOException;

/** The main repository class for Gitlet.
 *  @author Adrian Kwan
 */

public class Git {

    /**
     * A constructor for the repository object.
     * Also sets data structures to their previous state if necessary.
     */
    public Git() {
        _gitlet = new File(".gitlet");
        if (!_gitlet.exists()) {
            return;
        } else {
            _CWD = new File(System.getProperty("user.dir"));
            _commits = Utils.join(_gitlet, "commits");
            _staging = Utils.join(_gitlet, "staging");
            _blobs = Utils.join(_gitlet, "blobs");
            _stageDelFile = Utils.join(_staging, "stagedel");
            _stageAddFile = Utils.join(_staging, "stageadd");
            _stageNamesFile = Utils.join(_staging, "stagenames");
            _headFile = Utils.join(_gitlet, "head");
            _commitMapFile = Utils.join(_gitlet, "commitMap");
            _parentMapFile = Utils.join(_gitlet, "parentMap");
            _branchMapFile = Utils.join(_gitlet, "branchMap");
            _branchNamesFile = Utils.join(_gitlet, "branchNames");
            _mergedMapFile = Utils.join(_gitlet, "mergedMap");
            readHead();
            readCommitMaps();
            readStage();
        }
    }

    /**
     * The initialization of the gitlet repository.
     * @throws IOException Serializing Exception.
     */
    public void setupInit() throws IOException {
        _gitlet = new File(".gitlet");
        if (_gitlet.exists()) {
            System.out.println("A Gitlet version-control "
                    + "system already exists in the current directory.");
            System.exit(0);
        }
        _gitlet.mkdir();

        _commits = Utils.join(_gitlet, "commits");
        _commits.mkdir();
        _commitMapFile = Utils.join(_gitlet, "commitMap");
        _parentMapFile = Utils.join(_gitlet, "parentMap");
        _commitMapFile.createNewFile();
        _parentMapFile.createNewFile();
        _branchMapFile = Utils.join(_gitlet, "branchMap");
        _branchMapFile.createNewFile();
        _branchNamesFile = Utils.join(_gitlet, "branchNames");
        _branchNamesFile.createNewFile();
        _mergedMapFile = Utils.join(_gitlet, "mergedMap");
        _mergedMapFile.createNewFile();

        _staging = Utils.join(_gitlet, "staging");
        _staging.mkdir();
        _stageAddFile = Utils.join(_staging, "stageadd");
        _stageAddFile.createNewFile();
        _stageDelFile = Utils.join(_staging, "stagedel");
        _stageDelFile.createNewFile();
        _stageNamesFile = Utils.join(_staging, "stagenames");
        _stageNamesFile.createNewFile();

        _blobs = Utils.join(_gitlet, "blobs");
        _blobs.mkdir();

        Commit init = new Commit();
        String initHash = init.getHash();
        File initFile = Utils.join(_commits, initHash);
        Utils.writeObject(initFile, init);

        _head = "master";
        _headFile = Utils.join(_gitlet, "head");

        _headFile.createNewFile();

        _commitMap = new HashMap<>();
        _parentMap = new TreeMap<>();
        _branchMap = new HashMap<>();
        _mergedMap = new HashMap<>();
        _commitMap.put(initHash, init);
        _parentMap.put(initHash, null);
        _branchMap.put("master", initHash);
        _branchNames = new ArrayList<>();
        _branchNames.add("master");
        _stage = new Stage();
        writeStage(_stage);
        saveHead();
        saveCommitMaps();
    }

    /** Serializes and saves the current _head. */
    public void saveHead() {
        Utils.writeContents(_headFile, _head);
    }

    /** Updates the _head variable to its previous state. */
    @SuppressWarnings("unchecked")
    public void readHead() {
        _head = Utils.readContentsAsString(_headFile);
    }

    /** Serializes and saves the current map objects.
     * Includes _commitMap, _parentMap,
     * _branchMap, _branchNames, _mergedMap.
     */
    public void saveCommitMaps() {
        Utils.writeObject(_commitMapFile, _commitMap);
        Utils.writeObject(_parentMapFile, _parentMap);
        Utils.writeObject(_branchMapFile, _branchMap);
        Utils.writeObject(_branchNamesFile, _branchNames);
        Utils.writeObject(_mergedMapFile, _mergedMap);
    }

    /** Updates the map objects to its previous state. */
    @SuppressWarnings("unchecked")
    public void readCommitMaps() {
        _commitMap = Utils.readObject(_commitMapFile, HashMap.class);
        _parentMap = Utils.readObject(_parentMapFile, TreeMap.class);
        _branchMap = Utils.readObject(_branchMapFile, HashMap.class);
        _branchNames = Utils.readObject(_branchNamesFile, ArrayList.class);
        _mergedMap = Utils.readObject(_mergedMapFile, HashMap.class);
    }

    /**
     * Serializes and saves the current _stage.
     * @param s The Stage that is going to be saved.
     */
    public void writeStage(Stage s) {
        Utils.writeObject(_stageAddFile, s.getAdd());
        Utils.writeObject(_stageDelFile, s.getDel());
        Utils.writeObject(_stageNamesFile, s.getFileNames());
    }

    /** Updates the _stage to its previous state. */
    @SuppressWarnings("unchecked")
    public void readStage() {
        HashMap<String, String> add = Utils
                .readObject(_stageAddFile, HashMap.class);
        Stack<String> del = Utils
                .readObject(_stageDelFile, Stack.class);
        ArrayList<String> names = Utils
                .readObject(_stageNamesFile, ArrayList.class);
        _stage = new Stage();
        _stage.setter(add, del, names);
    }

    /**
     * Adds a file to the staging area for the next Commit.
     * @param file The file name.
     */
    public void add(String file) {
        File temp = new File(file);
        if (!temp.exists()) {
            System.out.println("File does not exist");
            System.exit(0);
        }
        Blob fileBlob = new Blob(file);
        String hash = _branchMap.get(_head);
        Commit current = readCommit(hash);
        if (current.getCommitBlobMap().
                containsValue(fileBlob.getHash())) {
            if (_stage.getAdd().containsKey(file)) {
                _stage.getAdd().remove(file);
            } else if (_stage.getDel().contains(file)) {
                _stage.getDel().remove(file);
            }
        } else {
            _stage.add(file, fileBlob.getHash());
            Blob.saveBlob(fileBlob);
        }
        writeStage(_stage);
    }

    /**
     * Helper method to check arg lengths before committing.
     * @param args The args passed in from Main.
     */
    public void commit(String... args) {
        Main.checkInit();
        if (args.length == 1) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        } else if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        Calendar c = Calendar.getInstance();
        String regex = "%1$ta %1$tb %1$te %1$tT %1$tY %1$tz";
        String date = String.format(regex, c);
        commit(args[1], date);
    }

    /**
     * Commits the current stage, updates commit map objects.
     * @param message The commit message.
     * @param date The commit date.
     */
    public void commit(String message, String date) {
        if (_stage.getAdd().size() == 0 && _stage.getDel().size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        String hash = _branchMap.get(_head);
        Commit parent = readCommit(hash);
        Commit newCommit = Commit.copy(parent, message, date);
        for (String fileName: _stage.getFileNames()) {
            if (_stage.getAdd().containsKey(fileName)) {
                String blobHash = _stage.getAdd().get(fileName);
                newCommit.putCommitBlobMap(fileName, blobHash);
            } else if (_stage.getDel().contains(fileName)) {
                newCommit.getCommitBlobMap().remove(fileName);
            }
        }
        newCommit.updateHash();
        _stage.clear();
        writeCommit(newCommit, newCommit.getHash());
        _commitMap.put(newCommit.getHash(), newCommit);
        _parentMap.put(newCommit.getHash(), _branchMap.get(_head));
        _branchMap.put(_head, newCommit.getHash());
        saveCommitMaps();
        saveHead();
        writeStage(_stage);
    }

    /** Serializes and saves the given COMMIT and HASH. */
    public void writeCommit(Commit commit, String hash) {
        File newCommitFile = Utils.join(_commits, hash);
        Utils.writeObject(newCommitFile, commit);
    }

    /** RETURNS a Commit object from its SHA-1 HASH. */
    public Commit readCommit(String hash) {
        File commitFile = Utils.join(_commits, hash);
        return Utils.readObject(commitFile, Commit.class);
    }

    /**
     * Starts at the head commit and prints
     * metadata until the initial commit is reached.
     */
    public void log() {
        String currHash = _branchMap.get(_head);
        while (currHash != null) {
            Commit curr = _commitMap.get(currHash);
            System.out.println("===");
            System.out.println("commit " + currHash);
            System.out.println("Date: " + curr.getCommitTimestamp());
            System.out.println(curr.getCommitMessage());
            System.out.println();
            currHash = _parentMap.get(currHash);
        }
    }

    /** Prints all commits and their metadata so far. */
    public void globallog() {
        List<String> commitHashes = Utils.plainFilenamesIn(_commits);
        for (String hash: commitHashes) {
            Commit temp = _commitMap.get(hash);
            System.out.println("===");
            System.out.println("commit " + hash);
            System.out.println("Date: " + temp.getCommitTimestamp());
            System.out.println(temp.getCommitMessage());
            System.out.println();
        }

    }

    /**
     * Remove a file from it's staging area.
     * Deletes a file from the CWD if it exists.
     * @param f The file name.
     */
    public void rm(String f) {
        File file = new File(_CWD, f);
        String hash = _branchMap.get(_head);
        Commit head = readCommit(hash);
        if (_stage.getFileNames().contains(f)) {
            _stage.getAdd().remove(f);
            _stage.getFileNames().remove(f);
        } else if (head.getCommitBlobMap().containsKey(f)) {
            _stage.rm(f);
            if (file.exists()) {
                Utils.restrictedDelete(file);
            }
        } else {
            System.out.println("No reason to remove the file");
            System.exit(0);
        }
        writeStage(_stage);
    }

    /**
     * The checkout method.
     * @param args Accounts for all three types of checkout.
     * @throws IOException
     */
    public void checkout(String... args) throws IOException {
        if (args.length != 2 && args.length != 3 && args.length != 4) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        Commit curr;
        String commitID;
        String fileName;
        if (args.length == 2) {
            String branch = args[1];
            checkout(branch);
            return;
        } else if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            commitID = null;
            fileName = args[2];
        } else {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            if (args[1].length() < _commitLength) {
                commitID = findPrefixID(args[1]);
            } else {
                commitID = args[1];
            }
            fileName = args[3];
        }
        if (commitID == null) {
            String hash = _branchMap.get(_head);
            curr = readCommit(hash);
        } else {
            if (!_commitMap.containsKey(commitID)) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            curr = readCommit(commitID);
        }
        if (!curr.getCommitBlobMap().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String blobHash = curr.getCommitBlobMap().get(fileName);
        File blobFile = Utils.join(_blobs, blobHash);
        File fileToChange = new File(_CWD, fileName);
        if (fileToChange.exists()) {
            Utils.restrictedDelete(fileToChange);
        }
        fileToChange.createNewFile();
        String blobText = Utils.readContentsAsString(blobFile);
        Utils.writeContents(fileToChange, blobText);
    }

    /**
     * An overload method used when a branch needs to be checked out.
     * @param branchName The branch name.
     * @throws IOException
     */
    public void checkout(String branchName) throws IOException {
        if (!_branchNames.contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (_head.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        List<String> fileNames = Utils.plainFilenamesIn(_CWD);
        fileNames.remove(".gitlet");
        String currentHash = _branchMap.get(_head);
        Commit currentCommit = _commitMap.get(currentHash);
        String newCommitHash = _branchMap.get(branchName);
        Commit newCommit = _commitMap.get(newCommitHash);
        for (String f: fileNames) {
            if (!currentCommit.getCommitBlobMap().containsKey(f)
                    && newCommit.getCommitBlobMap().containsKey(f)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String f: fileNames) {
            Utils.restrictedDelete(f);
        }
        for (String fileName: newCommit.getCommitBlobMap().keySet()) {
            File tempFile = new File(_CWD, fileName);
            tempFile.createNewFile();
            String blobHash = newCommit.getCommitBlobMap().get(fileName);
            File blobFile = Utils.join(_blobs, blobHash);
            String blobText = Utils.readContentsAsString(blobFile);
            Utils.writeContents(tempFile, blobText);
        }
        _stage.clear();
        _head = branchName;
        saveHead();
        writeStage(_stage);
    }

    /**
     * Finds a commit with the given message.
     * @param message The commit message.
     */
    public void find(String message) {
        List<String> commitList = Utils.plainFilenamesIn(_commits);
        int c = 0;
        for (String commitHash: commitList) {
            Commit temp = _commitMap.get(commitHash);
            if (temp.getCommitMessage().equals(message)) {
                System.out.println(commitHash);
                c++;
            }
        }
        if (c == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /** Initializes a new branch with given NAME. */
    public void branch(String name) {
        if (_branchMap.containsKey(name)) {
            System.out.println("A branch with the name already exists");
            System.exit(0);
        }
        String hash = _branchMap.get(_head);
        _branchMap.put(name, hash);
        _branchNames.add(name);
        saveCommitMaps();
    }

    /**
     * Removes a branch with given name.
     * No changes to the commit structure.
     * @param name The branch name.
     */
    public void rmbranch(String name) {
        if (!_branchMap.containsKey(name)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (_head.equals(name)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        _branchNames.remove(name);
        _branchMap.remove(name);
        saveCommitMaps();
    }

    /**
     * Helper method to find a commitID, given an abbreviation.
     * @param shortID An abbreviated version of a Commit ID.
     * @return The CommitID that has been found.
     */
    public String findPrefixID(String shortID) {
        List<String> commitIDs = Utils.plainFilenamesIn(_commits);
        for (String commitID: commitIDs) {
            for (int i = 0; i < shortID.length(); i++) {
                if (shortID.charAt(i) != commitID.charAt(i)) {
                    break;
                } else if (i == shortID.length() - 1) {
                    return commitID;
                }
            }
        }
        System.out.println("No commit with that id exists.");
        System.exit(0);
        return "";
    }

    /** The status method. */
    public void status() {
        System.out.println("=== Branches ===");
        java.util.Collections.sort(_branchNames, String.CASE_INSENSITIVE_ORDER);
        for (String branch: _branchNames) {
            if (branch.equals(_head)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
        ArrayList<String> staged = new ArrayList<>();
        ArrayList<String> removed = new ArrayList<>();
        String currentHash = _branchMap.get(_head);
        Commit current = _commitMap.get(currentHash);
        List<String> fileNames = Utils.plainFilenamesIn(_CWD);
        for (String file: fileNames) {
            File temp = new File(_CWD, file);
            Blob tempblob = new Blob(file);
            if (_stage.getAdd().containsKey(file)) {
                if (temp.exists() && _stage.getAdd().get(file)
                        .equals(tempblob.getHash())) {
                    staged.add(file);
                }
            }
        }
        for (String file: current.getCommitBlobMap().keySet()) {
            if (_stage.getDel().contains(file)) {
                removed.add(file);
            }
        }
        System.out.println("=== Staged Files ===");
        for (String f: staged) {
            System.out.println(f);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String f: removed) {
            System.out.println(f);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /**
     * Resets the current state of the repository.
     * @param commitID The commit ID to be resetted to.
     * @throws IOException Serializing Exception.
     */
    public void reset(String commitID) throws IOException {
        String currentHash = _branchMap.get(_head);
        Commit current = _commitMap.get(currentHash);
        if (commitID.length() < _commitLength) {
            commitID = findPrefixID(commitID);
        } else if (!_commitMap.containsKey(commitID)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit newCommit = readCommit(commitID);
        List<String> fileNames = Utils.plainFilenamesIn(_CWD);
        for (String f: fileNames) {
            if (!current.getCommitBlobMap().containsKey(f)
                    && newCommit.getCommitBlobMap().containsKey(f)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String f: fileNames) {
            Utils.restrictedDelete(f);
        }
        for (String fileName: newCommit.getCommitBlobMap().keySet()) {
            File tempFile = new File(_CWD, fileName);
            tempFile.createNewFile();
            String blobHash = newCommit.getCommitBlobMap().get(fileName);
            File blobFile = Utils.join(_blobs, blobHash);
            String blobText = Utils.readContentsAsString(blobFile);
            Utils.writeContents(tempFile, blobText);
        }
        _stage.clear();
        writeStage(_stage);
        _branchMap.put(_head, commitID);
        saveCommitMaps();
    }

    /**
     * Merges the given branch with the current branch.
     * @param branchname The given branch name.
     * @throws IOException Serializing exception.
     */
    public void merge(String branchname)
            throws IOException {
        if (!_branchMap.containsKey(branchname)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (branchname.equals(_head)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        } else if (!_stage.getAdd().isEmpty() || !_stage.getDel().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        String currentBranchHash = _branchMap.get(_head);
        List<String> fileNames = Utils.plainFilenamesIn(_CWD);
        Commit currCommit = _commitMap.get(currentBranchHash);
        for (String file: fileNames) {
            if (!currCommit.getCommitBlobMap().containsKey(file)) {
                System.out.println("There is an untracked file in the way; "
                       + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        String splitHash = findSplit(branchname);
        if (splitHash.equals(_branchMap.get(branchname))) {
            System.out.println("Given branch is an ancestor of "
                   + "the current branch.");
            System.exit(0);
        } else if (splitHash.equals(_branchMap.get(_head))) {
            checkout(branchname);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        Commit splitCommit = _commitMap.get(splitHash);
        Commit otherCommit = _commitMap.get(_branchMap.get(branchname));
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(otherCommit.getCommitBlobMap().keySet());
        allFiles.addAll(currCommit.getCommitBlobMap().keySet());
        boolean mergeConflict = mergeHelper(allFiles, branchname,
                splitCommit, otherCommit, currCommit);
        Calendar c = Calendar.getInstance();
        String regex = "%1$ta %1$tb %1$te %1$tT %1$tY %1$tz";
        String date = String.format(regex, c);
        commit("Merged " + branchname + " into " + _head + ".", date);
        String message = "Merged " + branchname + " into " + _head + ".";
        Commit newCommit = findCommitHelper(message);
        if (mergeConflict) {
            System.out.println("Encountered a merge conflict.");
        }
        newCommit.setSecondParent(_branchMap.get(branchname));
        _mergedMap.put(newCommit.getHash(),
                new String[] {newCommit.getFirstParent(),
                        newCommit.getSecondParent()});
        saveCommitMaps();
    }

    /**
     * Helper method for merge. Performs manipulations to files.
     * @param allFiles A list of all files that needs to be modified.
     * @param branchname The given branch's name.
     * @param splitCommit The split point's commit object.
     * @param otherCommit The other branch's head commit.
     * @param currCommit The current branch's head commit.
     * @return A boolean indicating whether a conflict happened.
     * @throws IOException Serializing Exception.
     */
    public boolean mergeHelper(Set<String> allFiles, String branchname,
                            Commit splitCommit, Commit otherCommit,
                            Commit currCommit) throws IOException {
        boolean mergeConflict = false;
        for (String file: allFiles) {
            String sHash = splitCommit.getCommitBlobMap().get(file);
            String oHash = otherCommit.getCommitBlobMap().get(file);
            String cHash = currCommit.getCommitBlobMap().get(file);
            File tempFile = Utils.join(_CWD, file);
            if (sHash == null) {
                if (cHash != null && oHash == null) {
                    continue;
                } else if (oHash != null && cHash == null) {
                    String[] args = new String[]{"checkout",
                            _branchMap.get(branchname), "--", file};
                    checkout(args);
                    add(file);
                } else if (!cHash.equals(oHash)) {
                    mergeConflictHelper(file, currCommit, otherCommit);
                    mergeConflict = true;
                }
            } else if (oHash == null) {
                if (sHash.equals(cHash)) {
                    rm(file);
                } else {
                    mergeConflictHelper(file, currCommit, otherCommit);
                    mergeConflict = true;
                }
            } else if (cHash == null) {
                if (sHash.equals(oHash)) {
                    continue;
                } else {
                    mergeConflictHelper(file, currCommit, otherCommit);
                    mergeConflict = true;
                }
            } else if (sHash.equals(cHash) && !sHash.equals(oHash)) {
                File blobFile = Utils.join(_blobs, oHash);
                String blobInfo = Utils.readContentsAsString(blobFile);
                Utils.writeContents(tempFile, blobInfo);
                add(file);
            } else if (!oHash.equals(sHash) && !oHash.equals(cHash)
                    && !cHash.equals(sHash)) {
                mergeConflictHelper(file, currCommit, otherCommit);
                mergeConflict = true;
            }
        }
        return mergeConflict;
    }


    /**
     * Helper method for merge, finds the split point.
     * @param branchname The given branch name.
     * @return A string hash for the split commit.
     */
    public String findSplit(String branchname) {
        String splitHash;
        String currentBranchHash = _branchMap.get(_head);
        String otherBranchHash = _branchMap.get(branchname);
        ArrayList<String> otherBranchPath = new ArrayList<>();
        ArrayList<String> currentBranchPath = new ArrayList<>();
        while (currentBranchHash != null) {
            currentBranchPath.add(currentBranchHash);
            currentBranchHash = _parentMap.get(currentBranchHash);
        }
        while (otherBranchHash != null) {
            otherBranchPath.add(otherBranchHash);
            otherBranchHash = _parentMap.get(otherBranchHash);
        }
        splitHash = splitHashHelper(currentBranchPath, otherBranchPath);
        if (!_mergedMap.isEmpty()) {
            HashMap<String, Integer> headAncestors = findAncestors(_head);
            HashMap<String, Integer> otherAncestors = findAncestors(branchname);
            HashMap<String, Integer> commonAncestors = new HashMap<>();
            for (String headHash: headAncestors.keySet()) {
                for (String otherHash: otherAncestors.keySet()) {
                    if (headHash.equals(otherHash)) {
                        commonAncestors.put(headHash,
                                headAncestors.get(headHash));
                    }
                }
            }
            int minimum = Integer.MAX_VALUE;
            String temp = "";
            for (String s: commonAncestors.keySet()) {
                if (commonAncestors.get(s) < minimum) {
                    minimum = commonAncestors.get(s);
                    temp = s;
                }
            }
            splitHash = temp;
        }
        return splitHash;
    }

    /**
     * Merge helper method to find and return a commit with a given message.
     * @param message The commit message.
     * @return The Commit found.
     */
    public Commit findCommitHelper(String message) {
        List<String> commitList = Utils.plainFilenamesIn(_commits);
        for (String commitHash: commitList) {
            Commit temp = _commitMap.get(commitHash);
            if (temp.getCommitMessage().equals(message)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Helper method for merge.
     * Overwrites a file with the merge conflict template.
     * @param fileName The file name.
     * @param curr The current or head commit.
     * @param other The given or other commit.
     * @throws IOException Serializing Exception.
     */
    public void mergeConflictHelper(String fileName,
                                    Commit curr, Commit other)
            throws IOException {
        File file = Utils.join(_CWD, fileName);
        Utils.restrictedDelete(file);
        file.createNewFile();
        String currBlobHash = curr.getCommitBlobMap().get(fileName);
        String currBlobInfo;
        if (currBlobHash != null) {
            File currBlobFile = Utils.join(_blobs, currBlobHash);
            currBlobInfo = Utils.readContentsAsString(currBlobFile);
        } else {
            currBlobInfo = "";
        }
        String otherBlobHash = other.getCommitBlobMap().get(fileName);
        String otherBlobInfo;
        if (otherBlobHash != null) {
            File otherBlobFile = Utils.join(_blobs, otherBlobHash);
            otherBlobInfo = Utils.readContentsAsString(otherBlobFile);
        } else {
            otherBlobInfo = "";
        }
        String toAdd = "<<<<<<< HEAD\n" + currBlobInfo
               + "=======\n" + otherBlobInfo + ">>>>>>>\n";
        Utils.writeContents(file, toAdd);
        add(fileName);
    }

    /**
     * Merge helper method.
     * Used to find the optimal split point for 2 paths.
     * @param currentPath The head branch path to the initial commit.
     * @param otherPath The other or given branch path to the initial commit.
     * @return The hash for the split point commit.
     */
    @SuppressWarnings("unchecked")
    public String splitHashHelper(ArrayList<String> currentPath,
                                  ArrayList<String> otherPath) {
        ArrayList<String> splitArray1 = (ArrayList<String>) currentPath.clone();
        ArrayList<String> splitArray2 = (ArrayList<String>) otherPath.clone();
        splitArray1.retainAll(otherPath);
        splitArray2.retainAll(currentPath);
        String split1 = splitArray1.get(0);
        String split2 = splitArray2.get(0);
        if (currentPath.indexOf(split1) >= currentPath.indexOf(split2)) {
            return split2;
        } else {
            return split1;
        }
    }

    /**
     * Merge helper method. Used when merge parents are present.
     * Finds all ancestors for a given branch.
     * @param branchName The branch name.
     * @return A HashMap that maps each ancestor hash
     * to its distance with the newest commit in the branch.
     */
    public HashMap<String, Integer> findAncestors(String branchName) {
        HashMap<String, Integer> res = new HashMap<>();
        String commitHash = _branchMap.get(branchName);
        for (int i = 0; commitHash != null; i++) {
            res.put(commitHash, i);
            commitHash = _parentMap.get(commitHash);
        }
        String newHash = _branchMap.get(branchName);
        for (int i = 0; newHash != null; i++) {
            if (_mergedMap.containsKey(newHash)) {
                String secondHash = _mergedMap.get(newHash)[1];
                for (int j = i + 1; secondHash != null; j++) {
                    res.put(secondHash, j);
                    secondHash = _parentMap.get(secondHash);
                }
            }
            newHash = _parentMap.get(newHash);
        }

        return res;
    }

    /** A file that represents the current working directory. */
    private File _CWD;

    /** The main hidden folder .gitlet to store commits and staging. */
    private File _gitlet;

    /** The blobs folder, houses all blobs. */
    private File _blobs;

    /** Hash of the current HEAD of the branch. */
    private String _head;

    /** A file that houses information about the current _head. */
    private File _headFile;

    /** The staging folder, which is the staging area when git add is used. */
    private File _staging;

    /** The current staging area. */
    private Stage _stage;

    /** A file that houses the stageAdditions HashMap. */
    private File _stageAddFile;

    /** A file that houses the stageDeletions Stack. */
    private File _stageDelFile;

    /** A file that houses the stageNames ArrayList. */
    private File _stageNamesFile;

    /** The commits folder, houses all commits. */
    private File _commits;

    /** Maps a commit hash to the Commit Object. */
    private HashMap<String, Commit> _commitMap;

    /** A file that houses the _commitMaps object. */
    private File _commitMapFile;

    /** Maps a child commit hash to its parent's. */
    private TreeMap<String, String> _parentMap;

    /** A file that houses the _parentMaps object. */
    private File _parentMapFile;

    /** Maps the branch name to the furthest commit hash of that branch. */
    private HashMap<String, String> _branchMap;

    /** A files that houses the _branchMap object. */
    private File _branchMapFile;

    /** All the branchnames so far. */
    private ArrayList<String> _branchNames;

    /** A file that houses the _branchNames ArrayList. */
    private File _branchNamesFile;

    /**
     * Maps a commit hash to a string list of parent hashes.
     * Index 0: First parent, Index 1: Second parent.
     */
    private HashMap<String, String[]> _mergedMap;

    /** A file that houses the _mergedMap object. */
    private File _mergedMapFile;

    /** The length of a commit hash. */
    private static int _commitLength = 40;

}
