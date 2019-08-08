package com.plugin.module.utils

import com.android.SdkConstants
import com.android.build.api.transform.TransformInput
import com.plugin.module.Constants
import com.plugin.module.Logger
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.Project

import javax.annotation.Nonnull
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.regex.Matcher
import java.util.regex.Pattern

class Utils {



//
//    /**
//     * 自动添加依赖，只在运行assemble任务的才会添加依赖，因此在开发期间组件之间是完全感知不到的，这是做到完全隔离的关键
//     * 支持两种语法：module或者groupId:artifactId:version(@aar),前者之间引用module工程，后者使用maven中已经发布的aar
//     *
//     * @param assembleTask
//     * @param project
//     */
//    static void compileComponents(@Nonnull Project project, @Nonnull AssembleTask assembleTask) {
//        String components;
//        if (assembleTask.isDebug) {
//            components = (String) project.getProperties().get("debugCompileComponent");
//        } else {
//            components = (String) project.getProperties().get("releaseCompileComponent");
//        }
//
//        if (components == null || components.length() == 0) {
//            System.out.println("there is no add dependencies ");
//            return;
//        }
//
//        String[] compileComponents = components.split(",");
//        if (compileComponents == null || compileComponents.length == 0) {
//            System.out.println("there is no add dependencies ");
//            return;
//        }
//        for (String str : compileComponents) {
//            System.out.println("comp is " + str);
//            str = str.trim();
//            if (str.startsWith(":")) {
//                str = str.substring(1);
//            }
//            if (isMavenArtifact(str)) {
//                /**
//                 * 示例语法:groupId:artifactId:version(@aar)
//                 * compileComponent=com.luojilab.reader:readercomponent:1.0.0
//                 * 注意，前提是已经将组件aar文件发布到maven上，并配置了相应的repositories
//                 */
//                project.getDependencies().add("implementation", str);
//                System.out.println("add dependencies lib  : " + str);
//            } else {
//                /**
//                 * 示例语法:module
//                 * compileComponent=readercomponent,sharecomponent
//                 */
//                project.getDependencies().add("implementation", project.project(':' + str));
//                System.out.println("add dependencies project : " + str);
//            }
//        }
//    }

    /**
     * 是否是maven 坐标
     *
     * @return
     */
    private static boolean isMavenArtifact(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return Pattern.matches("\\S+(\\.\\S+)+:\\S+(:\\S+)?(@\\S+)?", str);
    }

    static List<CtClass> toCtClasses(Collection<TransformInput> inputs, ClassPool classPool) {
        List<String> classNames = new ArrayList<>()
        List<CtClass> allClass = new ArrayList<>()
        inputs.each {
            it.directoryInputs.each {
                def dirPath = it.file.absolutePath
                classPool.insertClassPath(it.file.absolutePath)
                org.apache.commons.io.FileUtils.listFiles(it.file, null, true).each {
                    if (it.absolutePath.endsWith(SdkConstants.DOT_CLASS)) {
                        def className = it.absolutePath.substring(dirPath.length() + 1, it.absolutePath.length() - SdkConstants.DOT_CLASS.length()).replaceAll(Matcher.quoteReplacement(File.separator), '.')
                        if (classNames.contains(className)) {
                            throw new RuntimeException("You have duplicate classes with the same name : " + className + " please remove duplicate classes ")
                        }
                        classNames.add(className)
                    }
                }
            }

            it.jarInputs.each {
                classPool.insertClassPath(it.file.absolutePath)
                def jarFile = new JarFile(it.file)
                Enumeration<JarEntry> classes = jarFile.entries()
                while (classes.hasMoreElements()) {
                    JarEntry libClass = classes.nextElement()
                    String className = libClass.getName()
                    if (className.endsWith(SdkConstants.DOT_CLASS)) {
                        className = className.substring(0, className.length() - SdkConstants.DOT_CLASS.length()).replaceAll('/', '.')
                        if (classNames.contains(className)) {
                            throw new RuntimeException("You have duplicate classes with the same name : " + className + " please remove duplicate classes ")
                        }
                        classNames.add(className)
                    }
                }
            }
        }
        classNames.each {
            try {
                allClass.add(classPool.get(it))
            } catch (javassist.NotFoundException e) {
                println "class not found exception class name:  $it "
            }
        }
        return allClass
    }

}