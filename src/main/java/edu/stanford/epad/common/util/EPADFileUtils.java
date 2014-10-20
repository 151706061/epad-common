/*
 * Copyright 2012 The Board of Trustees of the Leland Stanford Junior University.
 * Author: Daniel Rubin, Alan Snyder, Debra Willrett. All rights reserved. Possession
 * or use of this program is subject to the terms and conditions of the Academic
 * Software License Agreement available at:
 *   http://epad.stanford.edu/license/
 */
package edu.stanford.epad.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Utility for writing generic files with text data.
 */
public class EPADFileUtils
{
	private static final EPADLogger log = EPADLogger.getInstance();

	EPADFileUtils()
	{
	}

	/**
	 * True if the write succeed otherwise it is false.
	 * 
	 * @param file - File including full directory path to write.
	 * @param contents - String complete contents of file.
	 * @return boolean
	 */
	public static boolean write(File file, String contents)
	{
		try {
			Writer out = new BufferedWriter(new FileWriter(file));
			out.write(contents);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			log.warning("Failed to write file: " + file.getAbsolutePath(), e);
			return false;
		}
	}

	/**
	 * User this instead of write when a file will be over-written often.
	 * 
	 * @param file File the file to over-write.
	 * @param contents String
	 * @return boolean
	 */
	public static boolean overwrite(File file, String contents)
	{
		String tempFilename = file.getAbsoluteFile() + "." + UUID.randomUUID().toString() + ".tmp";
		File tempFile = new File(tempFilename);

		return write(tempFile, contents) && tempFile.renameTo(file);
	}

	@SuppressWarnings("unchecked")
	public static Collection<File> getAllFilesWithExtension(File dir, String extension, boolean recursive)
	{
		String[] extensions = new String[] { extension };

		return FileUtils.listFiles(dir, extensions, recursive);
	}

	public static List<File> getAllFilesWithoutExtension(File dir, final String extension)
	{
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file)
			{
				return !file.getName().endsWith(extension);
			}
		});
		if (files != null) {
			return Arrays.asList(files);
		} else {
			return new ArrayList<File>(); // return an empty list of no files.
		}
	}

	/**
	 * Get all the directories under the file specified. If it isn't a directory then
	 * 
	 * @param dir File
	 * @return List of File
	 */
	public static List<File> getDirectoriesIn(File dir)
	{
		List<File> retVal = new ArrayList<File>();
		try {
			if (!dir.isDirectory()) {
				throw new IllegalArgumentException("Not a directory!!");
			}

			File[] candidates = dir.listFiles();
			for (File curr : candidates) {
				if (curr.isDirectory()) {
					retVal.add(curr);
				}
			}
		} catch (Exception e) {
			log.warning("Had:" + e.getMessage() + " for " + dir.getAbsolutePath(), e);
		}
		return retVal;
	}

	public static int countFilesWithEnding(String dirPath, String ending)
	{
		return countFilesWithEnding(new File(dirPath), ending);
	}

	/**
	 * Count the total number of files in a directory with a give ending. Recursively descend down the directory
	 * structure.
	 * 
	 * @param dir File
	 * @param ending String
	 * @return int total files with this ending.
	 */
	public static int countFilesWithEnding(File dir, final String ending)
	{
		if (!dir.isDirectory()) {
			throw new IllegalStateException("Not a directory: " + dir.getAbsolutePath());
		}

		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file)
			{
				return file.getName().toLowerCase().endsWith(ending);
			}
		});
		if (files == null) {
			return 0;
		}
		int count = files.length;
		for (File currFile : files) {
			if (currFile.isDirectory()) {
				count += countFilesWithEnding(currFile, ending);
			}
		}

		return count;
	}

	/**
	 * 
	 * @param f File
	 * @return String
	 */
	public static String getExtension(File f)
	{
		if (!f.exists()) {
			throw new IllegalStateException("File does not exists. file: " + f.getAbsolutePath());
		}

		String name = f.getName().toLowerCase();
		int dot = name.lastIndexOf('.');
		return name.substring(dot + 1);
	}

	/**
	 * Return true if the file doesn't have an extension.
	 * 
	 * @param f File
	 * @return boolean
	 */
	public static boolean hasExtension(File f)
	{
		if (!f.exists()) {
			throw new IllegalStateException("File does not exists. file: " + f.getAbsolutePath());
		}

		String name = f.getName().toLowerCase();
		int dot = name.lastIndexOf('.');

		return dot > 0;
	}

	/**
	 * Looks for an extension, which is the last dot '.' in a file name, but only if the characters are alpha to
	 * distinguish it from DicomUIDs.
	 * 
	 * @param f File ex. ./dir1/dir2/SomeFileName.ext
	 * @return String - ./dir1/dir2/SomeFileName
	 */
	public static String fileAbsolutePathWithoutExtension(File f)
	{
		String fullPath;
		try {
			fullPath = f.getCanonicalPath();
		} catch (IOException ioe) {
			fullPath = f.getAbsolutePath();
		}
		return removeExtension(fullPath);
	}

	/**
	 * Looks for an extension, which is the last dot '.' in a file name, but only if the characters are alpha to
	 * distinguish it from DicomUIDs.
	 * 
	 * @param f File ex. ./dir1/dir2/SomeFileName.ext
	 * @return String - SomeFileName
	 */
	public static String fileNameWithoutExtension(File f)
	{
		return removeExtension(f.getName());
	}

	/**
	 * Unzip the specified file.
	 * 
	 * @param zipFilePath String path to zip file.
	 * @throws IOException during zip or read process.
	 */
	public static void extractFolder(String zipFilePath) throws IOException
	{
		ZipFile zipFile = null;

		try {
			int BUFFER = 2048;
			File file = new File(zipFilePath);

			zipFile = new ZipFile(file);
			String newPath = zipFilePath.substring(0, zipFilePath.length() - 4);

			makeDirs(new File(newPath));
			Enumeration<?> zipFileEntries = zipFile.entries();

			while (zipFileEntries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)zipFileEntries.nextElement();
				String currentEntry = entry.getName();
				File destFile = new File(newPath, currentEntry);
				File destinationParent = destFile.getParentFile();

				// create the parent directory structure if needed
				makeDirs(destinationParent);

				InputStream is = null;
				BufferedInputStream bis = null;
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;

				try {
					if (!entry.isDirectory()) {
						int currentByte;
						byte data[] = new byte[BUFFER];
						is = zipFile.getInputStream(entry);
						bis = new BufferedInputStream(is);
						fos = new FileOutputStream(destFile);
						bos = new BufferedOutputStream(fos, BUFFER);

						while ((currentByte = bis.read(data, 0, BUFFER)) != -1) {
							bos.write(data, 0, currentByte);
						}
						bos.flush();
					}
				} finally {
					IOUtils.closeQuietly(bis);
					IOUtils.closeQuietly(is);
					IOUtils.closeQuietly(bos);
					IOUtils.closeQuietly(fos);
				}

				if (currentEntry.endsWith(".zip")) {
					extractFolder(destFile.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			log.warning("Failed to unzip: " + zipFilePath, e);
			throw new IllegalStateException(e);
		} finally {
			if (zipFile != null)
				zipFile.close();
		}
	}

	/**
	 * Check to see if the file exists. If it doesn't then move it to the new location.
	 * 
	 * @param from File - The file in the old location.
	 * @param to File - The file in the new location.
	 * @throws IOException during move.
	 */
	public static void checkAndMoveFile(File from, File to) throws IOException
	{
		// If the directory doesn't exist create it.
		File parentDir = to.getParentFile();
		if (!parentDir.exists()) {
			EPADFileUtils.makeDirs(parentDir);
		}

		if (!to.exists()) {
			// log.info("Moving file to: "+to.getCanonicalPath());
			FileUtils.moveFile(from, to);
		} else {
			// log.info("Not moving file since it already exists. file="+to.getCanonicalPath());
			FileUtils.deleteQuietly(from);
		}
	}// checkAndMove

	/**
	 * Make a directory in a thread-safe manner in case multiple threads try to make it at the same time.
	 * 
	 * @param dir File the directory(ies) to make.
	 * @return boolean true if the process was successful, or the directory already exists.
	 */
	public static boolean makeDirs(File dir)
	{
		// make a key based on the name.
		FileKey lock = new FileKey(dir);

		synchronized (lock.toString()) {
			if (!dir.exists()) {
				return dir.mkdirs();
			}
		}
		return true;
	}

	public static boolean createDirsAndFile(File file)
	{
		if (file.exists()) return true;
		boolean success = makeDirs(file.getParentFile());
		if (!success) {
			return false;
		}

		FileKey lock = new FileKey(file);
		synchronized (lock.toString()) {
			if (!file.exists()) {
				try {
					return file.createNewFile();
				} catch (IOException ioe) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Delete a directory and all of its contents.
	 * 
	 * @param directoryToDelete File
	 * @return boolean true if everything deleted.
	 */
	public static boolean deleteDirectoryAndContents(File directoryToDelete)
	{
		try {
			List<File> dirs = getDirectoriesIn(directoryToDelete);
			for (File currDir : dirs) {
				if (!deleteDirectoryAndContents(currDir)) {
					throw new IllegalStateException("Filed to delete dir=" + directoryToDelete.getAbsolutePath());
				}
			}
			File[] files = directoryToDelete.listFiles();
			for (File currFile : files) {
				if (!currFile.delete()) {
					throw new IllegalStateException("Could not delete file=" + currFile.getAbsolutePath());
				}
			}
			if (!directoryToDelete.delete()) {
				throw new IllegalStateException("Could not delete: " + directoryToDelete.getAbsolutePath());
			}
			return true;

		} catch (IllegalStateException ise) {
			log.warning("Warning: error deleting directory " + directoryToDelete.getAbsolutePath(), ise);
			return false;
		} catch (Exception e) {
			log.warning("Warning: error deleting directory " + directoryToDelete.getAbsolutePath(), e);
			return false;
		}
	}

	public static boolean deleteFilesInDirWithoutExtension(File dir, String extension)
	{
		try {
			List<File> files = getAllFilesWithoutExtension(dir, extension);

			for (File file : files) {
				if (!file.delete()) {
					throw new IllegalStateException("Could not delete file=" + file.getAbsolutePath());
				}
			}
			return true;
		} catch (IllegalStateException ise) {
			log.warning(ise.getMessage());
			return false;
		} catch (Exception e) {
			log.warning("Had: " + e.getMessage() + " for " + dir.getAbsolutePath(), e);
			return false;
		}
	}

	public static boolean deleteFilesInDirectoryWithExtension(File dir, String extension)
	{
		try {
			Collection<File> files = getAllFilesWithExtension(dir, extension, true);

			for (File file : files) {
				if (!file.delete()) {
					throw new IllegalStateException("Could not delete file " + file.getAbsolutePath());
				}
			}
			return true;
		} catch (IllegalStateException ise) {
			log.warning(ise.getMessage());
			return false;
		} catch (Exception e) {
			log.warning("Had: " + e.getMessage() + " for " + dir.getAbsolutePath(), e);
			return false;
		}
	}
	
    public static File copyFile(File src, File dst)
    {
    	FileChannel inChannel = null;
    	FileChannel outChannel = null;
		try {
	        inChannel = new FileInputStream(src).getChannel();
	        outChannel = new FileOutputStream(dst).getChannel();
	        inChannel.transferTo(0, inChannel.size(), outChannel);
			return dst;
		} catch (Exception e) {
			log.warning("Error copying file, from " + src.getAbsolutePath() + " to " + dst.getAbsolutePath(), e);
		} finally {
			try {
	            if (inChannel != null) inChannel.close();
	            if (outChannel != null) outChannel.close();
			} catch (IOException e) {}
		}
		return null;
    }


	/**
	 * 
	 * @param name String
	 * @return String
	 */
	private static String removeExtension(String name)
	{
		int lastDotIndex = name.lastIndexOf('.');
		if (lastDotIndex < 1) {
			return name;
		}

		String ext = name.substring(lastDotIndex);
		ext = ext.replace('.', ' ').trim();
		if (isNumber(ext)) {
			return name;
		}

		return name.substring(0, lastDotIndex);
	}

	private static boolean isNumber(String checkForNumber)
	{

		for (int i = 0; i < checkForNumber.length(); i++) {
			// If we find a non-digit character we return false.
			if (!Character.isDigit(checkForNumber.charAt(i)))
				return false;
		}

		return true;
	}

}
