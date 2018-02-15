package com.stefan.ingym.ui.fragment.community.post;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.ResponseObject;
import com.stefan.ingym.pojo.mine.User;
import com.stefan.ingym.ui.fragment.community.moments.MyApplication;
import com.stefan.ingym.ui.fragment.community.moments.bean.MomentItem;
import com.stefan.ingym.ui.fragment.community.moments.bean.PhotoInfo;
import com.stefan.ingym.ui.fragment.community.post.listener.IPicURLRequestListener;
import com.stefan.ingym.ui.fragment.community.post.model.CosXmlServiceModel;
import com.stefan.ingym.ui.fragment.community.post.model.UploadGoodsBean;
import com.stefan.ingym.ui.fragment.community.post.utils.Config;
import com.stefan.ingym.ui.fragment.community.post.utils.DbTOPxUtils;
import com.stefan.ingym.ui.fragment.community.post.view.MyGridView;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.HttpUtils;
import com.stefan.ingym.util.SpUtil;
import com.stefan.ingym.util.ToastUtil;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.zzti.fengyongge.imagepicker.PhotoPreviewActivity;
import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;
import com.zzti.fengyongge.imagepicker.model.PhotoModel;
import com.zzti.fengyongge.imagepicker.util.CommonUtils;
import com.zzti.fengyongge.imagepicker.util.FileUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.R.attr.phoneNumber;


/**
 * @author fengyongge
 * @Description
 */
public class PostMomentsActivity extends AppCompatActivity {

    @ViewInject(R.id.my_goods_GV)
    private MyGridView my_imgs_GV;
    @ViewInject(R.id.tv_content)
    private EditText tv_content;

    private List<PhotoModel> single_photos = new ArrayList<PhotoModel>();
    private ArrayList<UploadGoodsBean> img_uri = new ArrayList<UploadGoodsBean>();
    // FIXME: 2018/2/10
    private List<String> paths = null;

    private int screen_widthOffset;
    GridImgAdapter gridImgsAdapter;
    private CosXmlServiceModel cosModel;
    private String momentId;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_moments);
        ViewUtils.inject(this);
        initData();
    }

    private void initData() {
        // ToolBar初始化
        Toolbar mToolbar = (Toolbar) findViewById(R.id.post_moments_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        Config.ScreenMap = Config.getScreenSize(this, this);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screen_widthOffset = (display.getWidth() - (3* DbTOPxUtils.dip2px(this, 2)))/3;

        gridImgsAdapter = new GridImgAdapter();
        my_imgs_GV.setAdapter(gridImgsAdapter);
        img_uri.add(null);          // FIXME: 2018/2/10
        gridImgsAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.tv_post})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_post:
                // 获取用户输入的动态文字
                String momentContent = tv_content.getText().toString().trim();

                // 从Sp中获取本地用户名
                String username = SpUtil.getString(getApplicationContext(), ConstantValue.IDENTIFIED_USER, null);
                // 将登陆成功的用户信息封装到User实体类中
                Gson gson = new GsonBuilder().create();
                User user = gson.fromJson(username, User.class);
                // 获取当前用户ID
                userId = user.getId();
                // 将获取到的动态文字上传到com_moment表（需要用户id，需要要保存的内容）
                uploadMomentContent(userId, momentContent);

                // 上传moment pictures 到腾讯服务器
                picArrayUpload();

//                setResult(RESULT_OK, new Intent().putExtra("moment_content", momentContent));
                finish();


                break;
        }
    }

    /**
     * 上传moment content到服务器
     * @param userId
     * @param momentContent
     */
    private void uploadMomentContent(final String userId, final String momentContent) {
        // 封装请求参数
        MomentItem momentItem = new MomentItem();
        User user = new User();
        user.setId(userId);
        momentItem.setUser(user);
        momentItem.setContent(momentContent);

        HttpUtils.doPost(ConstantValue.UPLOAD_MOMENTS, new Gson().toJson(momentItem), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                ToastUtil.show(getApplicationContext(), "发布失败！请检查网络！");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                String json = response.body().string();
                final ResponseObject<String> object = gson.fromJson(json, new TypeToken<ResponseObject<String>>() {
                }.getType());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 对解析结果进行判断（服务器端传过来的状态码为1表示成功）
                        if (object.getState() == 1) {
                            // 获取插入moment content 之后返回的 moment id
                            momentId = object.getDatas();
                        } else {
                            // 提示服务器端返回错误的信息
                            ToastUtil.show(getApplicationContext(), object.getMsg());
                        }
                    }
                });
            }
        });
    }

    /**
     * 上传动态图片到腾讯服务器
     */
    private void picArrayUpload() {

        cosModel = new CosXmlServiceModel(((MyApplication)getApplicationContext()).getCosXmlService());
        for (int i = 0; i < paths.size(); i++) {
            // 图片路径数组：[/storage/emulated/0/1518252634738.jpg, /storage/emulated/0/1518252634857.jpg, /storage/emulated/0/1518252634961.jpg]
            // 处理上传的文件名命名，截取路径最后一个"/"后面的所有字符作为fileName
            String fileName = paths.get(i).substring(paths.get(i).lastIndexOf("/") + 1);
            //上传图片
            // 第二个参数填图片的inputstream，或者 byte[]，或者本地路径
            cosModel.uploadPic(fileName, paths.get(i), new IPicURLRequestListener() {
                @Override
                public void loadSuccess(Object object) {
                    // FIXME: 2018/2/12 对返回的图片URL进行额外处理 将返回的URL保存到本地MySQL数据库 然后以动态的形式显示给移动端
                    String picURL = ((PutObjectResult)object).accessUrl;        // 处理完之后的图片URL（直接保存到数据库中去）
                    // 将返回的URL保存到本地MySQL数据库(需要传入moment的id，和图片路径)
                    savePicURL(userId, momentId, picURL);

                }
            });
        }
    }

    private void savePicURL(String userId, String momentId, String picURL) {
        // 封装请求参数
        PhotoInfo photoinfo = new PhotoInfo();
        photoinfo.setUserId(userId);
        photoinfo.setMomentId(momentId);
        photoinfo.setUrl(picURL);
        photoinfo.setW(640);    // 给定宽度
        photoinfo.setH(792);    // 给定长度


        HttpUtils.doPost(ConstantValue.UPLOAD_PIC, new Gson().toJson(photoinfo), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                ToastUtil.show(getApplicationContext(), "图片发布失败！请检查网络！");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                String json = response.body().string();
                final ResponseObject<String> object = gson.fromJson(json, new TypeToken<ResponseObject<String>>() {
                }.getType());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 对解析结果进行判断（服务器端传过来的状态码为1表示成功）
                        if (object.getState() == 1) {

                        } else {
                            // 提示服务器端返回错误的信息
                            ToastUtil.show(getApplicationContext(), object.getMsg());
                        }
                    }
                });
            }
        });
    }



    class GridImgAdapter extends BaseAdapter implements ListAdapter {
            @Override
            public int getCount() {
                return img_uri.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                convertView = LayoutInflater.from(PostMomentsActivity.this).inflate(R.layout.activity_addstory_img_item, null);

                ViewHolder holder;

                if(convertView!=null){
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(PostMomentsActivity.this).inflate(R.layout.activity_addstory_img_item,null);
                    convertView.setTag(holder);
                }else{
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.add_IB = (ImageView) convertView.findViewById(R.id.add_IB);
                holder.delete_IV = (ImageView) convertView.findViewById(R.id.delete_IV);

                AbsListView.LayoutParams param = new AbsListView.LayoutParams(screen_widthOffset, screen_widthOffset);
                convertView.setLayoutParams(param);
                if (img_uri.get(position) == null) {
                    holder.delete_IV.setVisibility(View.GONE);

                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.iv_add_the_pic, holder.add_IB);

                    holder.add_IB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(PostMomentsActivity.this, PhotoSelectorActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // 启动时不执行动画
                            intent.putExtra("limit", 9 - (img_uri.size() - 1));
                            startActivityForResult(intent, 0);
                        }
                    });

                } else {
                    ImageLoader.getInstance().displayImage("file://" + img_uri.get(position).getUrl(), holder.add_IB);

                    holder.delete_IV.setOnClickListener(new View.OnClickListener() {
                        private boolean is_addNull;
                        @Override
                        public void onClick(View arg0) {
                            is_addNull = true;
                            String img_url = img_uri.remove(position).getUrl();
                            for (int i = 0; i < img_uri.size(); i++) {
                                if (img_uri.get(i) == null) {
                                    is_addNull = false;
                                    continue;
                                }
                            }
                            if (is_addNull) {
                                img_uri.add(null);
                            }

						    FileUtils.DeleteFolder(img_url);    //删除在emulate/0文件夹生成的图片

                            gridImgsAdapter.notifyDataSetChanged();
                        }
                    });

                    holder.add_IB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("photos",(Serializable)single_photos);
                            bundle.putInt("position", position);
                            bundle.putBoolean("isSave",false);
                            CommonUtils.launchActivity(PostMomentsActivity.this, PhotoPreviewActivity.class, bundle);
                        }
                    });

                }
                return convertView;
            }

            class ViewHolder {
                ImageView add_IB;
                ImageView delete_IV;
            }
        }

    private static final String TAG = "PostMomentsActivity";

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case 0:
                    if (data != null) {
                        // FIXME: 2018/2/10
//                        List<String> paths = (List<String>) data.getExtras().getSerializable("photos");
                        //  获取从相册中选中的图片数组路径
                        // 图片路径：[/storage/emulated/0/1518252634738.jpg, /storage/emulated/0/1518252634857.jpg, /storage/emulated/0/1518252634961.jpg]
                        paths = (List<String>) data.getExtras().getSerializable("photos");


                        if (img_uri.size() > 0) {
                            img_uri.remove(img_uri.size() - 1);
                        }

                        for (int i = 0; i < paths.size(); i++) {
                            img_uri.add(new UploadGoodsBean(paths.get(i), false));
                            //上传参数
                        }
                        for (int i = 0; i < paths.size(); i++) {
                            PhotoModel photoModel = new PhotoModel();
                            photoModel.setOriginalPath(paths.get(i));
                            photoModel.setChecked(true);
                            single_photos.add(photoModel);
                        }
                        if (img_uri.size() < 9) {
                            img_uri.add(null);
                        }
                        gridImgsAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

 }