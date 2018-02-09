import com.soar.analysis.FileAnalysisUtil;
import com.soar.analysis.SplitList;

import java.io.*;

public class Main {
    public static final File baseDir = new File(System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "test");

    public static void main(String[] args) throws IOException {

        //测试文件分割
       fileSplitTest();
    }

    private static void fileSplitTest() throws IOException {
        //源文件
        File source = new File(baseDir.getAbsolutePath() + File.separator + "video.mp4");
        //单张图片能够容纳的最大数据量
        long maxLength = 4 * 1024 * 1024;
        //测试将一个文件分割成多张图片
        SplitList splitList = FileAnalysisUtil.split(source, baseDir, maxLength, "png");
        //将被分割的多张图片还原成一个文件，要求图片必须是原图，未经过任何处理，压缩，转码，修改等
        File target = FileAnalysisUtil.merge(splitList, baseDir, "out_");
    }
}
