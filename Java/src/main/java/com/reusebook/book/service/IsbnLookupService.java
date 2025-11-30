package com.reusebook.book.service;

import com.reusebook.book.dto.IsbnLookupResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ISBN 查询服务：当前以内置数据模拟第三方接口
 * 对于未知ISBN，生成通用占位信息供用户手动修改
 */
@Service
public class IsbnLookupService {

    private static final Map<String, IsbnLookupResponse> MOCK_DATA = new HashMap<>();

    static {
        // 计算机类
        MOCK_DATA.put("9787115428028", new IsbnLookupResponse(
                "9787115428028",
                "深入理解计算机系统",
                "Randal E. Bryant",
                "机械工业出版社",
                2016,
                "https://example.com/csapp.jpg"
        ));
        MOCK_DATA.put("9787111213826", new IsbnLookupResponse(
                "9787111213826",
                "Java核心技术 卷I",
                "Cay S. Horstmann",
                "机械工业出版社",
                2018,
                "https://example.com/java-core.jpg"
        ));
        MOCK_DATA.put("9787115546081", new IsbnLookupResponse(
                "9787115546081",
                "JavaScript高级程序设计（第4版）",
                "Matt Frisbie",
                "人民邮电出版社",
                2020,
                "https://example.com/js-pro.jpg"
        ));
        MOCK_DATA.put("9787115417305", new IsbnLookupResponse(
                "9787115417305",
                "算法（第4版）",
                "Robert Sedgewick",
                "人民邮电出版社",
                2012,
                "https://example.com/algorithms.jpg"
        ));
        // 文学类
        MOCK_DATA.put("9787508660751", new IsbnLookupResponse(
                "9787508660751",
                "解忧杂货店",
                "东野圭吾",
                "南海出版公司",
                2014,
                "https://example.com/jyou.jpg"
        ));
        MOCK_DATA.put("9787544291163", new IsbnLookupResponse(
                "9787544291163",
                "活着",
                "余华",
                "作家出版社",
                2012,
                "https://example.com/huozhe.jpg"
        ));
        MOCK_DATA.put("9787020024759", new IsbnLookupResponse(
                "9787020024759",
                "三体",
                "刘慈欣",
                "重庆出版社",
                2008,
                "https://example.com/santi.jpg"
        ));
        // 教材类
        MOCK_DATA.put("9787040396638", new IsbnLookupResponse(
                "9787040396638",
                "高等数学（第七版）上册",
                "同济大学数学系",
                "高等教育出版社",
                2014,
                "https://example.com/gaoshu.jpg"
        ));
        MOCK_DATA.put("9787040396645", new IsbnLookupResponse(
                "9787040396645",
                "高等数学（第七版）下册",
                "同济大学数学系",
                "高等教育出版社",
                2014,
                "https://example.com/gaoshu2.jpg"
        ));
        MOCK_DATA.put("9787302671491", new IsbnLookupResponse(
                "9787302671491",
                "软件工程导论",
                "张海藩",
                "清华大学出版社",
                2023,
                "https://example.com/software.jpg"
        ));
    }

    public Optional<IsbnLookupResponse> lookup(String rawIsbn) {
        if (rawIsbn == null || rawIsbn.isBlank()) {
            return Optional.empty();
        }
        String normalized = normalize(rawIsbn);
        
        // 先从模拟数据中查找
        IsbnLookupResponse cached = MOCK_DATA.get(normalized);
        if (cached != null) {
            return Optional.of(cached);
        }
        
        // 对于未知ISBN，生成占位信息让用户手动填写
        // 生产环境可替换为调用第三方ISBN API
        if (normalized.length() >= 10 && normalized.length() <= 13) {
            return Optional.of(new IsbnLookupResponse(
                    normalized,
                    "请手动填写书名",
                    "请手动填写作者",
                    "未知出版社",
                    2024,
                    null
            ));
        }
        
        return Optional.empty();
    }

    private String normalize(String isbn) {
        return isbn.replaceAll("[- ]", "").toLowerCase();
    }
}
