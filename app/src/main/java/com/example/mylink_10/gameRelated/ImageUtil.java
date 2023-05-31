/*package com.example.mylink_10.gameRelated;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.mylink_10.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageUtil {
    private static List<Integer> resValues = getResValues();
    private static Bitmap CHECKBOX;

    private static List<Integer> getResValues() {
        Field[] drawableFields = R.drawable.class.getFields();
        List<Integer> ret = new ArrayList<>();
        try {
            for(Field f: drawableFields) {
                if(f.getName().startsWith("m_p")) {
                    ret.add(f.getInt(R.drawable.class));
                }
            }
            return ret;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    } // 加载图片（获取所有图片资源值）

    public static List<Integer> selValues() {
        List<Integer> ret = new ArrayList<>();
        int n = GameConf.X * GameConf.Y / 2; // 选board大小的一半
        Random random = new Random();
        for(int i = 0; i < n; i++) {
            ret.add(resValues.get(random.nextInt(resValues.size())));
        }
        ret.addAll(ret);
        return ret;
    }

    public static void attchImages(List<Piece> p, List<Integer> ids) {
        int i = 0;
        for(Integer id: ids) {
            Bitmap bm = BitmapFactory.decodeResource(GameConf.CONTEXT.getResources(), id);
            bm = Bitmap.createScaledBitmap(bm, GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT, true);
            p.get(i).setPieceImage(new PieceImage(id, bm));
            i++;
        }
    } // 获取实际的Bitmap并添加到每个piece中

    public static Bitmap getCheck() {
        if(CHECKBOX == null) {
            CHECKBOX = BitmapFactory.decodeResource(GameConf.CONTEXT.getResources(), R.drawable.selected);
            CHECKBOX = Bitmap.createScaledBitmap(CHECKBOX, GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT, true);
        }
        return CHECKBOX;
    } // 设置/返回选中框（的图片）

    public static void delCheckBox() {
        CHECKBOX = null;
    }
}*/

package com.example.mylink_10.gameRelated;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.mylink_10.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ImageUtil {
    private static List<Integer> resValues = getResValues();
    private static Bitmap CHECKBOX;

    public static List<Integer> getCppRes() {
        InputStream input;
        if(GameConf.n == 0) {
            input = GameConf.CONTEXT.getResources().openRawResource(R.raw.shenmidaima_0);
        } else if(GameConf.n == 2) {
            input = GameConf.CONTEXT.getResources().openRawResource(R.raw.shenmidaima_2);
        } else {
            input = GameConf.CONTEXT.getResources().openRawResource(R.raw.shenmidaima_1);
        }
        Reader reader = new InputStreamReader(input);
        BufferedReader bufferedReader = new BufferedReader(reader); //缓冲流
        String temp;
        List<String> txt = new LinkedList<>();
        try {
            while ((temp = bufferedReader.readLine()) != null) {
                txt.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        List<Integer> ret = new LinkedList<>();
        Random rand = new Random();
        int sel = rand.nextInt(txt.size());
        Log.i("cpp", "sel = " + sel);
        String s = txt.get(sel);
        int now = 0;
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == ' ') {
                ret.add(now);
                now = 0;
            } else {
                now = now * 10 + c - (int)'0';
            }
        }
        return ret;
    }

    public static TempNode getCppRes2() { // ImageUtil里新加
        InputStream input;
        if(GameConf.n == 0) {
            input = GameConf.CONTEXT.getResources().openRawResource(R.raw.shenmidaima_0);
        } else if(GameConf.n == 2) {
            input = GameConf.CONTEXT.getResources().openRawResource(R.raw.shenmidaima_2);
        } else {
            input = GameConf.CONTEXT.getResources().openRawResource(R.raw.shenmidaima_1);
        }
        Reader reader = new InputStreamReader(input);
        BufferedReader bufferedReader = new BufferedReader(reader); //缓冲流
        String temp;
        List<String> txt = new LinkedList<>();
        try {
            while ((temp = bufferedReader.readLine()) != null) {
                txt.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        List<Integer> ret = new LinkedList<>();
        Random rand = new Random();
        int sel = rand.nextInt(txt.size());
        Log.i("cpp", "sel = " + sel);
        String s = txt.get(sel);
        int now = 0;
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == ' ') {
                ret.add(now);
                now = 0;
            } else {
                now = now * 10 + c - (int)'0';
            }
        }
        return new TempNode(sel, ret);
    }

    public static List<Integer> getCppRes3(int id) { // ImageUtil类里新加
        InputStream input;
        if(GameConf.n == 0) {
            input = GameConf.CONTEXT.getResources().openRawResource(R.raw.shenmidaima_0);
        } else if(GameConf.n == 2) {
            input = GameConf.CONTEXT.getResources().openRawResource(R.raw.shenmidaima_2);
        } else {
            input = GameConf.CONTEXT.getResources().openRawResource(R.raw.shenmidaima_1);
        }
        Reader reader = new InputStreamReader(input);
        BufferedReader bufferedReader = new BufferedReader(reader); //缓冲流
        String temp;
        List<String> txt = new LinkedList<>();
        try {
            while ((temp = bufferedReader.readLine()) != null) {
                txt.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        List<Integer> ret = new LinkedList<>();
        Random rand = new Random();
        int sel = id;
        Log.i("cpp", "sel = " + sel);
        String s = txt.get(sel);
        int now = 0;
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == ' ') {
                ret.add(now);
                now = 0;
            } else {
                now = now * 10 + c - (int)'0';
            }
        }
        return ret;
    }

    private static List<Integer> getResValues() {
        Field[] drawableFields = R.drawable.class.getFields();
        List<Integer> ret = new ArrayList<>();
        try {
            for(Field f: drawableFields) {
                if(f.getName().contains("m_p")) {
                    ret.add(f.getInt(R.drawable.class));
                }
            }
            for(Integer num: ret) {
                System.out.println(num);
            }
            return ret;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    } // 加载图片（获取所有图片资源值）

    public static List<Integer> setSolvable(List<Integer> list) {
        List<Integer> ret = new LinkedList<>();
        for(int i = 0; i < list.size(); i++) {
            ret.add(resValues.get(list.get(i) - 1));
        }
        return ret;
    }

    public static List<Integer> selValues() {
        List<Integer> ret = new ArrayList<>();
        int n = GameConf.X * GameConf.Y / 2; // 选board大小的一半
        Random random = new Random();
        for(int i = 0; i < n; i++) {
            ret.add(resValues.get(random.nextInt(resValues.size())));
        }
        ret.addAll(ret);
        return ret;
    }

    public static void attchImages(List<Piece> p, List<Integer> ids) {
        int i = 0;
        for(Integer id: ids) {
            Bitmap bm = BitmapFactory.decodeResource(GameConf.CONTEXT.getResources(), id);
            bm = Bitmap.createScaledBitmap(bm, GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT, true);
            p.get(i).setPieceImage(new PieceImage(id, bm));
            i++;
        }
    } // 获取实际的Bitmap并添加到每个piece中

    public static Bitmap getCheck() {
        if(CHECKBOX == null) {
            CHECKBOX = BitmapFactory.decodeResource(GameConf.CONTEXT.getResources(), R.drawable.selected);
            CHECKBOX = Bitmap.createScaledBitmap(CHECKBOX, GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT, true);
        }
        return CHECKBOX;
    } // 设置/返回选中框（的图片）

    public static void delCheckBox() {
        CHECKBOX = null;
    }
}
