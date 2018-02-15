package com.stefan.ingym.ui.fragment.community.post.model;

import android.os.AsyncTask;

import com.stefan.ingym.ui.fragment.community.post.listener.IPicURLRequestListener;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.object.PutObjectRequest;

import java.io.InputStream;

/**
 * @ClassName:
 * @Description:
 * @Author Stefan
 * @Date
 */

public class CosXmlServiceModel {
    private static final String BUCKET = "gym";
    private static final String cosPath = "/pic/";
    private CosXmlService mService;

    public CosXmlServiceModel(CosXmlService service) {
        this.mService = service;
    }

    public void uploadPic(final String fileName, final InputStream is){
        new AsyncTask<Object, Object, Object>(){
            @Override
            protected Object doInBackground(Object... objects) {
                PutObjectRequest putObjectRequest = null;
                try {
                    putObjectRequest = new PutObjectRequest(BUCKET, cosPath + fileName, is);
                    mService.putObject(putObjectRequest);
                    return mService.putObject(putObjectRequest);

                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                    return null;
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o != null){
//                    Log.i("ggggggg", ((PutObjectResult)o).accessUrl);
                }
            }


        }.execute();

    }
    public void uploadPic(final String fileName, final byte[] data, final IPicURLRequestListener listener){
        new AsyncTask<Object, Object, Object>(){
            @Override
            protected Object doInBackground(Object... objects) {
                PutObjectRequest putObjectRequest = null;
                try {
                    putObjectRequest = new PutObjectRequest(BUCKET, cosPath + fileName, data);
                    mService.putObject(putObjectRequest);
                    return mService.putObject(putObjectRequest);
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                    return null;
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o != null){
//                    Log.i("ggggggg", ((PutObjectResult)o).accessUrl);
                    listener.loadSuccess(o);
                }
            }

        }.execute();

    }
    public void uploadPic(final String fileName, final String srcPath, final IPicURLRequestListener listener){
        new AsyncTask<Object, Object, Object>(){
            @Override
            protected Object doInBackground(Object... objects) {
                PutObjectRequest putObjectRequest = null;
                try {
                    putObjectRequest = new PutObjectRequest(BUCKET, cosPath + fileName, srcPath);
                    mService.putObject(putObjectRequest);
                    return mService.putObject(putObjectRequest);
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                    return null;
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o != null){
//                    Log.i("ggggggg", ((PutObjectResult)o).accessUrl);
                    listener.loadSuccess(o);
                }
            }


        }.execute();

    }
}
