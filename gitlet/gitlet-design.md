# Gitlet Design Document
author: Adrian Kwan

## 1. Classes and Data Structures

### Stage
This class will help track all the files that are a part of the staging area following a git add.
#### Fields
1. Hashmap <String, String> _stageAdditions: All the files that need to be added in the next commit tracked by a hashmap containing the filename and the hash.
2. Stack <String> _stageDeletions: All the files that have to be deleted at the next commit, tracked as strings of file names.
3. ArrayList<String> _fileNames: All the file names of the files that have been modified according to the stage.


### Commit
This class will represent a commit based on their metadata and their references to previous/parent commits.
#### Fields
1. String _message: Contains the message for the commit.
2. String _timestamp: Contains the date and time of the commit as a String.
3. Commit _self: Reference to this specific commit.
4. Commit _parent: Reference to this specific commit's parent.
5. Hashmap <String, Blob> _commitblobmaps: The data structure that will map each filename to a blob.


### Blob
This class will represent the Blob objects that each commit will map to when it's referring to a file.
#### Fields
1. String _info: This will be the string representation of everything in the file.
2. String _file: This will be the file name that corresponds to the blob instance.
3. File BLOB_FOLDER: This will point to the location of the .gitlet/blobs folder.
4. String _hash: The hash of the current blob instance.

### Git
This class is the central repository that will be housed in CWD/.gitlet and will allow the user to perform gitlet-related actions.
#### Fields
1. File _CWD: Tracks the file path of the current user working directory.
2. File _gitlet: Tracks the file path of .gitlet, allows the use of Utils.join when creating new paths.
3. File _staging: Tracks the file path of the staging area, allows the movement of git added files into this folder.
4. File _blobs: Tracks the blobs of information of a certain file. * Not sure if it's necessary, however, put it down here for now. *
5. File _commits: Tracks the file path for all the commits, allows us to retrace to previous commits and/or create new ones in the directory.
6. String _head: Tracks the hashcode of the current head of the branch.
7. File _headFile: Tracks the file that includes the hash of the current head. Used for persistence.
8. String _master: Trackers the hashcode of the current master of the branch
9. File _masterFile: Tracks the file that includes the hash of the current master branch. Used for persistence.
10. Stage _stage: The staging area of the repository.
11. File _stageAddFile: Tracks the file that includes the serialized _stageAdditions HashMap. Used for persistence.
12. File _stageDelFile: Tracks the file that includes the serialized _stageDeletions Stack. Used for persistence. 
13. File _stageNamesFile: Tracks the file that includes the serialized _stageFileNames ArrayList. Used for persistence.
14. HashMap <String, Commit> _commitMap: Maps the hash of the commit (the commit ID) to the commit object.
15. File _commitMapFile: Tracks the file that houses the serialized version of the _commitMap object.
16. TreeMap <String, String> _parentMap: Maps the current hash of the commit to its parent's hash. The initial commit has a parent hash of attribute null.
17. File _parentMapFile: Tracks the file that houses the serialized version of the _parentMap object.

## 2. Algorithms

### Stage Class
#### Constructors
1. ***Stage()***: The default constructor for the stage class. Initializes the data structures outlined above to new instances.

#### Getter Methods
2. ***getAdd()***: Returns the _stageAdditions hashmap.
3. ***getDel()***: Returns the _stageDeletions stack.
4. ***getFileNames()***: Returns the fileNames arraylist.

#### Setter Methods
5. ***Setter(HashMap <String, String> add, Stack <String> del, ArrayList <String> names)***: Updates the private variables for this stage instance. Used when readStage() is called in the Git class, which is called when the repository object is initialized at each call to gitlet.g

#### Other Methods
6. ***add()***: Adds the file name to the fileNames arraylist to be iterated over later and also adds a mapping of the file name to the blob hash, which is the name of a blob file in the .gitlet/blobs directory.
7. ***rm()***: Adds the file name to the stageDeletions stack. 
8. ***clear()***: Clears our data structures. To be used following a commit. 

### Commit Class

#### Constructors
1. ***Commit()***: A default constructor for the commit class. This is used for creating the initial commit when git init is called.
2. ***Commit(String m, String t, String p, HashMap<String, String>b))***: This is the non-default constructor for commit that will allow me to set the private variables mentioned above to whatever is passed into this constructor.

#### Getter Methods
3. ***getHash(), getParentHash()***: Simply returns the private attributes _self and _parent. This may be used by other classes to help us locate where files are physically in the commits folder.
4. ***getCommitMessage()***: Returns the message associated with the current commit.
5. ***getCommitTimeStamp()***: Returns the timestamp associated with the current commit.

#### Other Methods
6. ***createHash()***: This function works by serializing the instance variable commit into a byte array and then applying the SHA-1 hash on it.
7. ***putCommitBlobMap(String fileName, String blobHash)***: Updates the _commitblobmap for an instance of a commit so that a file's name is mapped to the blob stored in .gitlet/blobs.
8. ***copy(Commit c, String message, String date)***: Creates a new Commit object that has the same information as the Commit C given, apart from the commit message and date. This should be used when creating a new commit. 
9. ***updateHash()***: Updates the _hash attribute of the current Commit. This should be used following the call of Commit.copy(...) and Commit.putCommitBlobMap(...).

### Blob Class

#### Constructors
1. ***Blob(String file)*** : Initializes a blob by taking in the file name. Also initializes the _info, _file, and _hash instance variables.

#### Getter Methods
2. ***getHash()***: Returns the hash associated with this blob instance.
3. ***getBlobFile()***: Returns the file name associated with this blob instance.
4. ***getInfo()***: Returns the contents inside the file that this blob instance is representing as a string.

#### Other methods
5. ***createBlobHash()***: Hashes the file name as well as the string representation of the information stored inside the given file and returns the hash (SHA-1).
6. ***SaveBlob(Blob b)***: Creates a new blob under the BLOB_FOLDER directory with the name of its own hash.

### Git Class
#### Constructors
1. ***Git()***: This function will read in all the serialized objects from the last call and updates the local private variables and objects such as _stage and _commitMap with the information stored in the .gitlet directory. This is achieved by calling numerous helper methods outlined below that will set up the current repository object. This is ran every time a new call to gitlet happens.

#### Serialization Helper Methods
2. ***readCommit(String hash)***: This method will return the Commit saved in ./commits with the given hash.
3. ***writeCommit(Commit c, String hash)***: This method will serialize and save the Commit c given into the method and store it under the ./commits folder.
4. ***saveHeadAndMaster()***: Saves the current _head and _master hashes into their respective files in the .gitlet directory.
5. ***readHeadAndMaster()***: Reads the .gitlet/head and .gitlet/master files and updates the private hashes _head and _master in the current repository object.
6. ***saveCommitMaps()***: Serializes and saves the current _commitMap and _parentMap into their respective files.
7. ***readCommitMaps()***: Sets the private variables _commitMap and _parentMap to the serialized object stored in the .gitlet directory. 
8. ***writeStage()***: Serializes and saves the current _stage object and its data structures under .gitlet/staging.
9. ***readStage()***: Reads in the data structures stored under .gitlet/staging and updates the _stage object by calling the Stage.setter(...) method.

#### Gitlet Methods

10. ***setupInit()***: This function is ran in the gitlet.Main function after init is called through the terminal. It will create directories including the main .gitlet folder that will house all of our commits, staged files, and blobs. It also initializes a new Stage and the initial commit as well as sets the _head and _master attributes to the initial commit. Then, the new _stage, _head, _master, _commitMap, _parentMap strings/objects are serialized and saved under their respective folders to be persisted.
11. ***add(String file)***: This function is ran in the gitlet.Main function after gitlet add is called. After checking that the number of arguments is correct, it will first check if the file name exists in our CWD. Then, it will initialize a new blob that takes in the file name (see above for implementation details). It will then check if the current commit's blobmap contains the same hash as the new blob's hash, which will indicate whether or not the new blob's information already exists. If it does, it will then destage in _stage. If not, it is added to _stage through the _stage.add(...) method. Finally, Blob.SaveBlob(...) is called to put the blob in the ./blobs directory. Finally, the writeStage(Stage) is called to serialized the current state of the stage into its respective file. 
12. ***commit(String message, String Date)***: The message is passed in through args[1] in main and the Date is collected in the Main function and passed in as a string. Then the new Commit's parent commit (the _head Commit) is initialized and a copy is created for the representation of the new Commit. The newCommit's blobmaps is then modified as necessary. The new Commit's will then be updated and the _stage is cleared. Following this, the new Commit is serialized and saved. The _parentMap and the _head attributes are also updated in the process. Finally, saveCommitMaps() and saveHeadAndMaster() are both called to serialized the new updated commit as well as the new _head and _master hashes into their respective files under .gitlet.
13. ***log()***: During setupInit(), the initial commit's parent in _parentMap is set to null. Hence, this function will iterate through all the commit hashes stored in _parentMap until the hash becomes null. During each iteration, it will print out the necessary information by creating a temporary Commit object by using the _commitMaps object, and then updates the hashcode to the value of the _parentMaps object by passing in the current hashcode, effectively working as a linked list. 
14. ***globallog()***: A list of commit hashes is found by the Utils.plainFilenamesIn function. Then, the function iterates through all commit hashes and prints out the required information. 
15. ***rm(String f)***: This function will destage the file if the file is currently in _stageAdditions, and will add onto the _stageDeletions if the file is tracked by the current commit. It will then perform a restricted delete if the file is in the current CWD. 
16. ***checkout(String... args)***: This function overwrites the file in the CWD depending on what file is passed into it and which commitID is passed into it. 
17. ***checkout(String branchName)***: An overloading of the function above that is called when a branch name is passed into checkout in Main.
18. ***find(String message)***: This function will iterate through all the commit hashes found by the Utils.plainFilenamesIn method and will compare their respective commit objects and their messages with the message passed in. If they are equal, it will print out the commit hash. If none, throws error.





## 3. Persistence

* First, the current .gitlet directory will include three separate directories. The first one is the /Blobs directory, which will store all the blobs of information from each file. The second is /staging, which will house the staging area, which is cleared following each commit. Lastly, the Commit folder will house all the serialized commit objects to be accessed at any point through checkout or log.
* The first object that should be serialized and saved are the blobs in the gitlet add call.
* Following each add call, the stage should also be saved under the ./staging folder.
* Following this, in the Commit call, each new Commit object is saved in ./commits after the initialization and the updating of the object.
* The blobs are the most basic type of object to be persisted as it keeps track of the information stored in the files under the CWD. By persisting the stage, if a situation where the user calls gitlet add and then doesn't commit but then returns later ever happens, the current files staged for addition or deletion will still be tracked. Every time a Commit object is persisted, it will not only store the metadata and hashes associated with that specific object, but also the blobmap associated with that Commit object. This could be then used in a situation where a user commits then quits, and then returns to the gitlet repository some time later and will still be able to access the information in his commit history.
* This should then guarantee that the state of the program across multiple runs will be conserved. By using hashmaps that primarily deal with hashes and then storing objects with the same name as their hash in our directory, we should have a system where information can be accessed at any time.


## 4. Design Diagram

Attach a picture of your design diagram illustrating the structure of your
classes and data structures. The design diagram should make it easy to 
visualize the structure and workflow of your program.

