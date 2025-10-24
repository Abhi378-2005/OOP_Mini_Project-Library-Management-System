package src;

import java.io.*;
import java.util.ArrayList;
import src.LibraryException.FileOperationException;

/**
 * Utility class for handling file persistence using Java's serialization.
 * All critical I/O exceptions are wrapped and re-thrown as FileOperationException.
 */
public class FileHandler {

    /**
     * Reads a list of Serializable objects from a specified file path.
     * @param <T> The type of objects in the list.
     * @param filePath The path to the file.
     * @return An ArrayList of objects read from the file. Returns an empty list if the file is new or missing.
     * @throws FileOperationException if a critical I/O or class error occurs.
     */
    public static <T> ArrayList<T> readFromFile(String filePath) throws FileOperationException {
        File file = new File(filePath);
        
        // 1. Check if the directory exists, if not, create it
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            // We safely suppress the unchecked cast warning because we expect an ArrayList
            // of the serialized type based on how we save the data.
            @SuppressWarnings("unchecked")
            ArrayList<T> data = (ArrayList<T>) ois.readObject();
            return data;

        } catch (FileNotFoundException e) {
            // This is normal for the first run or if a data file hasn't been created yet.
            // We return an empty list instead of throwing an exception.
            return new ArrayList<>();
        } catch (IOException e) {
            // Handles errors during reading, connection issues, etc.
            throw new FileOperationException("reading from " + filePath, e);
        } catch (ClassNotFoundException e) {
            // Critical error: Class definition changed or is missing.
            throw new FileOperationException("deserializing data (Class definition mismatch)", e);
        }
    }

    /**
     * Saves a list of Serializable objects to a specified file path.
     * @param <T> The type of objects in the list.
     * @param data The ArrayList of objects to save.
     * @param filePath The path to the file.
     * @throws FileOperationException if a critical I/O error occurs during saving.
     */
    public static <T> void saveToFile(ArrayList<T> data, String filePath) throws FileOperationException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            oos.writeObject(data);
            
        } catch (IOException e) {
            // Handles errors during writing, permission issues, etc.
            throw new FileOperationException("saving to " + filePath, e);
        }
    }
}
