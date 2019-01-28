package com.example.elasticdemo.demo.utils;

import com.google.common.base.CharMatcher;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * word 文档工具
 */
public class DocumentUtil {

    /**
     * 读取word文本
     *
     * @param in
     * @param fileName
     * @return
     */
    public static String readWordFile(InputStream in, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (fileName.endsWith(".doc")) {
            HWPFDocument document = null;
            try {
                document = new HWPFDocument(in);
                WordExtractor extractor = new WordExtractor(document);
                String[] contextArray = extractor.getParagraphText();
                Arrays.stream(contextArray).forEach(s -> {
                    sb.append(s);
                });
            } catch (IOException e) {
                throw e;
            } finally {
                if (document != null) {
                    try {
                        document.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (fileName.endsWith(".docx")) {
            XWPFDocument document = null;
            try {
                document = new XWPFDocument(in).getXWPFDocument();
                List<XWPFParagraph> paragraphList = document.getParagraphs();
                paragraphList.forEach(paragraph -> sb.append(CharMatcher.whitespace().removeFrom(paragraph.getParagraphText())));
            } catch (IOException e) {
                throw e;
            } finally {
                if (document != null) {
                    try {
                        document.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * 读取PDF中的文本内容
     *
     * @param file
     * @return
     */
    public static String readPdfText(File file) throws IOException {
        PDDocument pdd = null;
        try {
            pdd = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdd);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (pdd != null) {
                pdd.close();
            }
        }
    }
    /**
     * 读取PDF中的文本内容
     *
     * @param in
     * @return
     */
    public static String readPdfText(InputStream in) throws IOException {
        PDDocument pdd = null;
        try {
            pdd = PDDocument.load(in);
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdd);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (pdd != null) {
                pdd.close();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\dingz\\Desktop\\2017广东公司办公系统功能改造及集团MSS流程对接软件开发项目-安全评估报告-初稿.docx");
        FileInputStream inputStream = new FileInputStream(file);
        System.out.println(DocumentUtil.readWordFile(inputStream, file.getName()));
    }
}
