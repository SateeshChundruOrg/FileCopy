package com.filecopy.sample;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileCopy {
	static boolean filesCopied=false;
	static List<String> failedFileNames=new ArrayList<String>();
	static List<String> replacedFiles=new ArrayList<String>();
	 public static void main(String... args) throws InterruptedException {
			System.out.println();
		 System.out.println("*** This Application copy files from source to destination folder."
		 		+ " If same files exists in source and destination and destination file older than "
		 		+ "source file then destination file will be replace with source file. *** ");
		 System.out.println();
		 Scanner input = new Scanner(System.in);
		 System.out.println("Enter the source folder path : ");
		 File folderA = new File(input.next());
		 System.out.println("Enter the destination folder path : ");
		 File folderB = new File(input.next());
		/*
		 * try { copyDir(Paths.get(folderA.getAbsolutePath()),
		 * Paths.get(folderB.getAbsolutePath())); } catch (IOException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }
		 */
		 copyFilesRecursively(folderA, folderB);
		System.out.println();
		System.out.println();
	 if( ! failedFileNames.isEmpty()) {
		 System.out.println("Following files are failed to copy to destination folder. Please run this app again or manualy copy them");
		System.out.println();
		 failedFileNames.forEach(fileName->System.out.println(fileName));
	 }
	 else if(filesCopied) { 
			System.out.println("All Files were copied.");
	}else {
			System.out.println("Everything upto date. No files to copy");
	}
		input.close();
		System.out.println();
		System.out.println("*** Completed ***");
		
		Thread.sleep(Long.MAX_VALUE);
		
	 }
	 
	 
	 private static void  copyFilesRecursively(File source, File destination) {
			Map<String,File>fileNameAndFileMapA= Stream.of(source.listFiles()).collect(Collectors.toMap(File::getName, Function.identity()));
			Map<String,File>fileNameAndFileMapB=  Stream.of(destination.listFiles()).collect(Collectors.toMap(File::getName, Function.identity()));

			fileNameAndFileMapA.entrySet().forEach(entryObj->{
				System.out.println("Keyname "+entryObj.getKey());
				File existingFleObj=fileNameAndFileMapB.get(entryObj.getKey());
				
				if(existingFleObj!=null) {
					if(existingFleObj.isDirectory()) {
						copyFilesRecursively(entryObj.getValue(),existingFleObj);
					}
					else if(entryObj.getValue().lastModified() > existingFleObj.lastModified()) {
						try {
							Files.delete(Paths.get(existingFleObj.getAbsolutePath()));
						existingFleObj.delete();
						filesCopied=true;
						copyOrReplaceFileToFolder(destination,entryObj.getValue());
						replacedFiles.add(existingFleObj.getName());
						}catch (Exception e) {
							failedFileNames.add(existingFleObj.getName());
							failedFileNames.add("Unable to replace above file because it is being used by another process in destination folder.");
							
						}
					}
				}else {
					filesCopied=true;
					Path createdPath=copyOrReplaceFileToFolder(destination,entryObj.getValue());
					if(entryObj.getValue().isDirectory() && createdPath!=null) {
						copyFilesRecursively(entryObj.getValue(),new File(createdPath.toString()));
					}
				}
			});
	 }
	 
	 private static Path copyOrReplaceFileToFolder(File targetFolder, File existingFleObj) {
       System.out.println("Copying file "+existingFleObj.getName());
      String targetFile= targetFolder.getAbsolutePath()+"\\"+existingFleObj.getName();
      String existingFile=existingFleObj.getAbsolutePath();
		Path target= Paths.get(targetFile);
        Path source= Paths.get(existingFile);
		 try {
			 ConsoleHelper.animate();
			Path path= Files.copy(source, target);
			ConsoleHelper.kill("Copied Successfully...");
			return path;
		} catch (IOException e) {
			failedFileNames.add(existingFleObj.getName());
			ConsoleHelper.kill("Failed to copy...");
			e.printStackTrace();
			return null;
		}
	}

	 public static void copyDir(Path src, Path dest) throws IOException {
			Files.walk(src)
					.forEach(source -> {
						try {
							String path=source.toString();
							System.out.println(path);
//							Files.copy(source, dest.resolve(src.relativize(source)),
//											StandardCopyOption.REPLACE_EXISTING);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
		}
	public static String getModifiedDate(long milliseconds) {
		 DateFormat format=new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
			return format.format(milliseconds);
		 
	 }

}
