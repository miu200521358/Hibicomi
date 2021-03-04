package jp.comfycolor.hibicomi.utils.bean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyBeanUtils {

	private static Logger logger = LoggerFactory.getLogger(MyBeanUtils.class);

    /**
     * Null 以外の値をコピーする
     *
     * @param dest
     * @param orig
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static void copyPropertiesIgnoreNull(Object dest, Object orig) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    	PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
    	PropertyDescriptor[] propertyDescriptors = propertyUtilsBean.getPropertyDescriptors(orig);

    	for (PropertyDescriptor pd : propertyDescriptors) {

    		if (StringUtils.equals(pd.getName(), "class")) {
    			continue;
    		}

    		try {
				Object obj = pd.getReadMethod().invoke(dest, (Object[]) null);

//	    		logger.debug("property: "+ pd.getName() + "/"+ obj);

	    		if (obj != null) {
//		    		logger.debug(obj.getClass().getName());

		    		if (obj instanceof Boolean) {
		    			logger.debug("property [boolean] copy: "+ pd.getName() + " / "+ obj);
		    			// boolean は null できないので、そのままコピー
		    			logger.debug(""+ pd);
		    			pd.getWriteMethod().invoke(orig, obj);
		    		}

		    		// Stringの場合
		    		else if (obj instanceof String) {
		    			if (StringUtils.isNotEmpty(obj.toString())) {
			    			logger.debug("property [String] copy: "+ pd.getName() + " / "+ obj);
			    			pd.getWriteMethod().invoke(orig, obj);
		    			}
		    		}

		    		// Integerの場合
		    		else if (obj instanceof Integer) {
		    			if (Integer.valueOf(obj.toString()) != 0) {
			    			logger.debug("property [Integer] copy: "+ pd.getName() + " / "+ obj);
			    			pd.getWriteMethod().invoke(orig, obj);
		    			}
		    		}

		    		// リストの場合
		    		else if (obj instanceof ArrayList) {
		    			if (((ArrayList)obj).size() > 0) {
			    			logger.debug("property [ArrayList] copy: "+ pd.getName() + " / "+ obj);
			    			pd.getWriteMethod().invoke(orig, obj);
		    			}
		    		}

		    		// マップの場合
		    		else if (obj instanceof HashMap) {
		    			if (((HashMap)obj).size() > 0) {
			    			logger.debug("property [HashMap] copy: "+ pd.getName() + " / "+ obj);
			    			pd.getWriteMethod().invoke(orig, obj);
		    			}
		    		}

		    		// 内部クラスの場合
		    		else {
		    			logger.debug("property [Class] : "+ obj.getClass() + ", "+ obj.toString());
		    			// 再帰呼び出し
		    			copyPropertiesIgnoreNull(obj, pd.getReadMethod().invoke(orig, (Object[]) null));
		    		}
	    		}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error("copyPropertiesIgnoreNull getter 失敗", e);
				throw e;
			}

    	}
    }
}