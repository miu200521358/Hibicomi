package jp.comfycolor.hibicomi.utils.err;

import java.util.List;

public class HibicomiTooManyRequestsException extends HibicomiFailureException {

	public HibicomiTooManyRequestsException(Throwable t) {
		super(t);
	}

	public HibicomiTooManyRequestsException(String message) {
		super(message);
	}

	public HibicomiTooManyRequestsException(String message, Throwable t) {
		super(message, t);
	}

	public HibicomiTooManyRequestsException(List<Throwable> throwables) {
		super(throwables);
	}

}
