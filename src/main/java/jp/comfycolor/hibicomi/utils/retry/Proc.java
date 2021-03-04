/**
 *
 */
package jp.comfycolor.hibicomi.utils.retry;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yoshiori_shoji
 *
 */
public class Proc {

	public static Logger logger = LoggerFactory.getLogger(Proc.class);

	static final Class<? extends Throwable> DEFAULT_CATCH_CLASS = Throwable.class;
	static final long DEFAULT_SLEEP_TIME = 0L;

	public static void retry(int retryCount, Runnable runnable){
		retry(retryCount, runnable, DEFAULT_CATCH_CLASS);
	}

	public static void retry(int retryCount, Runnable runnable, Class<? extends Throwable> catchClass){
		retry(retryCount, runnable, DEFAULT_SLEEP_TIME, catchClass);
	}

	public static void retry(int retryCount, Runnable runnable, long sleeptime){
		retry(retryCount, runnable, sleeptime, DEFAULT_CATCH_CLASS);
	}

	public static void retry(int retryCount, Runnable runnable, long sleeptime,
			Class<? extends Throwable> catchClass){
		if(catchClass == null){
			throw new IllegalArgumentException("catchClass is null");
		}
		if(sleeptime < 0){
			throw new IllegalArgumentException("sleeptime value is negative");
		}
		List<Throwable> throwables = new ArrayList<Throwable>();
		for(int i = 0; i < retryCount ; i++){
			try{
				runnable.run();
				return;
			} catch (Throwable e) {
				logger.warn("runnable.run() エラー", e);

				throwables.add(e);
				if(catchClass.isAssignableFrom(e.getClass())){
					try {
						Thread.sleep(sleeptime);
					} catch (InterruptedException e1) {
						throw new RetryException(e1);
					}
					continue;
				}
				throw new RetryException(e);
			}
		}

		for (Throwable t : throwables) {
			logger.warn("RetryException 回数オーバー", t);
		}

		throw new RetryException(throwables);
	}


}
