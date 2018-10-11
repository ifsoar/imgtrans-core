package com.soar.analysis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
 * 文件解析工具类
 */
public class FileAnalysisUtil {
    public static Result split(File sourceFile, File dir, int limitSize, String format, String password) {
        Result result = new Result();
        try {
            SplitList splitList = new SplitList();
            splitList.setSourceName(sourceFile.getName());
            splitList.setSourceLength(sourceFile.length());
            splitList.setFormat(format);
            if (password != null && password.length() > 0) {
                splitList.setPassword(password);
            }
            String md5 = MD5Util.getFileMD5(sourceFile);
            System.out.println(md5);
            splitList.setMd5(md5);
            int splitCount = (int) (sourceFile.length() % limitSize == 0 ? sourceFile.length() / limitSize : sourceFile.length() / limitSize + 1);
            System.out.println(splitCount);
            splitList.setSplitCount(splitCount);
            FileInputStream inputStream = new FileInputStream(sourceFile);
            splitList.setItemList(new ArrayList<>());
            for (int i = 0; i < splitCount; i++) {
                long startIndex = i * limitSize;
                long length;
                if (i == splitCount - 1) {
                    length = sourceFile.length() - startIndex;
                } else {
                    length = limitSize;
                }
                System.out.println(i + ":" + sourceFile.length() + ":" + startIndex + ":" + length);
                String targetName = UUIDUtil.make();
                File targetItem = new File(dir, targetName + "." + format);
                SplitItem item = makeItem(i, inputStream, targetItem, length, format, password);
                splitList.getItemList().add(item);
            }
            inputStream.close();
            result.splitList = splitList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static SplitItem makeItem(int index, FileInputStream inputStream, File targetItem, long length, String format, String password) throws IOException {
        SplitItem item = new SplitItem();
        item.setIndex(index);
        item.setLength(length);
        int pixelNum = (int) ((length + 16 + 4 + 2 + 2) / 3);
        int imgWidth, imgHeight;
        double sqrtWidth = Math.sqrt(pixelNum);
        if (sqrtWidth % 8 == 0) {
            imgWidth = (int) sqrtWidth;
            imgHeight = imgWidth;
        } else {
            int t = (int) Math.ceil(sqrtWidth);
            imgWidth = (t / 8 + 1) * 8;
            double d = 1D * pixelNum / imgWidth;
            imgHeight = (int) Math.ceil(d);
        }
        BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        int[] pixels = new int[imgWidth];
        byte[] data = new byte[imgWidth * 3];
        int readSize = data.length;
        for (int i = 0; i < imgHeight; i++) {
            if (i == imgHeight - 1) {
                readSize = (int) (length % data.length);
            }
            clearData(data);
            inputStream.read(data, 0, readSize);
            if (password != null && password.length() > 0) {
                encodeData(data, password);
            }
            if (i == imgHeight - 1) {
                makeEndMessage(data, length, password);
            }
            fillPixels(data, readSize, pixels);
            image.setRGB(0, i, imgWidth, 1, pixels, 0, imgWidth);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        if (!targetItem.exists()) {
            targetItem.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(targetItem);
        fileOutputStream.write(outputStream.toByteArray());
        fileOutputStream.flush();
        fileOutputStream.close();
        item.setName(targetItem.getName());
        item.setMd5(MD5Util.getFileMD5(targetItem));
        return item;
    }

    private static void encodeData(byte[] data, String password) {
        String md5 = MD5Util.stringMD5(password);
        byte[] p = new byte[md5.length() / 2];
        for (int i = 0; i < p.length; i++) {
            p[i] = (byte) Integer.parseInt(md5.substring(i * 2, i * 2 + 1), 16);
        }
        for (int i = 0; i < data.length; i++) {
            data[i] ^= p[i % p.length];
        }
    }

    private static void makeEndMessage(byte[] data, long length, String password) {
        int index = data.length - 24;
        data[index] = (byte) 0xFF;
        data[index + 1] = (byte) 0x00;
        byte[] end = ByteUtil.long2Bytes(length, 4);
        data[index + 2] = end[0];
        data[index + 3] = end[1];
        data[index + 4] = end[2];
        data[index + 5] = end[3];
    }

    private static void clearData(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
    }

    private static void fillPixels(byte[] temp, int readSize, int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = ((255 & 0xFF) << 24) | (((temp[i * 3]) & 0xFF) << 16) | (((temp[i * 3 + 1]) & 0xFF) << 8) | (((temp[i * 3 + 2]) & 0xFF) << 0);
        }
    }


    public static Result merge(SplitList splitList, File dir, File targetFile) {
        try {
            List<SplitItem> splitItems = splitList.getItemList();
            if (targetFile.exists()) {
                targetFile.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            for (SplitItem item : splitItems) {
                File itemFile = new File(dir, item.getName());
                boolean ret = mergeItem(item, itemFile, fileOutputStream,splitList.getPassword());
                if (!ret) {
                    return null;
                }
            }
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean mergeItem(SplitItem item, File itemFile, FileOutputStream fileOutputStream, String password) throws IOException {
        BufferedImage image = ImageIO.read(itemFile);
        int imgWidth = image.getWidth();
        long length = item.getLength();
        int[] pixels = new int[imgWidth];
        byte[] data = new byte[imgWidth * 3];
        for (int i = 0; i < image.getHeight(); i++) {
            image.getRGB(0, i, imgWidth, 1, pixels, 0, imgWidth);
            int readSize = data.length;
            if (i == image.getHeight() - 1) {
                readSize = (int) (length - i * data.length);
            }
            fillData(data, pixels);
            encodeData(data,password);
            fileOutputStream.write(data, 0, readSize);
        }
        return true;
    }

    private static void fillData(byte[] temp, int[] array) {
        for (int i = 0; i < array.length; i++) {
            temp[i * 3] = (byte) ((array[i] >> 16) & 0xFF);
            temp[i * 3 + 1] = (byte) ((array[i] >> 8) & 0xFF);
            temp[i * 3 + 2] = (byte) ((array[i] >> 0) & 0xFF);
        }
    }
}
