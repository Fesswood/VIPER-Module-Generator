package com.github.fesswood.domain.Utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fesswood on 29.09.16.
 */
public class PackageUtils {

    public static String getRootPackage(Module module) throws IOException {
        VirtualFile projectManifest = getProjectManifest(module);
        if (projectManifest == null) {
            throw new IOException("Can't find projectManifest File! in root package" );
        }
        String content = new String(projectManifest.contentsToByteArray());
        Pattern pattern = Pattern.compile("package=\"(.*)\"");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    @Nullable
    public static VirtualFile getProjectManifest(Module module) {
        List<VirtualFile> sourceRoots = getSourceRoot(module);
        for (VirtualFile file : sourceRoots) {
            VirtualFile manifestFile = file.getParent().findChild("AndroidManifest.xml");
            if (manifestFile != null) {
                return manifestFile;
            }
        }
        return null;
    }

    @NotNull
    public static List<VirtualFile> getSourceRoot(Module module) {
        return ModuleRootManager.getInstance(module).getSourceRoots(JavaModuleSourceRootTypes.SOURCES);
    }

}
