package com.aggregate.sou.service.impl;

import cn.hutool.json.JSONUtil;
import com.aggregate.sou.common.ErrorCode;
import com.aggregate.sou.model.entity.Picture;
import com.aggregate.sou.service.PictureService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aggregate.sou.exception.BusinessException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片服务实现类
 *
 */
@Service
public class PictureServiceImpl implements PictureService {

    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        long searchNum =(pageNum - 1) * pageSize + 1;
        // 1. 搜索url
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", searchText, searchNum);
        // 2. 获取图片url
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取异常");
        }
        Elements images = doc.select(".iuscp.isv");
        System.out.println(images);
        List<Picture> pictureList = new ArrayList<>();
        for (Element image : images) {
            String info = image.select(".iusc").get(0).attr("m");
            Map<String, Object> urlMap = JSONUtil.toBean(info,Map.class);
            String title = image.select(".inflnk").attr("aria-label");
            // 得到图片url
            String murl = (String) urlMap.get("murl");
            Picture picture = new Picture();
            // 3. 获得图片标题
            picture.setTitle(title);
            picture.setUrl(murl);
            pictureList.add(picture);
            if (pictureList.size() >= pageSize){
                break;
            }
        }
        Page<Picture> picturePage = new Page<>();
        // 4. 返回分页查询结果
        picturePage.setRecords(pictureList);
        return picturePage;
    }
}