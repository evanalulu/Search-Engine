package edu.usfca.cs272;

import java.nio.file.Path;
import java.util.HashMap;

/**
 * Parses and stores command-line arguments into simple flag/value pairs.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @author Evana Pradhan
 * @version Spring 2024
 */
public class ArgumentParser {
	private static final Path Path = null;
	/**
	 * Stores command-line arguments in flag/value pairs.
	 */
	private final HashMap<String, String> map;

	/**
	 * Initializes this argument map.
	 */
	public ArgumentParser() {
		this.map = new HashMap<>();
	}

	/**
	 * Initializes this argument map and then parsers the arguments into flag/value
	 * pairs where possible. Some flags may not have associated values. If a flag is
	 * repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public ArgumentParser(String[] args) {
		this();
		parse(args);
	}

	/**
	 * Determines whether the argument is a flag. The argument is considered a flag
	 * if it is a dash "-" character followed by any character that is not a digit
	 * or whitespace. For example, "-hello" and "-@world" are considered flags, but
	 * "-10" and "- hello" are not.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 * @see String#codePointAt(int)
	 * @see Character#isDigit(int)
	 * @see Character#isWhitespace(int)
	 */
	public static boolean isFlag(String arg) {
		return (arg == null || arg.length() < 2) ? false :
			(arg.charAt(0) == '-' && arg.charAt(1) != '	' && !Character.isDigit(arg.charAt(1)));

	}
	
	public static boolean isText(String arg) {
		return (isFlag(arg) && arg.equalsIgnoreCase("-text")) ? true : false; 
	}

	public static boolean isCounts(String arg) {
		return (isFlag(arg) && arg.equalsIgnoreCase("-counts")) ? true : false; 
	}

	/**
	 * Determines whether the argument is a value. Anything that is not a flag is
	 * considered a value.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 */
	public static boolean isValue(String arg) {
		return !isFlag(arg);
	}
	
	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may not
	 * have associated values. If a flag is repeated, its value will be overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public void parse(String[] args) {
		for (int i = 0; i < args.length; i++) {
		    String flag = args[i], value = (i + 1 < args.length) ? args[i + 1] : null;
			if (isText(flag) || isCounts(flag)) {
				if (isValue(value))
					map.put(flag.substring(1), value);
				else
					map.put(flag.substring(1), null);
			}
		}
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {
		return map.size();
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag check
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {
		return map.containsKey(flag.substring(1));
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to find
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
		return (map.get(flag.substring(1)) != null) ? true : false;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String}
	 * or the backup value if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @param backup the backup value to return if there is no mapping
	 * @return the value to which the specified flag is mapped, or the backup value
	 *   if there is no mapping
	 */
	public String getString(String flag, String backup) {
		return (hasValue(flag)) ? map.get(flag.substring(1)) : backup;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String}
	 * or null if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped or {@code null} if
	 *   there is no mapping
	 */
	public String getString(String flag) {
		return (hasValue(flag)) ? map.get(flag.substring(1)) : null;
	}

	/**
	 * Returns the value the specified flag is mapped as a {@link Path}, or the
	 * backup value if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or if no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value will be returned
	 * @param backup the backup value to return if there is no valid mapping
	 * @return the value the specified flag is mapped as a {@link Path}, or the
	 *   backup value if there is no valid mapping
	 *
	 * @see Path#of(String, String...)
	 */
	public Path getPath(String flag, Path backup) {
		String pathString = getString(flag, null);
        return (pathString != null) ? java.nio.file.Path.of(pathString) : backup;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path}, or
	 * {@code null} if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *   unable to retrieve this mapping
	 *
	 * @see #getPath(String, Path)
	 */
	public Path getPath(String flag) {
		String pathString = getString(flag, null);
        return (pathString != null) ? java.nio.file.Path.of(pathString) : null;
	}

	/**
	 * Returns the value the specified flag is mapped as an int value, or the backup
	 * value if unable to retrieve this mapping (including being unable to convert
	 * the value to an int or if no value exists).
	 *
	 * @param flag the flag whose associated value will be returned
	 * @param backup the backup value to return if there is no valid mapping
	 * @return the value the specified flag is mapped as an int, or the backup value
	 *   if there is no valid mapping
	 *
	 * @see Integer#parseInt(String)
	 */
	public int getInteger(String flag, int backup) {
	    try {
	        return Integer.parseInt(getString(flag));
	    } catch (NumberFormatException ignored) {
	        return backup;
	    }
	}

	/**
	 * Returns the value the specified flag is mapped as an int value, or 0 if
	 * unable to retrieve this mapping (including being unable to convert the value
	 * to an int or if no value exists).
	 *
	 * @param flag the flag whose associated value will be returned
	 * @return the value the specified flag is mapped as an int, or 0 if there is no
	 *   valid mapping
	 *
	 * @see #getInteger(String, int)
	 */
	public int getInteger(String flag) {
	    try {
	        return Integer.parseInt(getString(flag));
	    } catch (NumberFormatException ignored) {
	        return 0;
	    }
	}

	@Override
	public String toString() {
		return this.map.toString();
	}

	/**
	 * Demonstrates this class.
	 *
	 * @param args the arguments to test
	 */
	public static void main(String[] args) {

		if (args.length < 1) {
			args = new String[] {"-text", "world", "-counts"};
		}
		
		ArgumentParser map = new ArgumentParser(args);
		System.out.println(map);
	}
}
