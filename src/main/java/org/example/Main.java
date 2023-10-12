package org.example;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static Map<String, String[]> categories = new HashMap<>();

    public static void main(String[] args) {
        InitMap();
        String path = GetFolder();
        if (path == null) {
            System.err.println("Null path to folder");
            return;
        }
        SortFiles(path);
    }

    public static String GetFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(null);

        File selectedFile;
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected folder: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("No folder selected.");
            return null;
        }
        return selectedFile.getAbsolutePath();
    }

    public static void SortFiles(String path) {
        File folderPath = new File(path);
        if (!folderPath.exists() || !folderPath.isDirectory()) {
            System.err.println("Source folder doesn't exist or is not a directory.");
            return;
        }

        File[] files = folderPath.listFiles(file -> (!file.isDirectory() && file.isFile()));

        if (files != null) {

            File otherFolder = new File(folderPath, "Other");
            if (!otherFolder.exists() && !otherFolder.mkdirs()) {
                System.err.println("Failed to create 'Other' folder.");
                return;
            }

            for (File file : files) {
                String extension = getFileExtension(file);
                String category = findCategory(extension);

                if (category != null) {
                    File categoryFolder = new File(folderPath, category);
                    if (!categoryFolder.exists() && !categoryFolder.mkdirs()) {
                        System.err.println("Failed to create '" + category + "' folder.");
                        continue;
                    }

                    File newFile = new File(categoryFolder, file.getName());
                    if (file.renameTo(newFile)) {
                        System.out.println("Moved file: " + file.getName() + " to " + category);
                    } else {
                        System.err.println("Failed to move file: " + file.getName() + " to " + category);
                    }
                } else {
                    File newFile = new File(otherFolder, file.getName());
                    if (file.renameTo(newFile)) {
                        System.out.println("Moved file: " + file.getName() + " to 'Other'");
                    } else {
                        System.err.println("Failed to move file: " + file.getName() + " to 'Other'");
                    }
                }
            }
        } else {
            System.err.println("Failed to list files in the source folder.");
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "NoExtension";
    }

    private static String findCategory(String extension) {
        for (Map.Entry<String, String[]> entry : categories.entrySet()) {
            String category = entry.getKey();
            String[] extensions = entry.getValue();
            for (String ext : extensions) {
                if (extension.equals(ext)) {
                    return category;
                }
            }
        }
        return null; // Return null if no matching category is found
    }

    public static void InitMap() {
        categories.put("Media", new String[]{"mp3", "mp4", "avi", "jpg", "png", "gif"});
        categories.put("Text", new String[]{"pdf", "doc", "docx", "ppt", "pptx"});
        categories.put("Data", new String[]{"xml", "json", "xlsx"});
        categories.put("Executables", new String[]{"exe", "torrent"});

    }
}