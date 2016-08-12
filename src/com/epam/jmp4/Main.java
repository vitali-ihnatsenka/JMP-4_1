package com.epam.jmp4;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {

    static List<URLClassLoader> urlClassLoaders = new ArrayList<URLClassLoader>();

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
	    //Heap memory error
        List<List<List<Long>>> longlonglongList = new ArrayList<List<List<Long>>>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            List<List<Long>> longlongList= new ArrayList<List<Long>>();
            for (int j = 0; j < Integer.MAX_VALUE; j++) {
                List<Long> longList = new ArrayList<Long>();
                for (int k = 0; k < Integer.MAX_VALUE; k++) {
                    longList.add(Long.MAX_VALUE);
                }
                longlongList.add(longList);
            }
            longlonglongList.add(longlongList);
        }

        //Permgen error
        URL ioCommonsUrl = new URL("http://repo1.maven.org/maven2/commons-io/commons-io/2.4/commons-io-2.4.jar");
        URLClassLoader ioClassLoader = URLClassLoader.newInstance(new URL[]{ioCommonsUrl});
        Class clazz = ioClassLoader.loadClass("org.apache.commons.io.FileUtils");
        Method m = clazz.getMethod("copyURLToFile", URL.class, File.class);
        while(true){
            loadClassesFromUrl("http://repo1.maven.org/maven2/commons-io/commons-io/2.4/commons-io-2.4.jar", m);
            loadClassesFromUrl("http://repo1.maven.org/maven2/commons-graph/commons-graph/0.8.1/commons-graph-0.8.1.jar", m);
            loadClassesFromUrl("http://repo1.maven.org/maven2/github/com/taconaut/jtmdb/1.9.17/jtmdb-1.9.17.jar", m);
            loadClassesFromUrl("http://repo1.maven.org/maven2/ant/ant/1.5.1/ant-1.5.1.jar", m);
            loadClassesFromUrl("http://repo1.maven.org/maven2/commons-attributes/commons-attributes-api/2.2/commons-attributes-api-2.2.jar", m);
        }
    }

    private static void loadClassesFromUrl(String urlPath, Method m) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        File file = new File("d:\\temp.jar");
        URL url = new URL(urlPath);
        m.invoke(null, url, file);
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> e = jarFile.entries();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{url});
        urlClassLoaders.add(classLoader);


        while(e.hasMoreElements()){
            JarEntry je = e.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            Class c = classLoader.loadClass(className);
        }
    }
}
