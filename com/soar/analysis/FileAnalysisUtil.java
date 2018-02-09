package com.soar.analysis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * 文件解析工具类
 */
public class FileAnalysisUtil {

    public static SplitList split(File source, File baseDir, long maxLength, String format) throws IOException {
        if (!source.exists()) {
            throw new RuntimeException("source not exist:" + source.getAbsolutePath());
        }
        SplitList splitList = new SplitList();
        splitList.setFormat(format);
        splitList.setSourceLength(source.length());
        splitList.setSourceName(source.getName());

        int splitCount;
        if (source.length() % maxLength == 0) {
            splitCount = (int) (source.length() / maxLength);
        } else {
            splitCount = (int) (source.length() / maxLength) + 1;
        }
        splitList.setSplitCount(splitCount);

        List<SplitItem> splitItems = new ArrayList<>();
        long lengthOffset = 0;
        for (int i = 0; i < splitCount; i++) {
            SplitItem splitItem = new SplitItem();
            splitItem.setIndex(i);
            String uuid = UUID.randomUUID().toString() + "." + format;
            splitItem.setName(uuid);

            if (i != splitCount - 1) {
                boolean status = constructImage(
                        source,
                        new File(baseDir.getAbsolutePath() + File.separator + uuid),
                        format,
                        i,
                        lengthOffset,
                        maxLength);
                lengthOffset += maxLength;
                splitItem.setLength(maxLength);
            } else {
                boolean status = constructImage(
                        source,
                        new File(baseDir.getAbsolutePath() + File.separator + uuid),
                        format,
                        i,
                        lengthOffset,
                        source.length() - lengthOffset);
                splitItem.setLength(source.length() - lengthOffset);
            }

            splitItems.add(splitItem);
        }

        splitList.setItemList(splitItems);
        return splitList;
    }

    private static boolean constructImage(File source, File target, String format, int index, long offset, long length)
            throws IOException {
        FileInputStream fileInputStream = new FileInputStream(source);
        fileInputStream.skip(offset);
        long pixelAll;
        if (length % 3 == 0) {
            pixelAll = length / 3;
        } else {
            pixelAll = length / 3 + 1;
        }
        int width = (int) Math.ceil(Math.sqrt(pixelAll * 1.0));
        int height = width;
        if (width < 4) {
            width = 4;
            if (pixelAll % width == 0) {
                height = (int) (pixelAll / width);
            } else {
                height = (int) (pixelAll / width) + 1;
            }
        }
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] array = new int[width];
        byte[] temp = new byte[width * 3];
//        fillImageHead(array, index, length);
//        bufferedImage.setRGB(0, 0, width, 1, array, 0, width);
        for (int i = 0; i < height; i++) {
            int readSize = fileInputStream.read(temp);
            fillImageLine(temp, array);
            bufferedImage.setRGB(0, i, width, 1, array, 0, width);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, format, out);
        if (!target.exists()) {
            target.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(target);
        fileOutputStream.write(out.toByteArray());
        fileOutputStream.flush();
        fileOutputStream.close();
        fileInputStream.close();
        return true;
    }

    private static void fillImageLine(byte[] temp, int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = ((255 & 0xFF) << 24) | (((temp[i * 3]) & 0xFF) << 16) | (((temp[i * 3 + 1]) & 0xFF) << 8) | (((temp[i * 3 + 2]) & 0xFF) << 0);
        }
    }

    private static void fillImageHead(int[] array, int index, long length) {
        byte[] fileHead = new byte[12];


        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(index);
        byte[] temp = byteBuffer.array();
        for (int i = 0; i < temp.length; i++) {
            fileHead[i] = temp[i];
        }

        byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putLong(length);
        temp = byteBuffer.array();
        for (int i = 0; i < temp.length; i++) {
            fileHead[i + 4] = temp[i];
        }
        for (int i = 0; i < 4; i++) {
            array[i] = ((255 & 0xFF) << 24) | (((fileHead[i * 3]) & 0xFF) << 16) | (((fileHead[i * 3 + 1]) & 0xFF) << 8) | (((fileHead[i * 3 + 2]) & 0xFF) << 0);
        }
    }

    public static File merge(SplitList splitList, File baseDir, String prefix) throws IOException {
        File target = new File(baseDir.getAbsolutePath() + File.separator + prefix + splitList.getSourceName());
        FileOutputStream outputStream = new FileOutputStream(target);
        for (SplitItem item : splitList.getItemList()) {
            File image = new File(baseDir.getAbsolutePath() + File.separator + item.getName());
            boolean status = constructFile(outputStream, image,item.getLength());
        }
        outputStream.flush();
        outputStream.close();
        return target;
    }

    private static boolean constructFile(FileOutputStream outputStream, File image, long length) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image);
        int width = bufferedImage.getWidth();
        int[] array = new int[width];
        byte[] temp = new byte[width * 3];
//        bufferedImage.getRGB(0, 0, width, 1, array, 0, width);
//        long length = getFileLength(array);
        long offset = 0;
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            bufferedImage.getRGB(0, i, width, 1, array, 0, width);
            loadFileLine(array, temp);
            if (length - offset > temp.length) {
                offset += temp.length;
                outputStream.write(temp);
            } else if (length - offset == 0) {
                break;
            } else {
                outputStream.write(temp, 0, (int) (length - offset));
                break;
            }
        }
        return true;
    }

    private static void loadFileLine(int[] array, byte[] temp) {
        for (int i = 0; i < array.length; i++) {
            temp[i * 3] = (byte) ((array[i] >> 16) & 0xFF);
            temp[i * 3 + 1] = (byte) ((array[i] >> 8) & 0xFF);
            temp[i * 3 + 2] = (byte) ((array[i] >> 0) & 0xFF);
        }
    }

    private static long getFileLength(int[] array) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (((array[1] >> 8) & 0xFF));
        bytes[1] = (byte) (((array[1] >> 0) & 0xFF));
        bytes[2] = (byte) (((array[2] >> 16) & 0xFF));
        bytes[3] = (byte) (((array[2] >> 8) & 0xFF));
        bytes[4] = (byte) (((array[2] >> 0) & 0xFF));
        bytes[5] = (byte) (((array[3] >> 16) & 0xFF));
        bytes[6] = (byte) (((array[3] >> 8) & 0xFF));
        bytes[7] = (byte) (((array[3] >> 0) & 0xFF));
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.getLong();
    }

}
