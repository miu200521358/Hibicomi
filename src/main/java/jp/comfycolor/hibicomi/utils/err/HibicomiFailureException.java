package jp.comfycolor.hibicomi.utils.err;

import java.util.List;

public class HibicomiFailureException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	List<Throwable> throwables;

	public HibicomiFailureException(Throwable t) {
		super(t);
	}

	public HibicomiFailureException(String message) {
		super(message);
	}

	public HibicomiFailureException(String message, Throwable t) {
		super(message, t);
	}

	public HibicomiFailureException(List<Throwable> throwables) {
		this.throwables = throwables;
	}

	public List<Throwable> getThrowables() {
		return throwables;
	}

}