package com.example.embedded.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.core.content.FileProvider;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.util.Base64Util;
import com.baidu.aip.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class FaceUtils {
    private static final String APP_ID = "19801546";
    private static final String API_KEY = "vHoVEGbE558BylGG3T4zIGhQ";
    private static final String SECRET_KEY = "C58pXfpAMFD5hQpUgRicCCyMi1lpT9Cn";

    private final static String imageType = "BASE64";
    private final static String groupId = "student";

    public static boolean searchFace(Context context) {
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        //            String filePath = getFaceImagePath(context).getPath();
//            byte[] bytes = Util.readFileByBytes(filePath);
        byte[] bytes=getScaledBitArray(getPhoto(context));
        String image = Base64Util.encode(bytes);
        String userId = UserUtils.getUserId(context);
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("user_id", userId);
        options.put("liveness_control","LOW");
//            JSONObject res = client.addUser(image, imageType, groupId, userId, null);
        JSONObject res=client.search(image,imageType,groupId,options);
        System.out.println(res.isNull("result"));
        return !res.isNull("result");
//            System.out.println(res.toString(2));
    }

    public static boolean uploadFace(Context context,Bitmap bitmap,String userId){
        if(bitmap==null)return false;
        AipFace client=new AipFace(APP_ID,API_KEY,SECRET_KEY);
        String image= Base64Util.encode(getScaledBitArray(bitmap));
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("action_type","REPLACE");
        JSONObject res=client.addUser(image,imageType,groupId,userId,options);
        try {
            if(res.getInt("error_code")==0)
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isUpload(String userId){
        AipFace client=new AipFace(APP_ID,API_KEY,SECRET_KEY);
        JSONObject res=client.faceGetlist(userId,groupId,null);
        try {
            return res.getInt("error_code")==0
                    &&res.getJSONObject("result").getJSONArray("face_list").length()>0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static byte[] getScaledBitArray(Bitmap bitmap){
        ByteArrayOutputStream scaledBitmap = new ByteArrayOutputStream();
        int options_ = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options_, scaledBitmap);
        int nowLength = scaledBitmap.toByteArray().length;
        while (nowLength > 2*1024*1024) {//循环判断如果压缩后图片是否大于2M
            scaledBitmap.reset();
            options_ = Math.max(50, options_ - 10);
            System.out.println(options_);
            bitmap.compress(Bitmap.CompressFormat.JPEG, options_, scaledBitmap);
            nowLength = scaledBitmap.toByteArray().length;
            if (options_ == 50)
                break;
        }
        return scaledBitmap.toByteArray();
    }

    public static Bitmap getPhoto(Context context) {
        FileInputStream fis= null;
        Bitmap bitmap=null;
        try {
            fis = new FileInputStream(getFaceImagePath(context).getAbsolutePath());
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private static File getFaceImagePath(Context context){
        return new File(context.getExternalCacheDir(), "face.jpg");
    }

    public static Uri getFaceImageUri(Context context) {
        return FileProvider.getUriForFile(context,
                "com.example.embedded.fileprovider",
                getFaceImagePath(context));
//        return Uri.fromFile(mPhotoFile);
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
