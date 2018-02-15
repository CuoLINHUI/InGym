package com.stefan.ingym.ui.fragment.community.moments;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.stefan.ingym.ui.fragment.community.post.provider.LocalCredentialProvider;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;

import java.io.File;

/**
 * 
* @ClassName: MyApplication 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author yiw
* @date 2015-12-28 下午4:21:08 
*
 */
public class MyApplication extends Application {
	// 默认存放图片的路径
	public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory() + File.separator + "CircleDemo" + File.separator + "Images"
				+ File.separator;

	private static Context mContext;

	private CosXmlService cosXmlService;

	@Override
	public void onCreate() {
		super.onCreate();
		// show moments 部分
		mContext = getApplicationContext();
		//LeakCanary.install(this);
//        QPManager.getInstance(getApplicationContext()).initRecord();

		MultiDex.install(this);

		initPost();

		initCosXmlService();

	}

	public static Context getContext(){
		return mContext;
	}

	/**
	 * 初始化post moments 部分
	 */
	private void initPost() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//				.showImageOnFail(R.drawable.test)
//				.showImageOnFail(R.drawable.test)
				.cacheInMemory(true).cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions).build();

		ImageLoader.getInstance().init(config);

	}

	/**
	 * 实例化 CosXmlService 和 CosXmlServiceConfig
	 * 用于上传图片文件到腾讯云
	 */
	private void initCosXmlService() {
		/**
		 * 上传文件
		 */
		String appid = "1252749790";
		String region = "ap-shanghai";

		String secretId = "AKIDZzX1hFLGYfgV9fwKPK3aciFhN4wX4mCT";
		String secretKey ="EIPM7kHdyCFOSLeqrhMq8EMJ5eAXipmX";
		long keyDuration = 600; //SecretKey 的有效时间，单位秒

		//创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
		CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
				.setAppidAndRegion(appid, region)
				.setDebuggable(true)
				.builder();

		//创建获取签名类(请参考下面的生成签名示例，或者参考 sdk中提供的ShortTimeCredentialProvider类）
		LocalCredentialProvider localCredentialProvider = new LocalCredentialProvider(secretId, secretKey, keyDuration);

		//创建 CosXmlService 对象，实现对象存储服务各项操作.
		Context context = getApplicationContext(); //应用的上下文

		cosXmlService = new CosXmlService(context, serviceConfig, localCredentialProvider);

	}

	/**
	 * 对外提供CosXmlService
	 * @return
     */
	public CosXmlService getCosXmlService() {
		return cosXmlService;
	}
}
