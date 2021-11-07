
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jiafa
 * on 2021/11/5 20:58
 * 1.加载Hello.xlass文件，获取字节数组，并用255 - 字节
 * 2.反射调用hello方法
 */
public class ClassLoaderTest extends ClassLoader{
    
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ClassLoader classLoader = new ClassLoaderTest();
        Class<?> clazz = classLoader.loadClass("Hello");
        Object obj = clazz.getDeclaredConstructor().newInstance();
        Method method = clazz.getMethod("hello");
        method.invoke(obj);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String reourcePath = name.replace(".", "/");
        final String suffix = ".xlass";
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(reourcePath + suffix);
        try {
            int length = inputStream.available();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            byte[] classBytes = decode(bytes);
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] decode(byte[] bytes){
        byte[] targetBytes = new byte[bytes.length];
        for(int i = 0; i < bytes.length; i++){
            targetBytes[i] = (byte) (255 - bytes[i]);
        }
        return targetBytes;
    }
}
