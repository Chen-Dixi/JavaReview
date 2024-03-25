package dixi.bupt.io;

import java.io.*;

public class Serialize {

    public static void main1(String[] args) throws  UnsupportedEncodingException, IOException {
        String str1 = "中文";
        byte[] bytes = str1.getBytes();
        String str2 = new String(bytes, "UTF-8");
        System.out.println(str2);

        //readFileContent("src/dixi/bupt/io/in");
    }

    public static void readFileContent(String filePath) throws IOException {

        FileReader fileReader = new FileReader(filePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        // 装饰者模式使得 BufferedReader 组合了一个 Reader 对象
        // 在调用 BufferedReader 的 close() 方法时会去调用 Reader 的 close() 方法
        // 因此只要一个 close() 调用即可
        bufferedReader.close();
    }

    public static void main(String[] args) throws Exception {
        A a1 = new A(123,"abc");
        String objectFile = "src/dixi/bupt/io/a1.txt";

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(objectFile));
        objectOutputStream.writeObject(a1);
        objectOutputStream.close();

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(objectFile));
        A a2 = (A) objectInputStream.readObject();
        objectInputStream.close();
        System.out.println(a2);
    }
}
