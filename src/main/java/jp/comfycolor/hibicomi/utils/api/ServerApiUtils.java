package jp.comfycolor.hibicomi.utils.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.utils.err.HibicomiFailureException;

public class ServerApiUtils {

	public static Logger logger = LoggerFactory.getLogger(ServerApiUtils.class);

	/**
	 * 指定URLからgetする
	 *
	 * @return
	 */
	public static String requestGetServerApi(SettingBean setting, String url) {
		return requestGetServerApi(setting, url, 0);
	}

	/**
	 * 指定URLからgetする
	 *
	 * @return
	 */
	private static String requestGetServerApi(SettingBean setting, String url, int cnt) {
		Charset charset = StandardCharsets.UTF_8;
		HttpGet request = new HttpGet(url);
		request.addHeader("Authorization", setting.getWeb().getBearerTokenHeader());

		try (CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				String responseData = EntityUtils.toString(response.getEntity(), charset);

				logger.debug("requestGetServerApi responseData: "+ responseData +", url: "+ url);

				// responseを返す
				return responseData;
			} else if (status == 429 && cnt < 5) {
				logger.warn("requestPostServerApi HTTPStatus Too Many Requests : " + status + ", url=" + url);

				try {
					Thread.sleep(3 * 60 * 1000);
				} catch (InterruptedException e) {
					logger.error("スリープ失敗", e);
				}

				return requestGetServerApi(setting, url, cnt++);
			} else {
				throw new HibicomiFailureException("requestPostServerApi HTTPStatus.NG : " + status + ", url=" + url);
			}
		} catch (IOException e) {
			throw new HibicomiFailureException("requestGetServerApi exception url=" + url, e);
		}
	}

	/**
	 * post用のパラメーターを生成する
	 *
	 * @return
	 */
	public static List<NameValuePair> createRequestParams(Object bean) {
		try {
			List<NameValuePair> requestParams = new ArrayList<>();

			for (Entry<String, String> entry : BeanUtils.describe(bean).entrySet()) {
				if (!StringUtils.isEmpty(entry.getValue())) {
					requestParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}

			return requestParams;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new HibicomiFailureException("requestPostServerApi setEntity失敗", e);
		}
	}

	/**
	 * 指定URLにpostする
	 *
	 * @return
	 */
	public static boolean requestPostServerApi(SettingBean setting, String url, List<NameValuePair> requestParams) {
		return requestPostServerApi(setting, url, requestParams, 0);
	}

	/**
	 * 指定URLにpostする
	 *
	 * @return
	 */
	private static boolean requestPostServerApi(SettingBean setting, String url, List<NameValuePair> requestParams, int cnt) {
		String responseData = requestPostServerApiString(setting, url, requestParams, cnt);

		// responseを返す
		if (!StringUtils.containsIgnoreCase(responseData, "*OK*") ) {
			throw new HibicomiFailureException("requestPostServerApi response.NG response: " + responseData + ", url=" + url);
		}

		return true;
	}

	/**
	 * 指定URLにpostする(結果をstringで取得する)
	 *
	 * @return
	 */
	public static String requestPostServerApiString(SettingBean setting, String url, List<NameValuePair> requestParams) {
		return requestPostServerApiString(setting, url, requestParams, 0);
	}

	/**
	 * 指定URLにpostする(結果をstringで取得する)
	 *
	 * @return
	 */
	private static String requestPostServerApiString(SettingBean setting, String url, List<NameValuePair> requestParams, int cnt) {
		Charset charset = StandardCharsets.UTF_8;
		HttpPost request = new HttpPost(url);
		// トークン指定
		request.addHeader("Authorization", setting.getWeb().getBearerTokenHeader());
		// 文字コード指定
		request.addHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");

		try {
			// 文字コードを指定してクエリを追加
			request.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new HibicomiFailureException("requestPostServerApi setEntity失敗", e);
		}

		try (CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
			// パラメーター設定
			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				String responseData = EntityUtils.toString(response.getEntity(), charset);

				logger.debug("requestPostServerApi responseData: "+ responseData +", url: "+ url);

				return responseData;
			} else if (status == 429 && cnt < 5) {
				logger.warn("requestPostServerApi HTTPStatus Too Many Requests : " + status + ", url=" + url);

				try {
					Thread.sleep(3 * 60 * 1000);
				} catch (InterruptedException e) {
					logger.error("スリープ失敗", e);
				}

				return requestPostServerApiString(setting, url, requestParams, cnt++);
			} else {
				throw new HibicomiFailureException("requestPostServerApi HTTPStatus.NG : " + status + ", url=" + url);
			}
		} catch (IOException e) {
			throw new HibicomiFailureException("requestPostServerApi exception url=" + url, e);
		}
	}

	/**
	 * 指定URLにpostする(JSON)
	 *
	 * @return
	 */
	public static boolean requestPostServerApiJson(SettingBean setting, String url, String jsonData) {
		return requestPostServerApiJson(setting, url, jsonData, 0);
	}

	/**
	 * 指定URLにpostする(JSON)
	 *
	 * @return
	 */
	protected static boolean requestPostServerApiJson(SettingBean setting, String url, String jsonData, int cnt) {
		Charset charset = StandardCharsets.UTF_8;
		HttpPost request = new HttpPost(url);
		// トークン指定
		request.addHeader("Authorization", setting.getWeb().getBearerTokenHeader());
		// 文字コード指定
		request.addHeader("Content-type", "application/json; charset=UTF-8");
		// エンティティにデータ設定
		request.setEntity(new StringEntity(jsonData, "UTF-8"));

		try (CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
			// パラメーター設定
			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				String responseData = EntityUtils.toString(response.getEntity(), charset);

				logger.debug("requestPostServerApi responseData: "+ responseData +", url: "+ url);

				if (!StringUtils.containsIgnoreCase(responseData, "*OK*") ) {
					throw new HibicomiFailureException("requestPostServerApi response.NG response: " + responseData + ", url=" + url);
				}

				return true;
			} else if (status == 429 && cnt < 5) {
				logger.warn("requestPostServerApi HTTPStatus Too Many Requests : " + status + ", url=" + url);

				try {
					Thread.sleep(3 * 60 * 1000);
				} catch (InterruptedException e) {
					logger.error("スリープ失敗", e);
				}

				return requestPostServerApiJson(setting, url, jsonData, cnt++);
			} else {
				throw new HibicomiFailureException("requestPostServerApi HTTPStatus.NG : " + status + ", url=" + url);
			}
		} catch (IOException e) {
			throw new HibicomiFailureException("requestPostServerApi exception url=" + url, e);
		}
	}

}
