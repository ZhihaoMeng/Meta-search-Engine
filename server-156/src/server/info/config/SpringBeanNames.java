package server.info.config;

/**
 * Spring的配置文件beans.xml中配置的bean结点的id，用于以非注入的方式获取Spring的bean（java对象）
 * @author zhou
 *
 */
public class SpringBeanNames {

	final public static String CATEGORY_DAO_BEAN_NAME="CategoryDao";
	final public static String USER_DAO_BEAN_NAME="UserDao";
	final public static String QFG_DAO_BEAN_NAME="QfgGenDao";
	final public static String QUERIES_DAO_BEAN_NAME="QueriesDao";
	final public static String CLICK_LOG_DAO_BEAN_NAME="ClickLogDao";
	final public static String USER_GROUP_DAO_BEAN_NAME="UserGroupDao";
	final public static String HOTWORDS_DAO_BEAN_NAME="HotwordsDao";
	final public static String USER_INTEREST_VALUE_DAO_BEAN_NAME="UserInterestValueDao";
	final public static String Classifier_DBDao_BEAN_NAME="ClassifierDBDao";
	final public static String USER_INFO_DAO_BEAN_NAME = "UserInfoDao";
	final public static String USER_FAVOR_WORDS_DAO_BEAN_NAME ="UserFavorWordsDao";
	final public static String USER_CLUSTER_DAO_BEAN_NAME ="UserClusterDao";
	final public static String PROVINCES_DAO_BEAN_NAME = "ProvincesDao";
	final public static String CITIES_DAO_BEAN_NAME = "CitiesDao";
	final public static String COMPANY_DAO_BEAN_NAME = "CompanyDao";
	final public static String JOB_DAO_BEAN_NAME = "JobDao";
	final public static String ACA_LOG_DAO_BEAN_NAME = "AcaLogDao";
}
