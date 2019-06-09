package com.ykun.hotfix_module.utils;

import android.content.Context;

import java.io.File;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class FixDexUtils {
    //存放需要修复的dex集合
    private static HashSet<File> loadedDex = new HashSet<>();

    static {
        //修复前先清空
        loadedDex.clear();
    }

    public static void loadFixedDex(Context context) {
        if (context == null)
            return;
        //dex文件目录
        File fileDir = context.getDir("hotfix", Context.MODE_PRIVATE);
        File[] files = fileDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".dex") && !"classes.dex".equals(file.getName())) {
                //找到要修复的dex文件
                loadedDex.add(file);
            }
        }
        //创建类加载器
        createDexClassLoader(context, fileDir);
    }

    /**
     * 创建类加载器
     *
     * @param context
     * @param fileDir
     */
    private static void createDexClassLoader(Context context, File fileDir) {
        String optimizedDirectory = fileDir.getAbsolutePath() + File.separator + "opt_dex";
        File fOpt = new File(optimizedDirectory);
        if (!fOpt.exists()) {
            fOpt.mkdirs();
        }
        DexClassLoader classLoader;
        for (File dex : loadedDex) {
            //初始化类加载器
            classLoader = new DexClassLoader(dex.getAbsolutePath(), optimizedDirectory, null,
                    context.getClassLoader());
            //热修复
            hotFix(classLoader, context);
        }
    }

    /**
     * 修复核心代码
     * @param myClassLoader
     * @param context
     */
    private static void hotFix(DexClassLoader myClassLoader, Context context) {
        //系统的类加载器
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        try {
            //获取自己的 DexElements数组对象,也就是需要修复的 dex
            Object pathList = ReflectUtils.reflect(myClassLoader).field("pathList").get();
            Object myDexElements = ReflectUtils.reflect(pathList).field("dexElements").get();

            //获取系统的 dexElement 数组
            Object sysPathList = ReflectUtils.reflect(pathClassLoader).field("pathList").get();
            Object sysDexElements = ReflectUtils.reflect(sysPathList).field("dexElements").get();

            // 合并，这里利用插桩原理进行合并数组，将修复好的 class2.dex 放入第一位，优先加入就行了
            Object dexElements = ArrayUtils.combineArray(myDexElements, sysDexElements);

            //重新赋值
            ReflectUtils.reflect(sysPathList).field("dexElements", dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
