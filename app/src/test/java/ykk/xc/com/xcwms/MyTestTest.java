package ykk.xc.com.xcwms;

import org.junit.Test;

import java.util.List;

import ykk.xc.com.xcwms.util.JsonUtil;
import ykk.xc.com.xcwms.util.JsonUtil;

public class MyTestTest {

    @Test
    public void main() {
        String a = "{\"code\":100,\"msg\":\"处理成功\",\"extend\":{\"ykk_jsonArr\":{\"pageNum\":0,\"pageSize\":0,\"size\":5,\"startRow\":1,\"endRow\":5,\"total\":5,\"pages\":0,\"list\":[\"20161201-11\",\"20170621-12\",\"20171012-11\",\"20171110-24\",\"208507\"],\"prePage\":0,\"nextPage\":0,\"isFirstPage\":false,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":1,\"navigatepageNums\":[],\"navigateFirstPage\":0,\"navigateLastPage\":0,\"firstPage\":0,\"lastPage\":0}}}";
        List<String> list = JsonUtil.strToList2(a);
        for(int i=0; i<list.size(); i++){
            System.out.println("abc:"+ list.get(i));
        }

    }
}