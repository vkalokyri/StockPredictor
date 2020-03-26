package edu.rutgers.util.exceptions;

@SuppressWarnings("serial")
public abstract class UserException extends Exception {
	public static class IllegalUserEmail extends UserException { }
	public static class IllegalUserName extends UserException { }
}
