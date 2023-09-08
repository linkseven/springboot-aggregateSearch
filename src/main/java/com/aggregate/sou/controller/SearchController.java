package com.aggregate.sou.controller;

import com.aggregate.sou.common.BaseResponse;
import com.aggregate.sou.common.ResultUtils;
import com.aggregate.sou.manager.SearchFacade;
import com.aggregate.sou.model.dto.search.SearchRequest;
import com.aggregate.sou.model.vo.SearchVO;
import com.aggregate.sou.service.PictureService;
import com.aggregate.sou.service.PostService;
import com.aggregate.sou.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 图片接口
 *
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }

}
