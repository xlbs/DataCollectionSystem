import com.xielbs.jedis.util.JedisGlobal;

import java.util.Map;

/**
 * @ClassName: JedisServiceTest
 * @Description: TODO
 * @Author: xielbs
 * @Date: 2020/12/8 15:48
 * @Version 1.0
 */
public class JedisServiceTest {

    public static void main(String[] args) {
        try {
            Map<String,String> map = JedisGlobal.DOC.queryJedisMapAllObj("docMap");
            System.out.println("====>："+map.toString());
        } catch (Exception e) {
            System.out.println("----"+e.getMessage());
            e.printStackTrace();
        }
    }

}
