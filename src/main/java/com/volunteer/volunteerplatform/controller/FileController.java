package com.volunteer.volunteerplatform.controller;

import com.volunteer.volunteerplatform.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    // 读取 application.yml 里面配置的保存路径 (${user.dir}/files/)
    @Value("${files.upload.path}")
    private String fileUploadPath;

    /**
     * 文件上传接口
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        // 1. 获取原文件的名称 (例如: avatar.png)
        String originalFilename = file.getOriginalFilename();

        // 2. 生成一个唯一的前缀，防止别人上传同名文件把你的覆盖掉
        String flag = UUID.randomUUID().toString().replace("-", "");
        String fileName = flag + "_" + originalFilename;

        // 3. 检查保存图片的文件夹存不存在，不存在就自动创建
        File folder = new File(fileUploadPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 4. 将前端传来的文件写入到本地磁盘中
        File saveFile = new File(folder, fileName);
        file.transferTo(saveFile);

        // 5. 拼接出一个可以直接在浏览器里访问到这张图片的 URL 链接
        String url = "http://localhost:9090/file/download/" + fileName;

        // 将这个链接返回给前端，前端会自动把它赋值给 form.img 并存入数据库
        return Result.success(url);
    }

    /**
     * 文件下载/预览接口
     */
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        // 找到本地磁盘上的这张图片
        File file = new File(fileUploadPath + fileName);
        if (!file.exists()) {
            return;
        }

        // 告诉前端，这是一个需要内联显示的文件（这样图片就能直接在网页上显示，而不是变成下载文件）
        response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(fileName, "UTF-8"));

        // 读取文件字节流并返回给前端
        FileInputStream fis = new FileInputStream(file);
        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        fis.close();
        os.flush();
        os.close();
    }
}