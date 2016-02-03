package file_operations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

public class FileOperations
{
	String workingDir = System.getProperty("user.dir") + 
						System.getProperty("file.separator"); 
	
	StringTokenizer parseCommand;

	public void delete()
	{
		File targetFile = getFile();

		if (targetFile == null)
		{
			System.out.println("Insufficient paramaters. Aborting delete operation.");
			deleteUsage();
			return;
		}
		
		if (!targetFile.exists())
		{
			System.out.println("File or directory does not exist. Aborting delete operation: \n\t" + workingDir + targetFile.toString());
			return;
		}
		
		if (targetFile.listFiles() != null && targetFile.listFiles().length > 0)
		{
			System.out.println("Directory is not empty. Aborting delete operation: \n\t" + workingDir + targetFile.toString());
			return;
		}
		
		if (targetFile.delete())
			System.out.println("File or directory successfully deleted: \n\t" + workingDir + targetFile.toString());
		else
			System.out.println("Uknown error. Unable to delete file or directory: \n\t" + workingDir + targetFile.toString());
	}

	public void rename()
	{
		File targetFile = getFile();
		File newFilename = getFile();
		
		if (targetFile == null || newFilename == null)
		{
			System.out.println("Insufficient paramaters. Aborting rename operation.");
			renameUsage();
			return;
		}

		if (!targetFile.exists())
		{
			System.out.println("File or directory does not exist. Aborting rename operation: "
					+ "\n\t" + workingDir + targetFile.toString() );
			return;
		}
		
		if (newFilename.exists())
		{
			System.out.println("New file name already exists. Can't rename to already existing file name. Aborting:\n\t"
					+ workingDir + newFilename.toString());
			return;
		}

		if (targetFile.renameTo(newFilename))
			System.out.println("File or directory successfully renamed: "
					+ "\n\t" + workingDir + targetFile.toString() + " => \n\t"
					+ workingDir + newFilename.toString());
		else
			System.out.println("Uknown error. Unable to rename file or directory: "
					+ "\n\t" + workingDir + targetFile.toString());
	}

	public void list()
	{
		File targetFile = getFile();
		File[] fileList = null;
		
		if (targetFile == null)
		{
			System.out.println("Insufficient paramaters. Aborting list operation.");
			listUsage();
			return;
		}
		
		if (!targetFile.exists())
		{
			System.out.println("File does not exist. Aborting list operation: "
					+ "\n\t" + workingDir + targetFile.toString());
			return;
		}
		
		
		if (!targetFile.isDirectory())
		{
			System.out.println("File is not a valid directory. Aborting list operation: "
					+ "\n\t" + workingDir + targetFile.toString());
			return;
		}
		
		fileList = targetFile.listFiles();
		if (fileList.length <= 0)
			System.out.println("[empty directory]");
		else
			for (File elem : fileList)
				System.out.println(elem.getName());
	}

	public void size()
	{
		File targetFile = getFile();
		long totalSize = 0;
		
		
		if (targetFile == null)
		{
			System.out.println("Insufficient paramaters. Aborting size operation.");
			sizeUsage();
			return;
		}
		
		if (!targetFile.exists())
		{
			System.out.println("File does not exist. Aborting size operation: \n\t" + workingDir + targetFile.toString());
			return;
		}
		
		totalSize = getSize(targetFile);
		
		System.out.println("The total size of the file or directory " + workingDir + targetFile.toString() + " is " + totalSize + " bytes");
	}

	public void lastModified()
	{
		File targetFile = getFile();
		Date lastModifiedTime = null;

		if (targetFile == null)
		{
			System.out.println("Insufficient paramaters. Aborting lastModified operation.");
			lastModifiedUsage();
			return;
		}
		
		if (!targetFile.exists())
		{
			System.out.println("File does not exist. Aborting lastModified operation: \n\t" + workingDir + targetFile.toString());
			return;
		}
		
		lastModifiedTime = new Date(targetFile.lastModified());
		System.out.println("File or directory " + workingDir + targetFile.toString() + " was last modified on " + lastModifiedTime.toString());
	}

	public void mkdir()
	{
		File targetFile = getFile();
		
		if (targetFile == null)
		{
			System.out.println("Insufficient paramaters. Aborting mkdir operation.");
			mkdirUsage();
			return;
		}
		if (targetFile.exists())
		{
			System.out.println("Directory already exists. Aborting mkdir operation: \n\t" + workingDir + targetFile.toString());
			return;
		}
		
		if (targetFile.mkdir())
			System.out.println("Directory created successfully: \n\t" + workingDir + targetFile.toString());
		else
			System.out.println("Uknown error creating directory. Operation failed.\n\t" + workingDir + targetFile.toString());
	}

	public void createFile()
	{
		String appendLine = "";
		File targetFile = getFile();
		PrintStream outputFile = null;
		
		if (targetFile == null)
		{
			System.out.println("Insufficient paramaters. Aborting createFile operation.");
			createFileUsage();
			return;
		}
		
		if (targetFile.exists())
		{
			System.out.println("File already exists. Aborting createFile operation: \n\t" + workingDir + targetFile.toString());
			return;
		}
		
		try
		{
			if (!targetFile.createNewFile())
				throw new IOException("Could not create file.");
			outputFile = new PrintStream(targetFile);
		} 
		catch (IOException e)
		{
			System.out.println("Error creating file. Operation failed.\n\t" 
								+ workingDir + targetFile.toString());
			return;
		}
		
		while ((appendLine = getNextToken()) != null)
			outputFile.println(appendLine);

		outputFile.close();
		
		System.out.println("File successfully created: \n\t" + workingDir + targetFile.toString());
	}

	public void printFile()
	{
		File targetFile = getFile();
		Scanner inputFile = null;

		if (targetFile == null)
		{
			System.out.println("Insufficient paramaters. Aborting size operation.");
			printFileUsage();
			return;
		}
		
		try
		{
			inputFile = new Scanner(targetFile);
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found. Aborting printFile operation: \n\t" + workingDir + targetFile.toString());
			return;
		}
		System.out.println("Printing contents of " + workingDir + targetFile.toString());
		while (inputFile.hasNextLine())
			System.out.println(inputFile.nextLine());
		inputFile.close();
	}

	void printUsage()
	{
		System.out.println("Valid commands:");
		commandsUsage();
		createFileUsage();
		printFileUsage();
		lastModifiedUsage();
		sizeUsage();
		renameUsage();
		mkdirUsage();
		deleteUsage();
		listUsage();
		quitUsage();
	}
	
	private void commandsUsage()
	{
		System.out.println("?\n\tprints this command list");
	}
	
	private void createFileUsage()
	{
		System.out.println("createFile [filename] [line 1] [line 2] [line n]\n"
				+ "\tcreates a new file named [filename] containing the data in [line] arguments");
	}
	
	private void printFileUsage()
	{
		System.out.println("printFile [filename]"
				+ "\n\tprints the contents of [filename]");
	}
	
	private void lastModifiedUsage()
	{
		System.out.println("lastModified [filename]"
				+ "\n\tprints the time and date that [filename] was last modified");
	}
	
	private void sizeUsage()
	{
		System.out.println("size [filename]"
				+ "\n\tprints the number of bytes used by [filename] (directory tree or single file)");
	}
	
	private void renameUsage()
	{
		System.out.println("rename [current_filename] [new_filename]"
				+ "\n\trenames [current_filename] to [new_filename] ");
	}
	
	private void mkdirUsage()
	{
		System.out.println("mkdir [directory_name]"
				+ "\n\tcreates a new directory named [directory_name] in current working directory");
	}
	
	private void deleteUsage()
	{
		System.out.println("delete [filename]"
				+ "\n\tdeletes [filename] if it exists");
	}
	
	private void listUsage()
	{
		System.out.println("list [directory_name]"
				+ "\n\tlist the files contained in [directory_name]");
	}
	
	private void quitUsage()
	{
		System.out.println("quit"
				+ "\n\tTerminate processing of the current command file.");
	}

	// recursive method to get total size of the element pointed to by the File object
	private long getSize(File f)
	{
		if (f == null)
			return 0;
		
		if (f.isFile())
			return f.length();
			
		long t = 0;
		File[] fileList = f.listFiles();
		
		for (File elem : fileList)
				t += getSize(elem);
		
		return t;
	}
	
	// useful private routine for getting next string on the command line
	private String getNextToken()
	{
		if (parseCommand.hasMoreTokens())
			return parseCommand.nextToken();
		else
			return null;
	}

	// useful private routine for getting a File class from the next string on
	// the command line
	private File getFile()
	{
		File f = null;
		String fileName = getNextToken();
		if (fileName != null)
			f = new File(fileName);
		return f;
	}

	public boolean processCommandLine(String line)
	{
		if (line == null)
			return false;
		
		String tempToken = "";

		System.out.println("Processing command: " + line);
		
		parseCommand = new StringTokenizer(line);
		
		// command line was empty. force an invalid command error
		if ((tempToken = getNextToken()) == null)
			tempToken = " ";
		
		switch (tempToken)
		{
		case "?":
			printUsage();
			break;
		case "createFile": 
			createFile();
			break;
		case "printFile":
			printFile();
			break;
		case "lastModified": 
			lastModified();
			break;
		case "size":
			size();
			break;
		case "rename": 
			rename();
			break;
		case "mkdir":
			mkdir();
			break;
		case "delete": 
			delete();
			break;
		case "list": 
			list();
			break;
		case "quit":
			return false;
		default:
			System.out.println("Invalid command. Please use '?' to see valid commands.");
		}
		System.out.println();
		return true;
	}

	void processCommandFile(String commandFile)
	{
		String tempStr = "";
		
		File commandInputFile = new File(commandFile);
		Scanner commandInputStream = null;

		try
		{
			commandInputStream = new Scanner(commandInputFile);
		} 
		catch (FileNotFoundException e)
		{
			System.out.println("Command file not found or does not exist. Aborting: \n\t" + workingDir + commandFile);
			return;
		}
		
		while (commandInputStream.hasNextLine())
		{
			tempStr = commandInputStream.nextLine();
			if (!processCommandLine(tempStr))
			{
				System.out.println("Quitting...");
				break;
			}
		}
		commandInputStream.close();
	}

	public static void main(String[] args)
	{
		FileOperations fo = new FileOperations();
		
		for (String elem : args)
		{
			System.out.println("\n\n============  Processing " + elem + " =======================\n");
			fo.processCommandFile(elem);
		}

		System.out.println("Done with FileOperations");
	}
}
