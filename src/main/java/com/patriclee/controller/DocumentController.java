package com.patriclee.controller;

import com.patriclee.splitter.MarkDownSplitter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/document")
@RestController
@AllArgsConstructor
@Slf4j
public class DocumentController {
    private final VectorStore vectorStore;

    /**
     * 嵌入文件
     *
     * @param file 待嵌入的文件
     * @return 是否成功
     */
    @SneakyThrows
    @PostMapping("/embedding")
    public Boolean embedding(@RequestParam MultipartFile file) {
        // 从IO流中读取文件
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
        // 将文本内容划分成更小的块
        List<Document> splitDocuments = new TokenTextSplitter()
                .apply(tikaDocumentReader.read());
        // 存入向量数据库，这个过程会自动调用embeddingModel,将文本变成向量再存入。
        vectorStore.add(splitDocuments);
        return true;
    }

    @SneakyThrows
    @PostMapping("/embeddingJson")
    public Boolean embeddingJson(@RequestParam MultipartFile file) {
        JsonReader jsonReader = new JsonReader(new InputStreamResource(file.getInputStream()),
                "descriptions", "content");
        // 将文本内容划分成更小的块
        List<Document> splitDocuments = new TokenTextSplitter()
                .apply(jsonReader.read());
        // 存入向量数据库，这个过程会自动调用embeddingModel,将文本变成向量再存入。
        vectorStore.add(splitDocuments);
        return true;
    }
    @SneakyThrows
    @PostMapping("/embeddingMarkDown")
    public Boolean embeddingMarkDown(@RequestParam MultipartFile file) {
        // 从IO流中读取文件
        TextReader textReader = new TextReader (new InputStreamResource(file.getInputStream()));
        textReader.getCustomMetadata().put("filename", file.getOriginalFilename());
        // 将文本内容划分成更小的块
        List<Document> splitDocuments = new MarkDownSplitter()
                .apply(textReader.read());
        // 将问题提取出来存入Metadata
        splitDocuments.forEach(document -> {
            String title = document.getContent().split("==title==")[0];
            String replace = title.replace("##", "");
            document.getMetadata().put("question", replace.trim());
        });
        vectorStore.add(splitDocuments);
        return true;
    }
    /**
     * 查询向量数据库
     *
     * @param query 用户的提问
     * @return 匹配到的文档
     */

    @GetMapping("query")
    public List<Document> query(@RequestParam String query) {
        return vectorStore.similaritySearch(query);
    }
}
