package com.aggregate.sou.test;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aggregate.sou.model.entity.Picture;
import com.aggregate.sou.model.entity.Post;
import com.aggregate.sou.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class CrawlerTest {
    @Autowired
    private PostService postService;

    @Test
    void getPicture() throws IOException {
        // 1. 获取url
        String searchName = "二次元";
        int searchNum = 1;
        String url = String.format("https://cn.bing.com/images/search?q=%s&form=HDRSC2&first=%d", searchName, searchNum);
        Document doc = Jsoup.connect(url).get();
        String imageInfo = ".iuscp.varh.isv";
        Elements images = doc.select(imageInfo);
        List<Picture> pictureList = new ArrayList<>();
        for (Element image : images) {
            String info = image.select(".iusc").get(0).attr("m");
            Map<String, Object> urlMap = JSONUtil.toBean(info,Map.class);
            String title = image.select(".inflnk").attr("aria-label");
            String murl = (String) urlMap.get("murl");
            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);
            pictureList.add(picture);
        }
    }

    @Test
    void getEssay(){
        String url = "https://spa4.scrape.center/api/news/?limit=10&offset=0";
        String json = "limit=10&offset=0";
        String result = HttpRequest.get(url)
                .body(json)
                .execute().body();
        Map<String, Object> map = JSONUtil.toBean(result,Map.class);
        System.out.println(map);
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
            }catch (Exception e){
                log.error("传参错误：{}", e.getMessage());
                throw e;
            }

            list.add(post);
        }
        boolean b = postService.saveBatch(list);
        Assertions.assertTrue(b);
    }
}
