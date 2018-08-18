import org.csource.fastdfs.*;
import org.junit.Test;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package PACKAGE_NAME
 */

public class TestFastdfs {
    //测试上传图片
    @Test
    public void testUpload() throws Exception{
        //1.加载配置文件
        ClientGlobal.init("C:\\Users\\ThinkPad\\IdeaProjects\\33\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        //2.创建trackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //3.通过客户端获取服务端对象trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //4.先定义一个storageServer变量赋值为null

        StorageServer storageServer=null;

        //5.创建一个storageClient
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);

        //6.使用方法上传图片 下载图片 删除

        //1.参数：本地文件的路径
        //2.参数：文件的扩展名 不能带"."
        //3.参数：元数据
        String[] strings = storageClient.upload_file("C:\\Users\\ThinkPad\\Pictures\\5b4ff9deNec294aa1.jpg", "jpg", null);
        for (String string : strings) {
            System.out.println(string);
        }
    }
}
