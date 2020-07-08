package com.example.embedded.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.util.Base64Util;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
        try {
//            String filePath = getFaceImagePath(context).getPath();
//            byte[] bytes = Util.readFileByBytes(filePath);
            byte[] bytes=getScaledBitArray(context);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static byte[] getScaledBitArray(Context context) throws IOException {
        FileInputStream fis=new FileInputStream(getFaceImagePath(context).getAbsolutePath());
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
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
        fis.close();
        return scaledBitmap.toByteArray();
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
}
