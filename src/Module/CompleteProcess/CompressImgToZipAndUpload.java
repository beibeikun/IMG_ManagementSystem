package Module.CompleteProcess;

import Module.CheckOperations.SystemChecker;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static Module.FileOperations.DeleteDirectory.deleteDirectory;
import static Module.FileOperations.FileCopyAndDelete.copyFile;
import static Module.FileOperations.FileCopyAndDelete.copyFiles;
import static Module.Others.SystemPrintOut.systemPrintOut;
import static Module.FileOperations.CreateTemporaryDestinationFolder.createTemporaryDestinationFolder;
import static Module.DataOperations.FileLister.getFileNames;
import static Module.DataOperations.FileNameProcessor.processFileNames;
import static Module.DataOperations.FileSearch.searchFiles;
import static Module.CompressOperations.CompressFileList.compressFiles;
import static Module.CompressOperations.ImageCompression.imageCompression;
/**
 * 图片压缩上传类，封装了完整的图片压缩上传过程。
 */
public class CompressImgToZipAndUpload {
    /**
     * 完成图片压缩上传的整个过程。
     *
     * @param sourceFolder            源文件夹路径
     * @param destinationFolder       目标文件夹路径
     * @param mode                    1.相机图 其他.手机图
     * @throws IOException 如果文件操作失败
     */
    public static void compressImgToZipAndUpload(String sourceFolder,String destinationFolder,int mode) throws IOException, ImageProcessingException, MetadataException {
        systemPrintOut("Start to upload", 3, 0);
        SystemChecker system = new SystemChecker();

        //创建一个临时文件夹来储存压缩包
        String temporaryDestinationFolder = createTemporaryDestinationFolder(sourceFolder,"TemporaryCompression");
        //缩略图路径
        String thumbnailFolder = destinationFolder + system.identifySystem_String() + "thumbnail";
        //mode为1，检查缩略图路径
        if (mode == 1)
        {

            // 创建缩略图路径文件夹对象
            File thumbFolder = new File(thumbnailFolder);

            // 如果缩略图路径文件夹不存在，则创建
            if (!thumbFolder.exists()) {
                File directory = new File(thumbnailFolder);
                directory.mkdirs();
            }
        }
        //获取源文件夹内所有文件名,处理文件名数组，去除文件后缀名、去除 "(x)" 后缀并删除重复项，只保留一个
        String[] FileNames = processFileNames(getFileNames(sourceFolder));
        for (int i = 0; i < FileNames.length; i++) {
            //mode为1，上传缩略图
            if (mode == 1)
            {
                copyFile(sourceFolder + system.identifySystem_String() + FileNames[i] + ".JPG", thumbnailFolder);
                imageCompression(thumbnailFolder + system.identifySystem_String() + FileNames[i] + ".JPG", 2500);
                systemPrintOut("Thumbnail upload:" + FileNames[i], 1, 0);
            }
            //获取同一前缀的文件列表
            List<File> readytocompress = searchFiles(sourceFolder,FileNames[i]);
            //压缩同一前缀的文件
            compressFiles(readytocompress,temporaryDestinationFolder + system.identifySystem_String() +FileNames[i]+".zip");
            systemPrintOut("Compressed:" + FileNames[i]+".zip", 1, 0);
        }
        //把压缩包从临时文件夹移动到目标文件夹并按前缀分类
        copyFiles(temporaryDestinationFolder, destinationFolder, 6);
        //删除临时文件夹
        deleteDirectory(new File(temporaryDestinationFolder)); //删除临时文件夹
    }
}
