package com.example.elasticdemo.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.elasticdemo.demo.entity.Demo;
import com.example.elasticdemo.demo.service.DemoService;
import com.example.elasticdemo.demo.utils.DocumentUtil;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.completion.Completion;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @auther: dzeb
 * @create: 2019/1/19
 */
@RestController
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    private DemoService demoService;

    @PostMapping
    public Object save(@RequestBody Demo demo) {
        demo.setId(UUID.randomUUID().toString().replace("-", ""));
        demo.setCreateDate(new Date().getTime());
        demoService.insert(demo);
        return "success";
    }

    @PostMapping("/save")
    public Object save1(Demo demo, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        System.out.println(System.currentTimeMillis());
        if (request instanceof MultipartHttpServletRequest) {
           //  System.out.println(" request is MultipartHttpServletRequest");
        }
        try {
            if (file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                demo.setMainBody(DocumentUtil.readPdfText(file.getInputStream()));
            } else if (file.getOriginalFilename().toLowerCase().endsWith(".doc") || file.getOriginalFilename().toLowerCase().endsWith(".docx")) {
                demo.setMainBody(DocumentUtil.readWordFile(file.getInputStream(), file.getOriginalFilename()));
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String temp = null;
                while ((temp = reader.readLine()) != null) {
                    sb.append(temp);
                }
                demo.setMainBody(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println("模型参数 = " + JSON.toJSONString(demo));
        demo.setId(UUID.randomUUID().toString().replace("-", ""));
        // demo.setUserId(UUID.randomUUID().toString().replace("-", "") + ",931121f7553c4fcfaab7e5e0e3ea1696");
        List<String> groupIds = new ArrayList<>();
        groupIds.add(UUID.randomUUID().toString().replace("-", ""));
        groupIds.add(UUID.randomUUID().toString().replace("-", ""));
        demo.setGroupId(groupIds);
        demo.setCreateDate(new Date().getTime());
        long start = System.currentTimeMillis();
        demoService.insert(demo);
        System.out.println("耗时:" + (System.currentTimeMillis() - start));
        return "success";
    }

    /**
     * @return
     */
    @RequestMapping("/upload")
    public Object upload(MultipartFile file) {
        Demo demo = new Demo();
        demo.setTitle(file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf(".")));
        demo.setCreateDate(new Date().getTime());
        demo.setId(UUID.randomUUID().toString().replace("-", ""));
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "GBK"));
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                System.out.println(temp);
                sb.append(temp);
            }
            demo.setMainBody(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        demoService.insert(demo);
        return "success";
    }

    @GetMapping("query")
    public Object query(@RequestParam("q") String q, @RequestParam("num") Integer num, @RequestParam("size") Integer size) {
        long start = System.currentTimeMillis();
        AggregatedPage<Demo> search = demoService.search(q, num, size);
        System.out.println("耗时:" + (System.currentTimeMillis() - start));
        return search;
    }

    @DeleteMapping("{id}")
    public Object del(@PathVariable("id") String id) {
        demoService.delete(id);
        return "success";
    }

    @RequestMapping("index")
    public Object index() {
        demoService.createIndex();
        return "success";
    }

    @GetMapping("suggest")
    public Object suggest(@RequestParam("text") String text) {
        return demoService.suggest(text);
    }

}
