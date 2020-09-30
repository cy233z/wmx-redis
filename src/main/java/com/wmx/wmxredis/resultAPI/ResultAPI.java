package com.wmx.wmxredis.resultAPI;

import com.wmx.wmxredis.beans.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/9/30 16:49
 */
@RestController
public class ResultAPI {

    /**
     * http://localhost:8080/api/findPersons?page=5&size=20
     *
     * @param page ：页码
     * @param size ：每页显示的条数
     * @return
     */
    @GetMapping("api/findPersons")
    public ResultData findPersons(@RequestParam int page, @RequestParam int size) {
        List<Person> personList = this.getData(page, size);
        ResultData resultData = new ResultData(ResultCode.SUCCESS, personList, 356, page, size);
        return resultData;
    }

    /**
     * 模拟数据库返回数据
     *
     * @param page
     * @param size
     * @return
     */
    private List<Person> getData(int page, int size) {
        int start = (page - 1) * size + 1;
        int end = page * size;
        end = end > 365 ? 365 : end;

        List<Person> personList = new ArrayList<>(4);
        for (int i = start; i <= end; i++) {
            personList.add(new Person(i, "用户" + i, new Date()));
        }
        return personList;
    }
}
