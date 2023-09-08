package com.aggregate.sou.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aggregate.sou.model.entity.Post;
import com.aggregate.sou.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取初始帖子列表
 *
 */
// 取消注释后，每次启动 springboot 项目时会执行一次 run 方法
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Override
    public void run(String... args) {
        // 1. 获取数据
        String url = "https://spa4.scrape.center/api/news/?limit=10&offset=0";
        String json = "limit=10&offset=0";
        String result = HttpRequest.get(url)
                .body(json)
                .execute().body();
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        System.out.println(map);
        // 2. 数据解析
        JSONArray arrays = (JSONArray) map.get("results");
        List<Post> list = new ArrayList<>();
        for (Object array : arrays) {
            JSONObject object = (JSONObject) array;
            Post post = new Post();
            try {
                post.setTitle(object.getStr("title"));
                post.setContent(object.getStr("url"));
                post.setTags(object.getStr("website"));
                post.setUserId(1L);
            } catch (Exception e) {
                log.error("传参错误：{}", e.getMessage());
                throw e;
            }

            list.add(post);
        }
        // 3. 数据入库
        boolean b = postService.saveBatch(list);
        if (b) {
            log.info("已抓取数据：{} 条", list.size());
        } else {
            log.error("数据抓取错误");
        }
    }
}