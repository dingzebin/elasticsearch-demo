package com.example.elasticdemo.demo.controller;

import com.example.elasticdemo.demo.entity.Demo;
import com.example.elasticdemo.demo.service.DemoService;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.completion.Completion;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
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
        return demoService.search(q, num, size);
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
